package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Reward {

    public static void main(String[] args) {
        // DB 접속 정보
        String user   = "testuser";
        String pass   = "testpw";
        String dbName = "testdb_cmp";
        String url    = "jdbc:mysql://localhost:3306/" + dbName
                      + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

        Connection       conn       = null;
        PreparedStatement pstAcc    = null;
        PreparedStatement pstFinal  = null;
        PreparedStatement pstEarned = null;
        PreparedStatement pstGrade  = null;
        ResultSet         rs        = null;

        // ─────────────────────────────────────────────────────────────
        // 파라미터: 적립 대상 기간
        // ─────────────────────────────────────────────────────────────
        Timestamp start = Timestamp.valueOf("2025-05-01 00:00:00");
        Timestamp end   = Timestamp.valueOf("2025-05-31 23:59:59");

        try {
            // ─────────────────────────────────────────────────────────
            // 1) Connection 획득
            //    (이 단계는 모든 JDBC 기능에 공통) 
            // ─────────────────────────────────────────────────────────
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("... Connected to " + dbName + " ...");

            // ─────────────────────────────────────────────────────────
            // 2) 리워드 포인트 누적
            //    • UPDATE JOIN with 서브쿼리 사용 → [REQ-Update], [REQ-Subquery]
            //    • Purchase.reward_points 합산 → Users.point 누적
            // ─────────────────────────────────────────────────────────
            String sqlAcc =
                "UPDATE Users u " +
                "JOIN ( " +
                "  SELECT buyer_id AS user_id, SUM(reward_points) AS pts " +
                "  FROM Purchase " +
                "  WHERE purchased_date BETWEEN ? AND ? " +
                "  GROUP BY buyer_id " +            // [REQ-Select-GROUP BY]
                ") t ON u.user_id = t.user_id " +
                "SET u.point = u.point + t.pts";
            pstAcc = conn.prepareStatement(sqlAcc);
            pstAcc.setTimestamp(1, start);
            pstAcc.setTimestamp(2, end);
            int updated = pstAcc.executeUpdate();
            System.out.println("▶ Points accumulated for " + updated + " users.");

            // ─────────────────────────────────────────────────────────
            // 3) 최종 유저별 누적 포인트 출력
            //    • 단순 SELECT → [REQ-Select-Join] (Users 테이블 단독 조회지만
            //      ANSI 조인을 통해 추후 확장 가능)
            // ─────────────────────────────────────────────────────────
            String sqlFinal =
                "SELECT user_id, username, point " +
                "FROM Users " +
                "ORDER BY point DESC";
            pstFinal = conn.prepareStatement(sqlFinal);
            rs = pstFinal.executeQuery();
            System.out.println("\n-- Final User Points --");
            System.out.printf("%-8s %-20s %10s%n", "USER_ID", "USERNAME", "POINT");
            System.out.println("-".repeat(45));
            while (rs.next()) {
                System.out.printf("%-8d %-20s %10d%n",
                                  rs.getInt("user_id"),
                                  rs.getString("username"),
                                  rs.getInt("point"));
            }
            rs.close();
            pstFinal.close();

            // ─────────────────────────────────────────────────────────
            // 4) 기간 내 획득 리워드 포인트 조회
            //    • SELECT + GROUP BY → [REQ-Select-GROUP BY]
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

            // ─────────────────────────────────────────────────────────
            // 5) 누적 포인트별 등급 출력
            //    • SELECT + CASE → 등급 로직 구현
            //    • 등급 산정은 비즈니스 로직, SQL로도 가능
            // ─────────────────────────────────────────────────────────
            String sqlGrade =
                "SELECT user_id, username, point, " +
                " CASE " +
                "   WHEN point >= 1000 THEN 'Platinum' " +  // Platinum 기준
                "   WHEN point >= 500  THEN 'Gold'     " +  // Gold 기준
                "   WHEN point >= 200  THEN 'Silver'   " +  // Silver 기준
                "   ELSE 'Bronze'                        " +  // Bronze 이하
                " END AS grade " +
                "FROM Users " +
                "ORDER BY point DESC";
            pstGrade = conn.prepareStatement(sqlGrade);
            rs = pstGrade.executeQuery();
            System.out.println("\n-- User Grade --");
            System.out.printf("%-8s %-20s %10s %12s%n",
                              "USER_ID", "USERNAME", "POINT", "GRADE");
            System.out.println("-".repeat(55));
            while (rs.next()) {
                System.out.printf("%-8d %-20s %10d %12s%n",
                                  rs.getInt("user_id"),
                                  rs.getString("username"),
                                  rs.getInt("point"),
                                  rs.getString("grade"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // ─────────────────────────────────────────────────────────
            // 리소스 해제: 예제와 동일한 패턴으로 순서 보장
            // ─────────────────────────────────────────────────────────
            if (rs != null)       try { rs.close();       } catch(SQLException ignored){}
            if (pstGrade != null) try { pstGrade.close(); } catch(SQLException ignored){}
            if (pstEarned != null)try { pstEarned.close();} catch(SQLException ignored){}
            if (pstFinal != null) try { pstFinal.close(); } catch(SQLException ignored){}
            if (pstAcc != null)   try { pstAcc.close();   } catch(SQLException ignored){}
            if (conn != null)     try { conn.close();     } catch(SQLException ignored){}
        }
    }
}
