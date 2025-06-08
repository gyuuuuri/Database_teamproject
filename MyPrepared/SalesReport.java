package MyPrepared;

import java.sql.*;

public class SalesReport {
	public static void totalSales() {
		
		Connection myConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Statement stmt = null;
		String sql_select="";
		
		
		try {
			myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
			stmt=myConn.createStatement();
			sql_select="""
					select year(po.purchased_date) as year,
					       month(po.purchased_date) as month, 
					       count(*) as total, 
					       sum(final_price) as sales 
					from PurchaseOrder po 
					join PurchaseItem pi on po.purchase_id=pi.purchase_id 
					group by year, month with rollup
					""";
			rs=stmt.executeQuery(sql_select);
			
			System.out.println("\n-- 연도별,월별 매출 --");
			PrintResultSet.printResultSet(rs);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (rs != null) {			
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
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
	
	public static void totalUserSales() {
		// TODO Auto-generated method stub
		
		Connection myConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Statement stmt = null;
		String sql_select="";
		
		
		try {
			myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
			stmt=myConn.createStatement();
			sql_select="""
					select buyer_id, username, purchased_date, final_price, 
					       sum(final_price) over (partition by buyer_id 
					                              order by purchased_date) as total_purchase_price 
					       from user_purchase_history
					""";
			rs=stmt.executeQuery(sql_select);
			
			System.out.println("\n-- 유저별 매출 --");
			PrintResultSet.printResultSet(rs);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (rs != null) {			
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
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
