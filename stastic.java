package database;

import java.sql.*;
public class stastic {

	public void stastic1() {
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
		
		int year=0;
		int month=0;
		int total=0;
		
		
		try {
			myConn = DriverManager.getConnection(url, userID, userPW);
			stmt=myConn.createStatement();
			sql_select="select year(purchased_date) as year, month(purchased_date) as month, count(*) as total from purchase group by year, month with rollup";
			rs=stmt.executeQuery(sql_select);
			
			System.out.println(String.format("%50s", "").replace(' ', '='));
			while(rs.next()) {
				year = rs.getInt("year");
				month=rs.getInt("month");
				total=rs.getInt("total");
				System.out.println(String.format("year: %4d, month: %1d, total: %-8s", 
						year, month, total));
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
