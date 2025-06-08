package MyPrepared;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Discount {
	
 // 평균 가격 초과 도서 일괄 할인 기능 (서브쿼리, UPDATE)
    public static void updateBookPrice() throws SQLException {
    	Connection myConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement selectAfterPstmt = null;
        ResultSet selectAfterRs = null;
        try {
            System.out.println("\n--- 평균 가격 초과 도서 일괄 할인 ---");

            System.out.println("--- 할인 대상 도서 목록 (할인 전 가격) ---");
            String selectBeforeDiscountSql = """
                SELECT ub.used_book_id, b.title, ub.price
                FROM UsedBook ub
                JOIN Book b ON ub.book_id = b.book_id
                WHERE ub.status = '판매중' AND ub.price > (SELECT AVG(price) FROM UsedBook WHERE status = '판매중')
            """;
            pstmt = myConn.prepareStatement(selectBeforeDiscountSql);
            rs = pstmt.executeQuery();

            // 할인 대상 도서 ID들을 저장
            List<Integer> eligibleBookIds = new ArrayList<>();
            while(rs.next()) {
                eligibleBookIds.add(rs.getInt("used_book_id"));
            }
            // rs는 이미 끝까지 읽었으므로 닫고, printResultSet을 위해 다시 열 필요 없음
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); } rs = null; }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); } pstmt = null; }

            // 할인 대상 도서 목록 (할인 전 가격)을 다시 조회하여 출력
            if (!eligibleBookIds.isEmpty()) {
                StringBuilder inClause = new StringBuilder();
                inClause.append("(");
                for (int i = 0; i < eligibleBookIds.size(); i++) {
                    inClause.append(eligibleBookIds.get(i));
                    if (i < eligibleBookIds.size() - 1) {
                        inClause.append(",");
                    }
                }
                inClause.append(")");

                String selectBeforePrintSql = """
                    SELECT ub.used_book_id, b.title, ub.price
                    FROM UsedBook ub
                    JOIN Book b ON ub.book_id = b.book_id
                    WHERE ub.used_book_id IN """ + inClause.toString() + """
                    """;
                pstmt = myConn.prepareStatement(selectBeforePrintSql);
                rs = pstmt.executeQuery();
                PrintResultSet.printResultSet(rs); // 할인 전 가격 목록 출력
            } else {
                System.out.println("할인 대상 도서가 없습니다.");
                return; // 할인 대상이 없으면 종료
            }


            System.out.print("적용할 할인율 (예: 10% 할인은 0.1 입력, 취소하려면 0 입력): ");
            double discountRate = Menu.scanner.nextDouble();
            Menu.scanner.nextLine();

            if (discountRate <= 0 || discountRate >= 1) {
                System.out.println("할인 적용을 취소하거나 유효한 할인율이 아닙니다. 0보다 크고 1보다 작은 값을 입력하세요.");
                return;
            }

            System.out.print("위 도서들에 " + (discountRate * 100) + "% 할인을 적용하시겠습니까? (yes/no): ");
            String confirm = Menu.scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("yes")) {
                System.out.println("할인 적용을 취소합니다.");
                return;
            }

            String updateSql = """
                UPDATE UsedBook
                SET price = FLOOR(price * ?)
                WHERE status = '판매중' AND used_book_id IN (
                    SELECT used_book_id FROM (
                        SELECT ub_sub.used_book_id
                        FROM UsedBook AS ub_sub
                        WHERE ub_sub.status = '판매중' AND ub_sub.price > (SELECT AVG(price) FROM UsedBook WHERE status = '판매중')
                    ) AS tmp_eligible_books
                )
            """;
            pstmt = myConn.prepareStatement(updateSql);
            pstmt.setDouble(1, (1 - discountRate));
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + "개 도서의 가격이 할인되었습니다.");

            // 할인 적용 후, 해당 도서들의 새로운 가격을 조회하여 출력
            if (rowsAffected > 0 && !eligibleBookIds.isEmpty()) {
                System.out.println("\n--- 할인 적용 후 가격 (할인된 도서들) ---");
                StringBuilder inClauseAfter = new StringBuilder();
                inClauseAfter.append("(");
                for (int i = 0; i < eligibleBookIds.size(); i++) {
                    inClauseAfter.append(eligibleBookIds.get(i));
                    if (i < eligibleBookIds.size() - 1) {
                        inClauseAfter.append(",");
                    }
                }
                inClauseAfter.append(")");

                String selectAfterDiscountSql = """
                    SELECT ub.used_book_id, b.title, ub.price
                    FROM UsedBook ub
                    JOIN Book b ON ub.book_id = b.book_id
                    WHERE ub.used_book_id IN """ + inClauseAfter.toString() + """
                    """;
                selectAfterPstmt = myConn.prepareStatement(selectAfterDiscountSql);
                selectAfterRs = selectAfterPstmt.executeQuery();
                PrintResultSet.printResultSet(selectAfterRs); // 할인 후 가격 목록 출력
            }

        } finally {
			if (rs != null) {			
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (myConn != null) {
				try {
					myConn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}
    }

}
