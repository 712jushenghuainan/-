package com.swim.dao;

import com.swim.entity.Card;
import com.swim.entity.Member;
import com.swim.util.DBUtil;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class CardDao {

    /**
     * 调用存储过程 sp_SellCard 进行售卡
     * @param member 会员信息
     * @param card   卡信息
     * @return 新生成的卡号
     * @throws Exception 售卡失败时抛出异常
     */
    public String sellCard(Member member, Card card) throws Exception {
        // 注意：9个占位符对应存储过程的 7个IN + 2个OUT
        String sql = "{call sp_SellCard(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBUtil.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // ----- 1. 设置输入参数 (IN) -----
            // Member 字段
            cs.setString(1, member.getName());
            cs.setString(2, member.getPhone());
            cs.setString(3, member.getIdNumber());
            cs.setString(4, member.getAddress());
            // Card 字段
            cs.setString(5, card.getCardType());
            cs.setBigDecimal(6, card.getBalance());   // 储值卡充值金额
            cs.setBigDecimal(7, card.getDiscountRate());

            // ----- 2. 注册输出参数 (OUT) -----
            cs.registerOutParameter(8, Types.VARCHAR); // p_card_id
            cs.registerOutParameter(9, Types.INTEGER); // p_result

            // ----- 3. 执行存储过程 -----
            cs.execute();

            // ----- 4. 获取返回结果 -----
            int resultCode = cs.getInt(9);
            if (resultCode == 0) {
                // 成功，返回卡号
                return cs.getString(8);
            } else {
                // 失败，抛出异常
                throw new SQLException("存储过程返回错误码: " + resultCode);
            }
        }
    }
}