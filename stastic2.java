package database;

import java.sql.*;

public class stastic2 {

	public void stastic2() {
		// TODO Auto-generated method stub
		String userID = "testuser";
		String userPW = "testpw";
		String dbName = "proj";
		String header = "jdbc:mysql://localhost:3306/";
		String encoding = "useUnicode=true&characterEncoding=UTF-8";
		String url = header + dbName + "?" + encoding;
		
		Connection myConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Statement stmt = null;
		String sql_insert="";
		String sql_select="";
		String sql_delete="";
		String sql_update="";
		
		int buyer_id=0;
		String username="";
		Date purchased_date=null;
		int final_price=0;
		int total_purchase_price=0;
		
		
		try {
			myConn = DriverManager.getConnection(url, userID, userPW);
			stmt=myConn.createStatement();
			sql_select="select buyer_id, username, purchased_date, final_price, sum(final_price) over (partition by buyer_id order by purchased_date) as total_purchase_price from user_purchase_history;";
			rs=stmt.executeQuery(sql_select);
			
			System.out.println(String.format("%50s", "").replace(' ', '='));
			while(rs.next()) {
				buyer_id = rs.getInt("buyer_id");
				username=rs.getString("username");
				purchased_date=rs.getDate("purchased_date");
				final_price=rs.getInt("final_price");
				total_purchase_price=rs.getInt("total_purchase_price");
				System.out.println(String.format("buyer_id: %-3d, username: %1s, purchased_date: %-8s, final_price: %-6s, total_purchase_price: %1d", 
						buyer_id, username, purchased_date, final_price, total_purchase_price));
			}
			System.out.println(String.format("%50s", "").replace(' ', '='));
			
			
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
