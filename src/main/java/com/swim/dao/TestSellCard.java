package com.swim.dao;

import com.swim.entity.Card;
import com.swim.entity.Member;
import java.math.BigDecimal;

public class TestSellCard {
    public static void main(String[] args) {
        System.out.println("===== 开始测试售卡存储过程 =====");
        try {
            // 1. 造测试数据
            Member member = new Member();
            member.setName("测试用户-张三");
            member.setPhone("13800138001");
            member.setIdNumber("110101199001011234");
            member.setAddress("上海市松江区文汇路300弄");

            Card card = new Card();
            card.setCardType("储值卡");
            card.setBalance(new BigDecimal("500.00")); // 充值500
            card.setDiscountRate(new BigDecimal("1.00"));

            // 2. 调用 DAO 执行售卡
            CardDao dao = new CardDao();
            String cardId = dao.sellCard(member, card);

            // 3. 输出结果
            System.out.println("✅✅✅ 售卡测试成功！");
            System.out.println("生成的卡号：" + cardId);
            System.out.println("请去数据库查询 t_member 和 t_card 表确认数据已插入。");

        } catch (Exception e) {
            System.err.println("❌❌❌ 售卡测试失败！");
            System.err.println("错误信息：" + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("===== 测试结束 =====");
    }
}
