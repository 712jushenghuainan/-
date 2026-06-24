from flask import Flask, render_template, request, jsonify, session
import pymysql

app = Flask(__name__)
app.secret_key = 'your-secret-key-12345'

# ============ 数据库配置（使用成员A的 swim_club） ============
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'swim_club',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}

def get_db():
    return pymysql.connect(**DB_CONFIG)

# ============ 页面路由 ============
@app.route('/')
def index():
    return render_template('base.html')

@app.route('/disable')
def disable_page():
    return render_template('disable.html')

@app.route('/system')
def system_page():
    return render_template('system.html')

# ============ 登录接口 ============
@app.route('/api/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')
    
    db = get_db()
    cursor = db.cursor()
    cursor.execute(
        "SELECT user_id, username, role FROM t_user WHERE username=%s AND password_hash=%s",
        (username, password)
    )
    user = cursor.fetchone()
    db.close()
    
    if user:
        session['user'] = user
        return jsonify({'success': True, 'user': user})
    else:
        return jsonify({'success': False, 'message': '账号或密码错误'})

@app.route('/api/logout', methods=['POST'])
def logout():
    session.clear()
    return jsonify({'success': True})

@app.route('/api/current_user', methods=['GET'])
def current_user():
    user = session.get('user')
    if user:
        return jsonify({'success': True, 'user': user})
    return jsonify({'success': False, 'message': '未登录'})

# ============ 禁用/解禁接口 ============

@app.route('/api/card/disable', methods=['POST'])
def disable_card():
    """禁用会员卡"""
    if 'user' not in session:
        return jsonify({'success': False, 'message': '请先登录'})
    
    role = session['user'].get('role')
    if role not in ['admin', 'cashier']:
        return jsonify({'success': False, 'message': '权限不足，需要管理员或收银员权限'})
    
    data = request.json
    card_id = data.get('card_id')
    reason = data.get('reason', '')
    
    if not card_id:
        return jsonify({'success': False, 'message': '卡号不能为空'})
    
    db = get_db()
    cursor = db.cursor()
    try:
        # 1. 更新卡状态为'禁用'
        cursor.execute(
            "UPDATE t_card SET card_status='禁用' WHERE card_id=%s",
            (card_id,)
        )
        
        # 2. 插入状态变更日志
        cursor.execute(
            "INSERT INTO t_card_status_log (card_id, operator_id, action_type, reason) VALUES (%s, %s, '禁用', %s)",
            (card_id, session['user']['user_id'], reason)
        )
        
        db.commit()
        return jsonify({'success': True, 'message': '禁用成功'})
    except Exception as e:
        db.rollback()
        return jsonify({'success': False, 'message': str(e)})
    finally:
        db.close()

@app.route('/api/card/enable', methods=['POST'])
def enable_card():
    """解禁会员卡"""
    if 'user' not in session:
        return jsonify({'success': False, 'message': '请先登录'})
    
    role = session['user'].get('role')
    if role not in ['admin', 'cashier']:
        return jsonify({'success': False, 'message': '权限不足'})
    
    data = request.json
    card_id = data.get('card_id')
    
    if not card_id:
        return jsonify({'success': False, 'message': '卡号不能为空'})
    
    db = get_db()
    cursor = db.cursor()
    try:
        # 1. 更新卡状态为'正常'
        cursor.execute(
            "UPDATE t_card SET card_status='正常' WHERE card_id=%s",
            (card_id,)
        )
        
        # 2. 插入状态变更日志
        cursor.execute(
            "INSERT INTO t_card_status_log (card_id, operator_id, action_type, reason) VALUES (%s, %s, '解禁', '')",
            (card_id, session['user']['user_id'])
        )
        
        db.commit()
        return jsonify({'success': True, 'message': '解禁成功'})
    except Exception as e:
        db.rollback()
        return jsonify({'success': False, 'message': str(e)})
    finally:
        db.close()

@app.route('/api/card/search', methods=['GET'])
def search_card():
    """查询会员卡（关联t_member显示姓名）"""
    keyword = request.args.get('keyword', '')
    
    db = get_db()
    cursor = db.cursor()
    try:
        if keyword:
            sql = """
                SELECT c.card_id, c.card_status, c.balance, m.name AS member_name
                FROM t_card c
                JOIN t_member m ON c.member_id = m.member_id
                WHERE c.card_id LIKE %s OR m.name LIKE %s
            """
            like_keyword = f'%{keyword}%'
            cursor.execute(sql, (like_keyword, like_keyword))
        else:
            sql = """
                SELECT c.card_id, c.card_status, c.balance, m.name AS member_name
                FROM t_card c
                JOIN t_member m ON c.member_id = m.member_id
            """
            cursor.execute(sql)
        
        results = cursor.fetchall()
        return jsonify({'success': True, 'data': results})
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)})
    finally:
        db.close()

# ============ 系统管理接口 ============

@app.route('/api/config', methods=['GET'])
def get_config():
    """获取系统参数"""
    if 'user' not in session:
        return jsonify({'success': False, 'message': '请先登录'})
    
    db = get_db()
    cursor = db.cursor()
    try:
        cursor.execute("SELECT config_key, config_value, description FROM sys_config")
        configs = cursor.fetchall()
        return jsonify({'success': True, 'data': configs})
    except:
        return jsonify({'success': True, 'data': [
            {'config_key': 'pool_name', 'config_value': '阳光游泳馆', 'description': '游泳馆名称'},
            {'config_key': 'open_time', 'config_value': '09:00-21:00', 'description': '营业时间'}
        ]})
    finally:
        db.close()

@app.route('/api/config/update', methods=['POST'])
def update_config():
    """更新系统参数（仅管理员）"""
    if 'user' not in session:
        return jsonify({'success': False, 'message': '请先登录'})
    
    if session['user'].get('role') != 'admin':
        return jsonify({'success': False, 'message': '仅管理员可修改系统参数'})
    
    data = request.json
    db = get_db()
    cursor = db.cursor()
    try:
        for key, value in data.items():
            cursor.execute(
                "UPDATE sys_config SET config_value=%s WHERE config_key=%s",
                (value, key)
            )
        db.commit()
        return jsonify({'success': True, 'message': '参数更新成功'})
    except Exception as e:
        db.rollback()
        return jsonify({'success': False, 'message': str(e)})
    finally:
        db.close()

@app.route('/api/password/change', methods=['POST'])
def change_password():
    """修改密码"""
    if 'user' not in session:
        return jsonify({'success': False, 'message': '请先登录'})
    
    data = request.json
    old_password = data.get('old_password')
    new_password = data.get('new_password')
    user_id = session['user']['user_id']
    
    db = get_db()
    cursor = db.cursor()
    cursor.execute(
        "SELECT * FROM t_user WHERE user_id=%s AND password_hash=%s",
        (user_id, old_password)
    )
    user = cursor.fetchone()
    if not user:
        db.close()
        return jsonify({'success': False, 'message': '原密码错误'})
    
    try:
        cursor.execute(
            "UPDATE t_user SET password_hash=%s WHERE user_id=%s",
            (new_password, user_id)
        )
        db.commit()
        return jsonify({'success': True, 'message': '密码修改成功'})
    except Exception as e:
        db.rollback()
        return jsonify({'success': False, 'message': str(e)})
    finally:
        db.close()

# ============ 启动应用 ============
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)