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

public class Cancellation {
	
	public static void PurchaseCancel(int purchase_id) {
		
		Connection myConn=null;
		PreparedStatement pstmt=null;
		ResultSet myResSet=null;
		
		String select0="select count(*) from Shipping where purchase_id = ?";
		String select1="select used_book_id, reward_points from PurchaseItem where purchase_id = ?";
		String select2="select buyer_id from PurchaseOrder where purchase_id = ?";
		String update_status="update UsedBook set status=? where used_book_id=?";
		String update_points="update Users set points=points-? where user_id=?";
		String delete="delete from Purchase where purchase_id=?";
		
		int buyer_id=0;
		ArrayList<Integer> used_book_id = new ArrayList<>();
		ArrayList<Integer> reward_points = new ArrayList<>();
		int sum=0;
		
		try {
			myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
			
			//0. 주문취소 가능한 주문인지 확인하기
			pstmt=myConn.prepareStatement(select0);
			myResSet=pstmt.executeQuery();
			int cnt=0;
			while(myResSet.next()) {
				cnt=myResSet.getInt("count(*)");
			}
			if(cnt==0) {
				System.out.println("주문 취소 가능한 주문이 아닙니다.");
				return;
			}
			
			//1. select 구문으로 필요한 데이터 떼어오기
			//1-1. 주문취소할 책 정보 떼어오기
			pstmt=myConn.prepareStatement(select1);
			pstmt.setInt(1, purchase_id);
			myResSet=pstmt.executeQuery();
			while(myResSet.next()) {
				used_book_id.add(myResSet.getInt("used_book_id"));
				reward_points.add(myResSet.getInt("reward_points"));
			}
			//1-2.포인트 차감할 고객 정보 떼어오기
			pstmt=myConn.prepareStatement(select2);
			pstmt.setInt(1, purchase_id);
			myResSet=pstmt.executeQuery();
			while(myResSet.next()) {
				buyer_id=myResSet.getInt("buyer_id");
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
			
			System.out.println("정상적으로 취소 완료되었습니다.");
	        System.out.println("\n------------------------------------\n");
			
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
