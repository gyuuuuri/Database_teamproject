package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Reward {

    public static void main(String[] args) {
        // ─────────────────────────────────────────────────────────────────
        // DB 접속 정보
        // ─────────────────────────────────────────────────────────────────
        String user   = "root";
        String pass   = "YourNewPass!23";
        String dbName = "bookstore";
        String url    = "jdbc:mysql://localhost:3306/" + dbName
                      + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

        Connection       conn       = null;
        PreparedStatement pstAcc    = null;
        PreparedStatement pstAccView= null;
        PreparedStatement pstGrade  = null;
        PreparedStatement pstEarned = null;
        ResultSet         rs        = null;

        // ─────────────────────────────────────────────────────────────────
        // 파라미터: 적립 대상 기간
        // ─────────────────────────────────────────────────────────────────
        Timestamp start = Timestamp.valueOf("2025-05-01 00:00:00");
        Timestamp end   = Timestamp.valueOf("2025-05-31 23:59:59");

        try {
            // ─────────────────────────────────────────────────────────
            // 1) Connection 획득
            // ─────────────────────────────────────────────────────────
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("... Connected to " + dbName + " ...");

            // ─────────────────────────────────────────────────────────
            // 1. 리워드 포인트 누적 → Users.points 산출
            //    • UPDATE JOIN with 서브쿼리 → [Purchase.reward_points 합산]
            //    • Users.points 컬럼에 누적
            // ─────────────────────────────────────────────────────────
            String sqlAcc =
                "UPDATE Users u " +
                "JOIN ( " +
                "  SELECT buyer_id AS user_id, SUM(reward_points) AS pts " +
                "  FROM Purchase " +
                "  WHERE purchased_date BETWEEN ? AND ? " +
                "  GROUP BY buyer_id " +  // [REQ-Select-GROUP BY]
                ") t ON u.user_id = t.user_id " +
                "SET u.points = u.points + t.pts"; // Users.points로 수정

            pstAcc = conn.prepareStatement(sqlAcc);
            pstAcc.setTimestamp(1, start);
            pstAcc.setTimestamp(2, end);
            int updated = pstAcc.executeUpdate();
            System.out.println("▶ Points accumulated for " + updated + " users.");

            // ─────────────────────────────────────────────────────────
            // 2. 유저별 누적 리워드 조회
            //    • 단순 SELECT → Users 테이블의 points 출력
            // ─────────────────────────────────────────────────────────
            String sqlAccView =
                "SELECT user_id, username, points " +
                "FROM Users " +
                "ORDER BY points DESC";

            pstAccView = conn.prepareStatement(sqlAccView);
            rs = pstAccView.executeQuery();

            System.out.println("\n-- Accumulated Reward Points Per User --");
            System.out.printf("%-8s %-20s %10s%n", "USER_ID", "USERNAME", "POINTS");
            System.out.println("-".repeat(45));
            while (rs.next()) {
                System.out.printf("%-8d %-20s %10d%n",
                                  rs.getInt("user_id"),
                                  rs.getString("username"),
                                  rs.getInt("points"));
            }
            rs.close();
            pstAccView.close();

            // ─────────────────────────────────────────────────────────
            // 3. 누적 포인트별 등급 출력
            //    • SELECT + CASE → 등급 로직 구현
            //    • 등급 산정: Platinum(>=1000), Gold(>=500),
            //      Silver(>=200), Bronze(else)
            // ─────────────────────────────────────────────────────────
            String sqlGrade =
                "SELECT user_id, username, points, " +
                " CASE " +
                "   WHEN points >= 1000 THEN 'Platinum' " +
                "   WHEN points >= 500  THEN 'Gold'     " +
                "   WHEN points >= 200  THEN 'Silver'   " +
                "   ELSE 'Bronze'                         " +
                " END AS grade " +
                "FROM Users " +
                "ORDER BY points DESC";

            pstGrade = conn.prepareStatement(sqlGrade);
            rs = pstGrade.executeQuery();

            System.out.println("\n-- User Grade Based on Accumulated Points --");
            System.out.printf("%-8s %-20s %10s %12s%n",
                              "USER_ID", "USERNAME", "POINTS", "GRADE");
            System.out.println("-".repeat(55));
            while (rs.next()) {
                System.out.printf("%-8d %-20s %10d %12s%n",
                                  rs.getInt("user_id"),
                                  rs.getString("username"),
                                  rs.getInt("points"),
                                  rs.getString("grade"));
            }
            rs.close();
            pstGrade.close();

            // ─────────────────────────────────────────────────────────
            // 4. 기간 내 획득한 리워드 포인트 조회
            //    • SELECT + GROUP BY → [Purchase.reward_points 합산]
            //    • ROUND() 사용해 소수점 반올림 처리
            // ─────────────────────────────────────────────────────────
            String sqlEarned =
                "SELECT u.user_id, u.username, " +
                "       ROUND(SUM(p.reward_points), 0) AS earned_points " +
                "FROM Users u " +
                "JOIN Purchase p ON u.user_id = p.buyer_id " +
                "WHERE p.purchased_date BETWEEN ? AND ? " +
                "GROUP BY u.user_id, u.username " +
                "ORDER BY earned_points DESC";

            pstEarned = conn.prepareStatement(sqlEarned);
            pstEarned.setTimestamp(1, start);
            pstEarned.setTimestamp(2, end);
            rs = pstEarned.executeQuery();

            System.out.println("\n-- Earned Points This Period --");
            System.out.printf("%-8s %-20s %15s%n", "USER_ID", "USERNAME", "EARNED_PTS");
            System.out.println("-".repeat(50));
            while (rs.next()) {
                System.out.printf("%-8d %-20s %15d%n",
                                  rs.getInt("user_id"),
                                  rs.getString("username"),
                                  rs.getInt("earned_points"));
            }
            rs.close();
            pstEarned.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // ─────────────────────────────────────────────────────────
            // 리소스 해제: ResultSet → PreparedStatement들 → Connection 순서
            // ─────────────────────────────────────────────────────────
            if (rs != null)        try { rs.close();        } catch (SQLException ignored) {}
            if (pstEarned != null) try { pstEarned.close(); } catch (SQLException ignored) {}
            if (pstGrade != null)  try { pstGrade.close();  } catch (SQLException ignored) {}
            if (pstAccView != null)try { pstAccView.close();} catch (SQLException ignored) {}
            if (pstAcc != null)    try { pstAcc.close();    } catch (SQLException ignored) {}
            if (conn != null)      try { conn.close();      } catch (SQLException ignored) {}
        }
    }
}
