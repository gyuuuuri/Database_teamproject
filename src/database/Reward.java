package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Reward {
	
	//데이터베이스 연결 정보 
    String userID   = "root";
    String userPW   = "YourNewPass!23";
    String dbName = "bookstore";
    String url    = "jdbc:mysql://localhost:3306/" + dbName
                  + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    
    
    // ─────────────────────────────────────────────────────────
    // 기능 1. 리워드 포인트 누적 → Users.points 산출
    // ─────────────────────────────────────────────────────────
    
	public void accumulatedReward() {
		
		//객체 생성 
		Connection MyConn = null;
		PreparedStatement pstmt = null;
		Statement MyState = null;
		ResultSet MyResSet = null;
		
		String updateSql=null;
		
		
        try {
        	MyConn = DriverManager.getConnection(url, userID, userPW);

        	updateSql = "update Users u join (select po.buyer_id AS user_id, SUM(pi.reward_points) AS total_pts from PurchaseOrder po join PurchaseItem pi ON po.purchase_id = pi.purchase_id group by po.buyer_id) t ON u.user_id = t.user_id set u.points = t.total_pts";
            pstmt = MyConn.prepareStatement(updateSql);
            
            int updatedRows = pstmt.executeUpdate();
            System.out.println("▶ Users.points가 총 " + updatedRows + "건 업데이트되었습니다.");
            

            MyState = MyConn.createStatement();
            String selectSql = 
                "SELECT user_id, username, points " +
                "FROM Users " +
                "ORDER BY points DESC";

            MyResSet = MyState.executeQuery(selectSql);

            System.out.println("\n-- UPDATED Users.points (TOP 10) --");
            System.out.printf("%-8s %-20s %10s%n", "USER_ID", "USERNAME", "POINTS");
            System.out.println("-".repeat(45));
            int rowCount = 0;
            while (MyResSet.next() && rowCount < 10) {
                System.out.printf("%-8d %-20s %10d%n",
                			MyResSet.getInt("user_id"),
                			MyResSet.getString("username"),
                			MyResSet.getInt("points"));
                rowCount++;
            }    
        }catch (SQLException e) {
        	e.printStackTrace();
        }finally {
            // 리소스 해제
            if (MyResSet != null) {
                try { MyResSet.close(); 
                } catch (SQLException e) {
                	e.printStackTrace();}
                }
            if (pstmt != null) {
                try { pstmt.close(); 
                } catch (SQLException e) {
                	e.printStackTrace();}
                }
            if (MyState != null) {
                try { MyState.close(); 
                } catch (SQLException e) {
                	e.printStackTrace();}
                }
            if (MyConn != null) {
                try { MyConn.close(); 
                } catch (SQLException e) {
                	e.printStackTrace();}
                }
               }
           }
 

	 
	    // ─────────────────────────────────────────────────────────
	    // 2. 누적 포인트별 등급 출력
	    //    • SELECT + CASE → 등급 로직 구현
	    //    • 등급 산정: Platinum(>=1000), Gold(>=500),
	    //      Silver(>=200), Bronze(else)
	    // ─────────────────────────────────────────────────────────
		public void printGradesByPoints() {
			
			//객체 생성 
			Connection MyConn = null;
			PreparedStatement pstmt = null;
			ResultSet myResSet = null;
			
			String selectSql;
		
	        try {
	        	MyConn = DriverManager.getConnection(url, userID, userPW);
	        	
	        	selectSql="select user_id, username, points from users order by points desc";
				
	        	pstmt=MyConn.prepareStatement(selectSql);
	        	myResSet=pstmt.executeQuery();
	        	
	        	System.out.println("\n-- User Grade Based on Accumulated Points --");
	            System.out.printf("%-8s %-20s %10s %12s%n", "USER_ID", "USERNAME", "POINTS", "GRADE");
	            System.out.println("-".repeat(55));
	            while (myResSet.next()) {
	                int userId   = myResSet.getInt("user_id");
	                String name  = myResSet.getString("username");
	                int points   = myResSet.getInt("points");
	
	                // Java에서 CASE 처리
	                String grade;
	                if      (points >= 1000) grade = "Platinum";
	                else if (points >= 500)  grade = "Gold";
	                else if (points >= 200)  grade = "Silver";
	                else                     grade = "Bronze";
	
	                System.out.printf("%-8d %-20s %10d %12s%n",
	                                  userId, name, points, grade);
	            }
	        }catch (SQLException e) {
	        e.printStackTrace();
	        } finally  {
	            // 리소스 해제 	
	            if (myResSet != null)        
	            	try { myResSet.close();        
	            	} catch (SQLException e) {
	            		e.printStackTrace();
	            	}
	            if (pstmt != null)        
	            	try { pstmt.close();        
	            	} catch (SQLException e) {
	            		e.printStackTrace();
	            	}
	            if (MyConn != null)        
	            	try { MyConn.close();        
	            	} catch (SQLException e) {
	            		e.printStackTrace();
	            	}
	            }
		}
		
	    // ─────────────────────────────────────────────────────────
	    // 4. 기간 내 획득한 리워드 포인트 조회
	    //       *    • SELECT + JOIN + GROUP BY 사용
		//   	 *    • 예시: 2025-05-01 ~ 2025-05-31 고정
	    // ─────────────────────────────────────────────────────────
		public void RewardPointsInPeriod() {
			
			//객체 생성 
			Connection MyConn = null;
			PreparedStatement pstmt = null;
			ResultSet myResSet = null;
			
			//적립 대상 기간 파라미터(기간 별 리워드 누적) 
		    Timestamp start = Timestamp.valueOf("2025-05-01 00:00:00");
		    Timestamp end   = Timestamp.valueOf("2025-05-31 23:59:59");
			
		    String selectSql=null;
		    
		    try {
	        	MyConn = DriverManager.getConnection(url, userID, userPW);
	        	selectSql = "select u.user_id, u.username, ROUND(SUM(pi.reward_points), 0) AS earned_points from Users u join PurchaseOrder po ON u.user_id = po.buyer_id join PurchaseItem pi ON po.purchase_id = pi.purchase_id where po.purchased_date between ? and ? group by user_id, u.username order by earned_points desc";
	                 
	        	pstmt = MyConn.prepareStatement(selectSql);
	            pstmt.setTimestamp(1, start);
	            pstmt.setTimestamp(2, end);
	            myResSet = pstmt.executeQuery();
	            
	            System.out.println("\n-- Earned Points This Period (2025-05-01~2025-05-31) --");
	            System.out.printf("%-8s %-20s %15s%n", "USER_ID", "USERNAME", "EARNED_PTS");
	            System.out.println("-".repeat(50));
	            while (myResSet.next()) {
	                System.out.printf("%-8d %-20s %15d%n",
	                                  myResSet.getInt("user_id"),
	                                  myResSet.getString("username"),
	                                  myResSet.getInt("earned_points"));
	            }
	        }
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	        finally {
	            // 리소스 해제: ResultSet → PreparedStatement → Connection
	            if (myResSet != null) {
	                try { myResSet.close(); } catch (SQLException ignored) {}
	            }
	            if (pstmt != null) {
	                try { pstmt.close(); } catch (SQLException ignored) {}
	            }
	            if (MyConn != null) {
	                try { MyConn.close(); } catch (SQLException ignored) {}
	            }
	        }
	    }
			
	
	    public static void main(String[] args) {
	    	Reward rewardApp=new Reward();
	        
	        rewardApp.accumulatedReward();

	        rewardApp.printGradesByPoints();

	        rewardApp.RewardPointsInPeriod();
	    
	    }

}
