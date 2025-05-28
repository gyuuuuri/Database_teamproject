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

public class BookStore {
	String userID="testuser";
	String userPW="testpw";
	String dbName="projectdb";
	String header="jdbc:mysql://localhost:3306/";
	String encoding="useUnicode=true&characterEncoding=UTF-8";
	String url=header+dbName+"?"+encoding;
	
	public void PurchaseCancel(int purchase_id) {
		
		Connection myConn=null;
		PreparedStatement pstmt=null;
		ResultSet myResSet=null;
		
		String select="select used_book_id, buyer_id, reward_points from Purchase where purchase_id = ?";
		String update_status="update UsedBook set status=? where used_book_id=?";
		String update_points="update Users set points=points-? where user_id=?";
		String delete="delete from Purchase where purchase_id=?";
		
		int buyer_id=0;
		ArrayList<Integer> used_book_id = new ArrayList<>();
		ArrayList<Integer> reward_points = new ArrayList<>();
		int sum=0;
		
		try {
			myConn=DriverManager.getConnection(url, userID, userPW);
			
			//1. select 구문으로 필요한 데이터 떼어오기
			pstmt=myConn.prepareStatement(select);
			pstmt.setInt(1, purchase_id);
			myResSet=pstmt.executeQuery();
			while(myResSet.next()) {
				used_book_id.add(myResSet.getInt("used_book_id"));
				buyer_id=myResSet.getInt("buyer_id");
				reward_points.add(myResSet.getInt("reward_points"));
			}
			
			//2. update 구문으로 주문취소된 책들 status 바꾸기
			for(int book:used_book_id) {
				pstmt=myConn.prepareStatement(update_status);
				pstmt.setString(1, "판매중");
				pstmt.setInt(2, book);
				pstmt.executeUpdate();
			}
			
			//3. update 구문으로 구매자의 포인트 차감
			for(int points:reward_points) {
				sum+=points;
			}
			pstmt=myConn.prepareStatement(update_points);
			pstmt.setInt(1, sum);
			pstmt.setInt(2, buyer_id);
			pstmt.executeUpdate();
			
			//4. delete 구문으로 주문 목록에서 삭제
			pstmt=myConn.prepareStatement(delete);
			pstmt.setInt(1, purchase_id);
			pstmt.executeUpdate();
			
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
