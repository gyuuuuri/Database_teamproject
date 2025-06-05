package MyPrepared;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.ArrayList;


public class ShippingCheck {
	public void Check_NotShipped() {
		Connection myConn=null;
		PreparedStatement pstmt=null;
		ResultSet myResSet=null;
		
		String select="select purchase_id, shipping_status from Shipping where shipping_status in (?,?) order by shipping_status, purchase_id ";
		
		String status1="배송준비중";
		String status2="배송중";
		int purchase_id=0;
		String status="";
		
		
		try {
			myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
			
			//배송완료가 아닌 주문들 조회
			pstmt=myConn.prepareStatement(select);
			pstmt.setString(1, status1);
			pstmt.setString(2, status2);
			myResSet=pstmt.executeQuery();
			
			System.out.println(String.format("%150s", "").replace(' ', '='));
			while(myResSet.next()) {
				purchase_id=myResSet.getInt("purchase_id");
				status=myResSet.getString("shipping_status");
				System.out.println(String.format("purchase_id: %4d, status=%-8s", purchase_id, status));
			}
			System.out.println(String.format("%150s", "").replace(' ', '='));
		
			
			
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(myResSet!=null) {
				try {
					myResSet.close();
					System.out.println("... Close ResultSet ...");
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
					System.out.println("... Close PreparedStatement ...");
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			if(myConn!=null) {
				try {
					myConn.close();
					System.out.println("... Close Connection ...");
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
	}

}
