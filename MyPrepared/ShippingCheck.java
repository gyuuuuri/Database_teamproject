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
	public static void Check_NotShipped() {
		Connection myConn=null;
		PreparedStatement pstmt=null;
		ResultSet myResSet=null;
		
		String select="""
				select s.purchase_id, po.buyer_id, s.shipping_status 
				from Shipping s 
				join PurchaseOrder po on s.purchase_id=po.purchase_id 
				where shipping_status in (?,?) 
				order by shipping_status, purchase_id 
				""";
		
		String status1="배송준비중";
		String status2="배송중";
		
		
		try {
			myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
			
			//배송완료가 아닌 주문들 조회
			pstmt=myConn.prepareStatement(select);
			pstmt.setString(1, status1);
			pstmt.setString(2, status2);
			myResSet=pstmt.executeQuery();
			System.out.println("\n-- 배송 완료되지 않은 주문 목록 --");
			PrintResultSet.printResultSet(myResSet);
			
			
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(myResSet!=null) {
				try {
					myResSet.close();
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			if(myConn!=null) {
				try {
					myConn.close();
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
	}

}
