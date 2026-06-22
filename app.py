from flask import Flask, render_template, request, jsonify, session
import pymysql
import hashlib

app = Flask(__name__)
app.secret_key = 'your-secret-key-12345'  # 用于session加密

# ============ 数据库配置 ============
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',  # ⚠️ 改成你的MySQL密码！
    'database': 'swimming_pool',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}

def get_db():
    """获取数据库连接"""
    return pymysql.connect(**DB_CONFIG)

# ============ 页面路由 ============

@app.route('/')
def index():
    """首页"""
    return render_template('base.html')

@app.route('/disable')
def disable_page():
    """禁用/解禁管理页面"""
    return render_template('disable.html')

@app.route('/system')
def system_page():
    """系统管理页面"""
    return render_template('system.html')

# ============ 登录接口 ============

@app.route('/api/login', methods=['POST'])
def login():
    """用户登录"""
    data = request.json
    username = data.get('username')
    password = data.get('password')
    
    db = get_db()
    cursor = db.cursor()
    cursor.execute(
        "SELECT user_id, username, role FROM sys_users WHERE username=%s AND password=%s",
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
    """退出登录"""
    session.clear()
    return jsonify({'success': True})

@app.route('/api/current_user', methods=['GET'])
def current_user():
    """获取当前登录用户"""
    user = session.get('user')
    if user:
        return jsonify({'success': True, 'user': user})
    return jsonify({'success': False, 'message': '未登录'})

# ============ 禁用/解禁接口 ============

@app.route('/api/card/disable', methods=['POST'])
def disable_card():
    """禁用会员卡"""
    # 检查是否登录
    if 'user' not in session:
        return jsonify({'success': False, 'message': '请先登录'})
    
    # 检查权限（管理员和收银员可以操作）
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
        # 检查卡是否存在（注意：cards表可能还没创建，先跳过检查）
        # 直接记录禁用日志
        cursor.execute(
            "INSERT INTO disable_logs (card_id, action, reason, operator) VALUES (%s, 'disable', %s, %s)",
            (card_id, reason, session['user']['username'])
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
        cursor.execute(
            "INSERT INTO disable_logs (card_id, action, operator) VALUES (%s, 'enable', %s)",
            (card_id, session['user']['username'])
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
    """查询会员卡（模拟数据，因为cards表还没创建）"""
    keyword = request.args.get('keyword', '')
    
    # 因为cards表还没创建，先返回模拟数据
    # 等成员A创建了cards表后，这里可以改成真实查询
    mock_data = [
        {'card_id': 'C001', 'member_name': '张三', 'status': 'active'},
        {'card_id': 'C002', 'member_name': '李四', 'status': 'active'},
        {'card_id': 'C003', 'member_name': '王五', 'status': 'disabled'},
    ]
    
    # 根据关键词过滤
    if keyword:
        mock_data = [d for d in mock_data if keyword in d['card_id'] or keyword in d['member_name']]
    
    return jsonify({'success': True, 'data': mock_data})

# ============ 系统管理接口 ============

@app.route('/api/config', methods=['GET'])
def get_config():
    """获取系统参数"""
    if 'user' not in session:
        return jsonify({'success': False, 'message': '请先登录'})
    
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT config_key, config_value, description FROM sys_config")
    configs = cursor.fetchall()
    db.close()
    return jsonify({'success': True, 'data': configs})

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
    # 验证旧密码
    cursor.execute(
        "SELECT * FROM sys_users WHERE user_id=%s AND password=%s",
        (user_id, old_password)
    )
    user = cursor.fetchone()
    if not user:
        db.close()
        return jsonify({'success': False, 'message': '原密码错误'})
    
    try:
        cursor.execute(
            "UPDATE sys_users SET password=%s WHERE user_id=%s",
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