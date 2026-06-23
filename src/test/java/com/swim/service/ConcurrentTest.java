package com.swim.service;

import com.swim.dao.CardDAO;
import com.swim.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

/**
 * 并发测试 - 演示 READ_COMMITTED 隔离级别
 *
 * 场景：多个线程同时对同一张卡进行存款操作
 * 预期：最终余额正确，不会出现脏读
 */
public class ConcurrentTest {

    private static final String TEST_CARD_ID = "CARD001";
    private static final BigDecimal DEPOSIT_AMOUNT = new BigDecimal("100");
    private static final int THREAD_COUNT = 5;

    public static void main(String[] args) throws InterruptedException {
        // 重置余额为0
        resetBalance(TEST_CARD_ID, BigDecimal.ZERO);
        System.out.println("========== 并发存款测试开始 ==========");
        System.out.println("测试卡号：" + TEST_CARD_ID);
        System.out.println("初始余额：0.00");
        System.out.println("每个线程存款：" + DEPOSIT_AMOUNT + " 元");
        System.out.println("线程数：" + THREAD_COUNT);
        System.out.println("预期最终余额：" + DEPOSIT_AMOUNT.multiply(new BigDecimal(THREAD_COUNT)) + " 元");
        System.out.println("隔离级别：READ_COMMITTED");
        System.out.println("========================================");

        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        DepositService service = new DepositService();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("线程 " + threadId + " 开始存款...");
                    BigDecimal newBalance = service.deposit(TEST_CARD_ID, DEPOSIT_AMOUNT, 1, "并发测试-" + threadId);
                    System.out.println("线程 " + threadId + " 存款成功，当前余额：" + newBalance);
                } catch (Exception e) {
                    System.err.println("线程 " + threadId + " 存款失败：" + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        System.out.println("========================================");

        BigDecimal finalBalance = getBalance(TEST_CARD_ID);
        BigDecimal expected = DEPOSIT_AMOUNT.multiply(new BigDecimal(THREAD_COUNT));
        System.out.println("最终余额：" + finalBalance);
        System.out.println("预期余额：" + expected);
        System.out.println("测试结果：" + (finalBalance.compareTo(expected) == 0 ? "✅ 通过" : "❌ 失败"));
    }

    private static void resetBalance(String cardId, BigDecimal amount) {
        try (Connection conn = DBUtil.getConnection()) {
            // 先更新为0，再测试
            String sql = "UPDATE t_card SET balance = ? WHERE card_id = ?";
            try (var pstmt = conn.prepareStatement(sql)) {
                pstmt.setBigDecimal(1, amount);
                pstmt.setString(2, cardId);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BigDecimal getBalance(String cardId) {
        try (Connection conn = DBUtil.getConnection()) {
            return new CardDAO().getBalance(conn, cardId);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
}