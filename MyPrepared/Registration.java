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

public class Registration {
	public static void UsedbookRegister(int book_id, int book_price) {
		Connection myConn=null;
		PreparedStatement pstmt=null;
		ResultSet myResSet=null;
		
		String insert="insert into UsedBook (book_id, price) values (?,?)";
		String select="select used_book_id, book_id, from UsedBook where registered_date=date(now())";
		
		
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
			System.out.println("\n--- 오늘 입고된 도서 ---");
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
