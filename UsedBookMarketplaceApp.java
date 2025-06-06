import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class UsedBookMarketplaceApp {

    // 사용자 입력 처리 객체
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement pstmt = null; 
        ResultSet rs = null; 

        // 데이터베이스 연결 설정
        String dbHost = "localhost";
        String dbPort = "3306";
        String dbName = "usedbook_db"; 
        String dbParams = "useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?" + dbParams;

        String dbUser = "root"; 
        String dbPassword = "your_password"; 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            System.out.println("   ____________________________________________________");
            System.out.println("  |____________________________________________________|");
            System.out.println("  | __     __   ____   ___ ||  ____    ____     _  __  |");
            System.out.println("  ||  |__ |--|_| || |_|   |||_|**|*|__|+|+||___| ||  | |");
            System.out.println("  ||==|^^||--| |=||=| |=*=||| |~~|~|  |=|=|| | |~||==| |");
            System.out.println("  ||  |##||  | | || | |JRO|||-|  | |==|+|+||-|-|~||__| |");
            System.out.println("  ||__|__||__|_|_||_|_|___|||_|__|_|__|_|_||_|_|_||__|_|");
            System.out.println("  ||_______________________||__________________________|");
            System.out.println("  | _____________________  ||      __   __  _  __    _ |");
            System.out.println("  ||=|=|=|=|=|=|=|=|=|=|=| __..\\/ |  |_|  ||#||==|  / /|");
            System.out.println("  || | | | | | | | | | | |/\\ \\  \\\\|++|=|  || ||==| / / |");
            System.out.println("  ||_|_|_|_|_|_|_|_|_|_|_/_/\\_.___\\__|_|__||_||__|/_/__|");
            System.out.println("  |____________________ /\\~()/()~//\\ __________________|");
            System.out.println("  | __   __    _  _     \\_  (_ .  _/ _    ___     _____|");
            System.out.println("  ||~~|_|..|__| || |_ _   \\ //\\\\ /  |=|__|~|~|___| | | |");
            System.out.println("  ||--|+|^^|==|1||2| | |__/\\ __ /\\__| |==|x|x|+|+|=|=|=|");
            System.out.println("  ||__|_|__|__|_||_|_| /  \\ \\  / /  \\_|__|_|_|_|_|_|_|_|");
            System.out.println("  |_________________ _/    \\/\\/\\/    \\_ _______________|");
            System.out.println("  | _____   _   __  |/      \\../      \\|  __   __   ___|");
            System.out.println("  ||_____|_| |_|##|_||   |   \\/ __|   ||_|==|_|++|_|-|||");
            System.out.println("  ||______||=|#|--| |\\   \\   o    /   /| |  |~|  | | |||");
            System.out.println("  ||______||_|_|__|_|_\\   \\  o   /   /_|_|__|_|__|_|_|||");
            System.out.println("  |_________ __________\\___\\____/___/___________ ______|");
            System.out.println("  |__    _  /    ________     ______           /| _ _ _|");
            System.out.println("  |\\ \\  |=|/   //    /| //   /  /  / |        / ||%|%|%|");
            System.out.println("  | \\/\\ |*/  .//____//.//   /__/__/ (_)      /  ||=|=|=|");
            System.out.println("__|  \\/\\|/   /(____|/ //                    /  /||~|~|~|__");
            System.out.println("  |___\\_/   /________//   ________         /  / ||_|_|_|");
            System.out.println("  |___ /   (|________/   |\\_______\\       /  /| |______|");
            System.out.println("      /                  \\|________)     /  / | |");
            System.out.println("=========================================");
            System.out.println("  중고 도서 마켓플레이스 프로그램에 오신 것을 환영합니다!  ");
            System.out.println("=========================================");

            while (true) {
                printMainMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1:
                        inquireStock(conn); // 재고 관리 기능 호출
                        break;
                    case 0:
                        System.out.println("프로그램을 종료합니다.");
                        return; // 프로그램 종료
                    default:
                        System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
                }
                System.out.println("\n=========================================\n");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("오류: JDBC 드라이버를 찾을 수 없습니다. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("오류: 데이터베이스 연결 또는 쿼리 실행 중 문제가 발생했습니다. " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    System.out.println("데이터베이스 연결이 종료되었습니다.");
                }
                if (scanner != null) {
                    scanner.close();
                }
            } catch (SQLException e) {
                System.err.println("데이터베이스 연결 종료 중 오류: " + e.getMessage());
            }
        }
    }

    // 메인 메뉴 출력
    private static void printMainMenu() {
        System.out.println("--- 메인 메뉴 ---");
        System.out.println("1. 재고 관리 (조회 및 할인)");
        System.out.println("0. 프로그램 종료");
        System.out.print("메뉴를 선택하세요: ");
    }

    // 사용자로부터 정수 입력 받기
    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("유효한 숫자를 입력해주세요.");
            scanner.next();
            System.out.print("메뉴를 선택하세요: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기
        return choice;
    }

    // 재고 조회 및 관리 기능
    private static void inquireStock(Connection conn) throws SQLException {
        while (true) { // 재고 조회 메뉴 루프
            System.out.println("\n--- 재고 관리 메뉴 ---");
            System.out.println("1. 도서 제목별 판매중 재고 현황 (기본 조회, 개수)");
            System.out.println("2. 도서 제목별 판매중 재고 요약 (GROUP BY, HAVING)");
            System.out.println("3. 평균 가격보다 비싼 판매중 도서 (SUBQUERY)");
            System.out.println("4. 평균 가격 초과 도서 일괄 할인 (UPDATE)");
            System.out.println("5. 작가별 판매 및 재고 종합 분석 (판매 완료 기준 랭킹)"); // 종합 분석
            System.out.println("6. 출판사별/출판년도별 판매 완료 PIVOT 분석 (OLAP: CASE 구문)"); // PIVOT OLAP - 판매 완료 기준으로 변경
            System.out.println("7. 도서 검색 (작가/제목)"); // 검색 기능
            System.out.println("0. 이전 메뉴로 돌아가기");
            System.out.print("메뉴를 선택하세요: ");

            int subChoice = getUserChoice();
            switch (subChoice) {
                case 1:
                    displayStockCountByTitle(conn);
                    break;
                case 2:
                    displayStockSummary(conn);
                    break;
                case 3:
                    displayExpensiveStock(conn);
                    break;
                case 4:
                    updateBookPrice(conn);
                    break;
                case 5:
                    displayAuthorSalesAndStockOverview(conn);
                    break;
                case 6: // PIVOT OLAP 호출 (판매 완료 기준)
                    displayPublisherPublishedYearPivot(conn);
                    break;
                case 7: // 도서 검색 기능 호출
                    searchBooksByAuthorOrTitle(conn);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
            }
            System.out.println("\n------------------------------------\n");
        }
    }

    // 도서 제목별 판매중 재고 현황 (기본 조회, 개수) - 재고 0권 포함
    private static void displayStockCountByTitle(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            System.out.println("\n--- 도서 제목별 판매중 재고 현황 ---");
            String sql = """
                SELECT
                    b.title AS book_title,
                    b.author,
                    b.publisher,
                    COALESCE(COUNT(ub.used_book_id), 0) AS available_stock_count,
                    COALESCE(SUM(ub.price), 0) AS total_stock_amount_current_price
                FROM
                    Book b
                LEFT JOIN
                    UsedBook ub ON b.book_id = ub.book_id AND ub.status = '판매중'
                GROUP BY
                    b.book_id, b.title, b.author, b.publisher
                ORDER BY
                    book_title;
            """;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            printResultSet(rs);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
        }
    }

    // 도서 제목별 판매중 재고 요약 (GROUP BY, HAVING)
    private static void displayStockSummary(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            System.out.println("\n--- 도서 제목별 판매중 재고 요약 (재고 2권 이상) ---");
            String sql = """
                SELECT
                    b.title AS book_title,
                    COUNT(ub.used_book_id) AS available_stock_count,
                    SUM(ub.price) AS total_available_stock_amount
                FROM
                    UsedBook ub
                JOIN
                    Book b ON ub.book_id = b.book_id
                WHERE
                    ub.status = '판매중'
                GROUP BY
                    b.title
                HAVING
                    available_stock_count >= 2;
            """;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            printResultSet(rs);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
        }
    }

    // 평균 가격보다 비싼 판매중 도서 (SUBQUERY)
    private static void displayExpensiveStock(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            System.out.println("\n--- 평균 가격보다 비싼 판매중 도서 ---");
            String sql = """
                SELECT
                    ub.used_book_id,
                    b.title AS book_title,
                    ub.price,
                    ub.status
                FROM
                    UsedBook ub
                JOIN
                    Book b ON ub.book_id = b.book_id
                WHERE
                    ub.status = '판매중' AND ub.price > (SELECT AVG(price) FROM UsedBook WHERE status = '판매중');
            """;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            printResultSet(rs);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
        }
    }

    // 평균 가격 초과 도서 일괄 할인 기능 (UPDATE)
    private static void updateBookPrice(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement selectAfterPstmt = null;
        ResultSet selectAfterRs = null;
        try {
            System.out.println("\n--- 평균 가격 초과 도서 일괄 할인 ---");

            System.out.println("--- 할인 대상 도서 목록 (할인 전 가격) ---");
            String selectBeforeDiscountSql = """
                SELECT ub.used_book_id, b.title, ub.price
                FROM UsedBook ub
                JOIN Book b ON ub.book_id = b.book_id
                WHERE ub.status = '판매중' AND ub.price > (SELECT AVG(price) FROM UsedBook WHERE status = '판매중');
            """;
            pstmt = conn.prepareStatement(selectBeforeDiscountSql);
            rs = pstmt.executeQuery();

            // 할인 대상 도서 ID들을 저장
            List<Integer> eligibleBookIds = new ArrayList<>();
            while(rs.next()) {
                eligibleBookIds.add(rs.getInt("used_book_id"));
            }
            // rs는 이미 끝까지 읽었으므로 닫고, printResultSet을 위해 다시 열 필요 없음
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); } rs = null; }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); } pstmt = null; }

            // 할인 대상 도서 목록 (할인 전 가격)을 다시 조회하여 출력
            if (!eligibleBookIds.isEmpty()) {
                StringBuilder inClause = new StringBuilder();
                inClause.append("(");
                for (int i = 0; i < eligibleBookIds.size(); i++) {
                    inClause.append(eligibleBookIds.get(i));
                    if (i < eligibleBookIds.size() - 1) {
                        inClause.append(",");
                    }
                }
                inClause.append(")");

                String selectBeforePrintSql = """
                    SELECT ub.used_book_id, b.title, ub.price
                    FROM UsedBook ub
                    JOIN Book b ON ub.book_id = b.book_id
                    WHERE ub.used_book_id IN """ + inClause.toString() + """;
                    """;
                pstmt = conn.prepareStatement(selectBeforePrintSql);
                rs = pstmt.executeQuery();
                printResultSet(rs); // 할인 전 가격 목록 출력
            } else {
                System.out.println("할인 대상 도서가 없습니다.");
                return; // 할인 대상이 없으면 종료
            }


            System.out.print("적용할 할인율 (예: 10% 할인은 0.1 입력, 취소하려면 0 입력): ");
            double discountRate = scanner.nextDouble();
            scanner.nextLine();

            if (discountRate <= 0 || discountRate >= 1) {
                System.out.println("할인 적용을 취소하거나 유효한 할인율이 아닙니다. 0보다 크고 1보다 작은 값을 입력하세요.");
                return;
            }

            System.out.print("위 도서들에 " + (discountRate * 100) + "% 할인을 적용하시겠습니까? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("yes")) {
                System.out.println("할인 적용을 취소합니다.");
                return;
            }

            String updateSql = """
                UPDATE UsedBook
                SET price = FLOOR(price * ?)
                WHERE status = '판매중' AND used_book_id IN (
                    SELECT used_book_id FROM (
                        SELECT ub_sub.used_book_id
                        FROM UsedBook AS ub_sub
                        WHERE ub_sub.status = '판매중' AND ub_sub.price > (SELECT AVG(price) FROM UsedBook WHERE status = '판매중')
                    ) AS tmp_eligible_books
                );
            """;
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setDouble(1, (1 - discountRate));
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + "개 도서의 가격이 할인되었습니다.");

            // 할인 적용 후, 해당 도서들의 새로운 가격을 조회하여 출력
            if (rowsAffected > 0 && !eligibleBookIds.isEmpty()) {
                System.out.println("\n--- 할인 적용 후 가격 (할인된 도서들) ---");
                StringBuilder inClauseAfter = new StringBuilder();
                inClauseAfter.append("(");
                for (int i = 0; i < eligibleBookIds.size(); i++) {
                    inClauseAfter.append(eligibleBookIds.get(i));
                    if (i < eligibleBookIds.size() - 1) {
                        inClauseAfter.append(",");
                    }
                }
                inClauseAfter.append(")");

                String selectAfterDiscountSql = """
                    SELECT ub.used_book_id, b.title, ub.price
                    FROM UsedBook ub
                    JOIN Book b ON ub.book_id = b.book_id
                    WHERE ub.used_book_id IN """ + inClauseAfter.toString() + """;
                    """;
                selectAfterPstmt = conn.prepareStatement(selectAfterDiscountSql);
                selectAfterRs = selectAfterPstmt.executeQuery();
                printResultSet(selectAfterRs); // 할인 후 가격 목록 출력
            }

        } finally {
            // 모든 자원 닫기
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
            if (selectAfterRs != null) try { selectAfterRs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (selectAfterPstmt != null) try { selectAfterPstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
        }
    }

    // 작가별 판매 및 재고 종합 분석 (판매 완료 기준 랭킹)
    private static void displayAuthorSalesAndStockOverview(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            System.out.println("\n--- 작가별 판매 및 재고 종합 분석 (판매 완료 기준 랭킹) ---");
            String sql = """
                SELECT
                    AuthorSales.author_name,
                    AuthorSales.total_sales_count,
                    AuthorSales.total_sales_amount, -- 총 판매 금액 합계
                    COALESCE(AuthorStock.total_current_stock_amount, 0) AS total_current_stock_amount,
                    COALESCE(AuthorStock.current_stock_count, 0) AS current_stock_count,
                    RANK() OVER (ORDER BY AuthorSales.total_sales_count DESC) AS sales_rank
                FROM
                    ( -- 작가별 총 판매 건수 및 총 판매 금액 합계
                        SELECT
                            b.author AS author_name,
                            COUNT(pi.purchase_id) AS total_sales_count, -- PurchaseItem에서 구매 건수 집계
                            SUM(pi.final_price) AS total_sales_amount -- PurchaseItem에서 최종 금액 합계
                        FROM
                            PurchaseOrder po -- PurchaseOrder 사용
                        JOIN
                            PurchaseItem pi ON po.purchase_id = pi.purchase_id -- PurchaseItem 조인
                        JOIN
                            UsedBook ub ON pi.used_book_id = ub.used_book_id
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        GROUP BY
                            b.author
                    ) AS AuthorSales
                LEFT JOIN
                    ( -- 작가별 현재 판매중인 재고 총액 및 개수
                        SELECT
                            b.author AS author_name,
                            SUM(ub.price) AS total_current_stock_amount,
                            COUNT(ub.used_book_id) AS current_stock_count
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            ub.status = '판매중'
                        GROUP BY
                            b.author
                    ) AS AuthorStock ON AuthorSales.author_name = AuthorStock.author_name
                ORDER BY
                    AuthorSales.total_sales_count DESC;
            """;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            printResultSet(rs);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
        }
    }

    // 출판사별/출판년도별 판매 완료 PIVOT 분석 (OLAP: CASE 구문)
    private static void displayPublisherPublishedYearPivot(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            System.out.println("\n--- 출판사별/출판년도별 판매 완료 PIVOT 분석 (OLAP: CASE 구문) ---");
            String pivotSql = """
                SELECT
                    b.publisher,
                    SUM(CASE WHEN b.published_date = 2020 THEN 1 ELSE 0 END) AS Year_2020_Count,
                    SUM(CASE WHEN b.published_date = 2020 THEN pi.final_price ELSE 0 END) AS Year_2020_Amount,
                    SUM(CASE WHEN b.published_date = 2021 THEN 1 ELSE 0 END) AS Year_2021_Count,
                    SUM(CASE WHEN b.published_date = 2021 THEN pi.final_price ELSE 0 END) AS Year_2021_Amount,
                    SUM(CASE WHEN b.published_date = 2022 THEN 1 ELSE 0 END) AS Year_2022_Count,
                    SUM(CASE WHEN b.published_date = 2022 THEN pi.final_price ELSE 0 END) AS Year_2022_Amount,
                    COUNT(pi.used_book_id) AS Total_Count, -- PurchaseItem 기준 총 개수
                    SUM(pi.final_price) AS Total_Amount -- PurchaseItem 기준 총 금액
                FROM
                    PurchaseOrder po
                JOIN
                    PurchaseItem pi ON po.purchase_id = pi.purchase_id
                JOIN
                    UsedBook ub ON pi.used_book_id = ub.used_book_id
                JOIN
                    Book b ON ub.book_id = b.book_id
                WHERE
                    ub.status = '판매완료' -- 판매 완료된 도서만 조회
                GROUP BY
                    b.publisher
                ORDER BY
                    b.publisher;
            """;
            pstmt = conn.prepareStatement(pivotSql);
            rs = pstmt.executeQuery();
            printResultSet(rs);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
        }
    }

    // 도서 검색 (작가/제목)
    private static void searchBooksByAuthorOrTitle(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            System.out.println("\n--- 도서 검색 (작가/제목) ---");
            System.out.print("검색할 키워드를 입력하세요: ");
            String keyword = scanner.nextLine();
            String searchKeyword = "%" + keyword + "%"; // LIKE 연산자를 위한 와일드카드

            System.out.println("검색 기준을 선택하세요:");
            System.out.println("  (1) 제목만 검색");
            System.out.println("  (2) 작가만 검색");
            System.out.println("  (3) 제목 또는 작가 검색");
            System.out.print("선택: ");
            int searchType = getUserChoice();

            String sql = "";
            switch (searchType) {
                case 1: // 제목만 검색
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            b.title LIKE ? AND ub.status = '판매중';
                        """;
                    break;
                case 2: // 작가만 검색
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            b.author LIKE ? AND ub.status = '판매중';
                        """;
                    break;
                case 3: // 제목 또는 작가 검색
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            (b.title LIKE ? OR b.author LIKE ?) AND ub.status = '판매중';
                        """;
                    break;
                default:
                    System.out.println("잘못된 검색 기준입니다. 전체 검색으로 진행합니다.");
                    sql = """
                        SELECT
                            ub.used_book_id,
                            b.title AS book_title,
                            b.author,
                            b.publisher,
                            ub.price,
                            ub.status,
                            ub.registered_date
                        FROM
                            UsedBook ub
                        JOIN
                            Book b ON ub.book_id = b.book_id
                        WHERE
                            (b.title LIKE ? OR b.author LIKE ?) AND ub.status = '판매중';
                        """;
                    searchType = 3; // 기본값으로 설정
                    break;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, searchKeyword);
            if (searchType == 3) {
                pstmt.setString(2, searchKeyword); // OR 조건일 경우 두 번째 파라미터 바인딩
            }
            rs = pstmt.executeQuery();
            printResultSet(rs);

        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
        }
    }


    // ResultSet을 콘솔에 깔끔하게 출력하는 헬퍼 메소드
    private static void printResultSet(ResultSet rs) throws SQLException {
        // ResultSet이 비어있는지 확인
        if (!rs.next()) { // 첫 행으로 이동 시도, 데이터가 없으면 false 반환
            System.out.println("조회된 결과가 없습니다.");
            return;
        }

        // 컬럼 메타데이터 가져오기
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // 각 컬럼의 실제 데이터 길이를 저장할 리스트
        List<String[]> rows = new ArrayList<>();
        // 첫 행은 이미 rs.next()로 읽었으므로 추가
        do {
            String[] rowData = new String[columnCount + 1]; // 1-based index
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                rowData[i] = (value != null) ? value.toString() : "NULL";
            }
            rows.add(rowData);
        } while (rs.next()); // 나머지 행 읽기

        // 각 컬럼의 최종 너비를 저장할 배열 (최소 너비, 최대 너비 설정)
        int[] columnWidths = new int[columnCount + 1];

        // 컬럼별 너비 설정 상수 (조정 가능)
        final int MIN_COL_WIDTH = 15; // 최소 컬럼 너비
        final int MAX_COL_WIDTH_TITLE = 40; // 'book_title'의 최대 너비
        final int MAX_COL_WIDTH_GENERAL = 25; // 일반 컬럼의 최대 너비


        // 헤더 너비 초기화 및 데이터 최대 길이 파악
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            int currentCalculatedWidth = 0;

            // 컬럼 라벨에 따라 최대 너비 설정
            if ("book_title".equalsIgnoreCase(label) || "title".equalsIgnoreCase(label)) { // 제목 컬럼
                currentCalculatedWidth = MAX_COL_WIDTH_TITLE;
            } else { // 기타 컬럼
                currentCalculatedWidth = MAX_COL_WIDTH_GENERAL;
            }
            
            // 헤더 길이와 최소 너비 중 큰 값으로 초기화
            columnWidths[i] = Math.max(label.length(), MIN_COL_WIDTH);
            // 계산된 최대 너비와 실제 데이터 길이 중 작은 값으로 최종 너비 결정
            columnWidths[i] = Math.min(columnWidths[i], currentCalculatedWidth);


            for (String[] rowData : rows) {
                String cellValue = rowData[i];
                columnWidths[i] = Math.min(Math.max(columnWidths[i], cellValue.length()), currentCalculatedWidth);
            }
        }

        // 헤더 출력
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(String.format("%-" + columnWidths[i] + "s ", metaData.getColumnLabel(i)));
        }
        System.out.println();

        // 구분선 출력
        for (int i = 1; i <= columnCount; i++) {
            for (int j = 0; j < columnWidths[i]; j++) {
                System.out.print("-");
            }
            System.out.print(" "); // 컬럼 간 공백
        }
        System.out.println();

        // 데이터 출력
        for (String[] rowData : rows) {
            for (int i = 1; i <= columnCount; i++) {
                String cellValue = rowData[i];
                // 최종 너비를 초과하면 ...으로 자르기
                if (cellValue.length() > columnWidths[i]) {
                    cellValue = cellValue.substring(0, columnWidths[i] - 3) + "...";
                }
                System.out.print(String.format("%-" + columnWidths[i] + "s ", cellValue));
            }
            System.out.println();
        }
    }
}
