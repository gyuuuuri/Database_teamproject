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

public class BookSearch {
	
	// 도서 검색 (작가/제목)
    public static void searchBooks() throws SQLException {
    	Connection myConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
        try {
        	myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
            System.out.println("\n--- 도서 검색 (작가/제목) ---");
            

            System.out.println("검색 기준을 선택하세요:");
            System.out.println("  (1) 제목만 검색");
            System.out.println("  (2) 작가만 검색");
            System.out.println("  (3) 제목 또는 작가 검색");
            System.out.print("선택: ");
            int searchType = Menu.getUserChoice();
            
            System.out.print("검색할 키워드를 입력하세요: ");
            String keyword = Menu.scanner.nextLine();
            String searchKeyword = "%" + keyword + "%"; // LIKE 연산자를 위한 와일드카드

            String sql = "";
            switch (searchType) {
                case 1: // 제목만 검색
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            b.title LIKE ? AND ub.status = '판매중'
                        """;
                    break;
                case 2: // 작가만 검색
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            b.author LIKE ? AND ub.status = '판매중'
                        """;
                    break;
                case 3: // 제목 또는 작가 검색
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            (b.title LIKE ? OR b.author LIKE ?) AND ub.status = '판매중'
                        """;
                    break;
                default:
                    System.out.println("잘못된 검색 기준입니다. 전체 검색으로 진행합니다.");
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            (b.title LIKE ? OR b.author LIKE ?) AND ub.status = '판매중'
                        """;
                    searchType = 3; // 기본값으로 설정
                    break;
            }

            pstmt = myConn.prepareStatement(sql);
            pstmt.setString(1, searchKeyword);
            if (searchType == 3) {
                pstmt.setString(2, searchKeyword); // OR 조건일 경우 두 번째 파라미터 바인딩
            }
            rs = pstmt.executeQuery();
            PrintResultSet.printResultSet(rs);

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
