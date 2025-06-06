import java.util.Scanner;

public class main {
	static Scanner s=new Scanner(System.in);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 while (true) {
             printMainMenu();
             int choice = getUserChoice();

             switch (choice) {
                 case 1:
                     stastic.totalSales();
                     //년, 월별 매출(다차원 OLAP)
                     break;
                 case 2:
                	 stastic.totalUserSales();
                	 //전체 유저의 구매기록을 바이어 id 순으로 정렬(윈도우 OLAP)
                	 break;
                 case 0:
                     System.out.println("프로그램을 종료합니다.");
                     return; // 프로그램 종료
                 default:
                     System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
             }
         }
	}
	
	private static int getUserChoice() {
        while (!s.hasNextInt()) {
            System.out.println("유효한 숫자를 입력해주세요.");
            s.next(); 
            System.out.print("메뉴를 선택하세요: ");
        }
        int choice = s.nextInt();
        s.nextLine(); // 버퍼 비우기
        return choice;
    }

	 private static void printMainMenu() {
	        System.out.println("--- 통계 메뉴 ---");
	        System.out.println("1. 연간/월간 매출 조회");
	        System.out.println("2. 전체 유저 구매기록 조회(id순)");
	        System.out.println("0. 메인 메뉴로 돌아가기");
	        System.out.print("메뉴를 선택하세요: ");
	    }
}
