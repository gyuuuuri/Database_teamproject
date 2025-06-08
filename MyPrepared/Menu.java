package MyPrepared;

import java.sql.Connection;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Menu {
	public static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) throws SQLException {
       Header.headerPrint();
       
       while (true) {
           printMainMenu();
           int choice = getUserChoice();

           switch (choice) {
               case 1:
                   BookSearch.searchBooks();
                   break;
               case 2:
                   inventoryMenu(); 
                   break;
               case 3:
                   registerMenu();
                   break;
               case 4:
                   rewardMenu();
                   break;
               case 5:
                   Discount.updateBookPrice();
                   break;    
               case 6:
                   cancellationMenu(); 
                   break;    
               case 7:
                   ShippingCheck.Check_NotShipped(); 
                   break;
               case 8:
                   reportMenu();
                   break;
               case 0:
                   System.out.println("프로그램을 종료합니다.");
                   return; // 프로그램 종료
               default:
                   System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
           }
           System.out.println("\n=========================================\n");
       }
		
		
    }
	
	// 메인 메뉴 출력
    public static void printMainMenu() {
        System.out.println("--- 메인 메뉴 ---");
        System.out.println("1. 도서 검색");
        System.out.println("2. 재고 조회");
        System.out.println("3. 입고 도서 등록");
        System.out.println("4. 포인트 적립");
        System.out.println("5. 도서 할인");
        System.out.println("6. 주문 취소");
        System.out.println("7. 배송 조회");
        System.out.println("8. 매출 조회");
        System.out.println("0. 프로그램 종료");
        System.out.print("메뉴를 선택하세요: ");
    }

    // 사용자로부터 정수 입력 받기
    public static int getUserChoice() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기
        return choice;
    }
    
    public static void registerMenu()throws SQLException {
    	System.out.println("\n--- 입고 도서 등록 ---");
    	System.out.println("새로 입고된 책의 id를 입력해주세요:");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.println("새로 입고된 책의 가격을 입력해주세요:");
        int price = scanner.nextInt();
        scanner.nextLine();
        System.out.println("\n------------------------------------\n");
        Registration.UsedbookRegister(id, price);
       
    }
    
    public static void inventoryMenu()throws SQLException {
        while (true) { // 재고 조회 메뉴 루프
            System.out.println("\n--- 재고 조회 메뉴 ---");
            System.out.println("1. 도서 제목별 판매중 재고 현황");
            System.out.println("2. 도서 제목별 판매중 재고 요약");
            System.out.println("3. 작가별 판매 및 재고 종합 분석"); // 종합 분석
            System.out.println("0. 이전 메뉴로 돌아가기");
            System.out.print("메뉴를 선택하세요: ");

            int subChoice = getUserChoice();
            switch (subChoice) {
                case 1:
                    Inventory.displayStockCountByTitle();
                    break;
                case 2:
                	Inventory.displayStockSummary();
                    break;
                case 3:
                	Inventory.displayAuthorSalesAndStockOverview();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
            }
            System.out.println("\n------------------------------------\n");
        }
    }
    
    public static void rewardMenu()throws SQLException {
        while (true) { 
            System.out.println("\n--- 포인트 적립 메뉴 ---");
            System.out.println("1. 리워드 포인트 누적");
            System.out.println("2. 누적 포인트별 등급 출력");
            System.out.println("3. 기간 내 획득한 리워드 포인트 조회");
            System.out.println("0. 이전 메뉴로 돌아가기");
            System.out.print("메뉴를 선택하세요: ");

            int subChoice = getUserChoice();
            switch (subChoice) {
                case 1:
                    Reward.accumulatedReward();
                    break;
                case 2:
                	Reward.printGradesByPoints();
                    break;
                case 3:
                	Reward.RewardPointsInPeriod();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
            }
            System.out.println("\n------------------------------------\n");
        }
    }
    
    public static void cancellationMenu()throws SQLException {
    	System.out.println("\n--- 주문 취소 ---");
    	System.out.println("취소할 주문의 주문번호를 입력해주세요:");
        int id = scanner.nextInt();
        scanner.nextLine();
    }
    
    public static void reportMenu()throws SQLException {
        while (true) { 
            System.out.println("\n--- 매출 조회 메뉴 ---");
            System.out.println("1. 연도별, 월별 매출 ");
            System.out.println("2. 유저별 결제 금액");
            System.out.println("0. 이전 메뉴로 돌아가기");
            System.out.print("메뉴를 선택하세요: ");

            int subChoice = getUserChoice();
            switch (subChoice) {
                case 1:
                	SalesReport.totalSales();
                    break;
                case 2:
                	SalesReport.totalUserSales();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
            }
            System.out.println("\n------------------------------------\n");
        }
    }

}
