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

public class Registeration {
	public void UsedbookRegister(int book_id, int book_price) {
		Connection myConn=null;
		PreparedStatement pstmt=null;
		ResultSet myResSet=null;
		
		String insert="insert into UsedBook (book_id, price) values (?,?)";
		String select="select used_book_id, book_id, from UsedBook where registered_date=date(now())";
		
		int used_book_id=0;
		int id=0;
		int price=0;
		
		try {
			myConn=DriverManager.getConnection(DBConfig.url, DBConfig.userID, DBConfig.userPW);
			
			//1. 책 insert
			pstmt=myConn.prepareStatement(insert);
			pstmt.setInt(1, book_id);
			pstmt.setInt(2, book_price);
			pstmt.executeUpdate();
			
			//2. 오늘 입고된 책 조회
			pstmt=myConn.prepareStatement(select);
			myResSet=pstmt.executeQuery();
			
			System.out.println(String.format("%150s", "").replace(' ', '='));
			while(myResSet.next()) {
				used_book_id=myResSet.getInt("used_book_id");
				id=myResSet.getInt("book_id");
				price=myResSet.getInt("price");
				System.out.println(String.format("used_book_id: %4d, book_id: %4d, price=%4d", used_book_id, id, price));
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
