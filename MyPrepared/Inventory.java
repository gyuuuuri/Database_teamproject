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

public class Inventory {
	
	// 도서 제목별 판매중 재고 현황 (기본 조회, 개수) - 재고 0권 포함
    public static void displayStockCountByTitle() throws SQLException {
    	
    	Connection myConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
        try {
        	myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
            System.out.println("\n--- 도서 제목별 판매중 재고 현황 ---");
            String sql = """
                SELECT
                    b.title AS book_title,
                    b.author,
                    b.publisher,
                    COALESCE(COUNT(ub.used_book_id), 0) AS available_stock_count,
                    COALESCE(SUM(ub.price), 0) AS total_stock_amount_current_price
                FROM
                    Book b
                LEFT JOIN
                    UsedBook ub ON b.book_id = ub.book_id AND ub.status = '판매중'
                GROUP BY
                    b.book_id, b.title, b.author, b.publisher
                ORDER BY
                    book_title;
            """;
            pstmt = myConn.prepareStatement(sql);
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
    
 // 도서 제목별 판매중 재고 요약 (GROUP BY, HAVING)
    public static void displayStockSummary() throws SQLException{
    	Connection myConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
        try {
        	myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
            System.out.println("\n--- 도서 제목별 판매중 재고 요약 (재고 2권 이상) ---");
            String sql = """
                SELECT
                    b.title AS book_title,
                    COUNT(ub.used_book_id) AS available_stock_count,
                    SUM(ub.price) AS total_available_stock_amount
                FROM
                    UsedBook ub
                JOIN
                    Book b ON ub.book_id = b.book_id
                WHERE
                    ub.status = '판매중'
                GROUP BY
                    b.title
                HAVING
                    available_stock_count >= 2;
            """;
            pstmt = myConn.prepareStatement(sql);
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
    
 // 작가별 판매 및 재고 종합 분석 (판매 완료 기준 랭킹)
    public static void displayAuthorSalesAndStockOverview() throws SQLException {
    	Connection myConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
        try {
        	myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
            System.out.println("\n--- 작가별 판매 및 재고 종합 분석 (판매 완료 기준 랭킹) ---");
            String sql = """
                SELECT
                    AuthorSales.author_name,
                    AuthorSales.total_sales_count,
                    AuthorSales.total_sales_amount, -- 총 판매 금액 합계
                    COALESCE(AuthorStock.total_current_stock_amount, 0) AS total_current_stock_amount,
                    COALESCE(AuthorStock.current_stock_count, 0) AS current_stock_count,
                    RANK() OVER (ORDER BY AuthorSales.total_sales_count DESC) AS sales_rank
                FROM
                    ( -- 작가별 총 판매 건수 및 총 판매 금액 합계
                        SELECT
                            b.author AS author_name,
                            COUNT(pi.purchase_id) AS total_sales_count, -- PurchaseItem에서 구매 건수 집계
                            SUM(pi.final_price) AS total_sales_amount -- PurchaseItem에서 최종 금액 합계
                        FROM
                            PurchaseOrder po -- PurchaseOrder 사용
                        JOIN
                            PurchaseItem pi ON po.purchase_id = pi.purchase_id -- PurchaseItem 조인
                        JOIN
                            UsedBook ub ON pi.used_book_id = ub.used_book_id
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        GROUP BY
                            b.author
                    ) AS AuthorSales
                LEFT JOIN
                    ( -- 작가별 현재 판매중인 재고 총액 및 개수
                        SELECT
                            b.author AS author_name,
                            SUM(ub.price) AS total_current_stock_amount,
                            COUNT(ub.used_book_id) AS current_stock_count
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            ub.status = '판매중'
                        GROUP BY
                            b.author
                    ) AS AuthorStock ON AuthorSales.author_name = AuthorStock.author_name
                ORDER BY
                    AuthorSales.total_sales_count DESC;
            """;
            pstmt = myConn.prepareStatement(sql);
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
