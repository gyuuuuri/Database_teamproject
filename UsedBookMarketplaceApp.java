import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

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
        String dbName = "bookstore"; 
        String dbParams = "useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?" + dbParams;

        String dbUser = "root"; 
        String dbPassword = "1234"; 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // ResultSet을 스크롤 가능하게 설정 (TYPE_SCROLL_INSENSITIVE)
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            // conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword,
            //     java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

            printAsciiArt(); // 아스키아트 출력
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

    // 아스키아트 출력 메소드
    private static void printAsciiArt() {
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
            System.out.println("5. 작가별 판매 및 재고 종합 분석 (판매 완료 기준 랭킹)");
            System.out.println("6. 출판사별/출판년도별 판매중 재고 분석");
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
                case 6:
                    displayPublisherPublishedYearPivot(conn);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
            }
            System.out.println("\n------------------------------------\n");
        }
    }

    // 도서 제목별 판매중 재고 현황 (기본 조회, 개수)
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
                    COUNT(ub.used_book_id) AS available_stock_count,
                    SUM(ub.price) AS total_stock_amount_current_price
                FROM
                    UsedBook ub
                JOIN
                    Book b ON ub.book_id = b.book_id
                WHERE
                    ub.status = '판매중'
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
            printResultSet(rs);

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
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { System.err.println("ResultSet 닫기 오류: " + e.getMessage()); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { System.err.println("PreparedStatement 닫기 오류: " + e.getMessage()); }
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
                    COALESCE(AuthorStock.total_current_stock_amount, 0) AS total_current_stock_amount,
                    COALESCE(AuthorStock.current_stock_count, 0) AS current_stock_count,
                    RANK() OVER (ORDER BY AuthorSales.total_sales_count DESC) AS sales_rank
                FROM
                    ( -- 작가별 총 판매 건수
                        SELECT
                            b.author AS author_name,
                            COUNT(p.purchase_id) AS total_sales_count
                        FROM
                            Purchase p
                        JOIN
                            UsedBook ub ON p.used_book_id = ub.used_book_id
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

    // 출판사별/출판년도별 판매중 재고 분석
    private static void displayPublisherPublishedYearPivot(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            System.out.println("\n--- 출판사별/출판년도별 판매중 재고 분석 ---");
            String pivotSql = """
                SELECT
                    b.publisher,
                    SUM(CASE WHEN b.published_date = 2020 THEN 1 ELSE 0 END) AS Year_2020_Count,
                    SUM(CASE WHEN b.published_date = 2020 THEN ub.price ELSE 0 END) AS Year_2020_Amount,
                    SUM(CASE WHEN b.published_date = 2021 THEN 1 ELSE 0 END) AS Year_2021_Count,
                    SUM(CASE WHEN b.published_date = 2021 THEN ub.price ELSE 0 END) AS Year_2021_Amount,
                    SUM(CASE WHEN b.published_date = 2022 THEN 1 ELSE 0 END) AS Year_2022_Count,
                    SUM(CASE WHEN b.published_date = 2022 THEN ub.price ELSE 0 END) AS Year_2022_Amount,
                    COUNT(ub.used_book_id) AS Total_Count,
                    SUM(ub.price) AS Total_Amount
                FROM
                    UsedBook ub
                JOIN
                    Book b ON ub.book_id = b.book_id
                WHERE
                    ub.status = '판매중'
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

        // 고정된 컬럼 너비 설정 (조정 가능)
        int defaultColumnWidth = 30; // 기본
        int maxColumnWidth = 30; // 최대

        // 헤더 출력
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            // 헤더 길이를 고려하여 최소 너비 확보, 최대 너비 제한
            int currentWidth = Math.min(Math.max(label.length(), defaultColumnWidth), maxColumnWidth);
            System.out.print(String.format("%-" + currentWidth + "s ", label));
        }
        System.out.println();

        // 구분선 출력
        for (int i = 1; i <= columnCount; i++) {
            String label = metaData.getColumnLabel(i);
            int currentWidth = Math.min(Math.max(label.length(), defaultColumnWidth), maxColumnWidth);
            for (int j = 0; j < currentWidth; j++) {
                System.out.print("-");
            }
            System.out.print(" "); // 컬럼 간 공백
        }
        System.out.println();

        // 데이터 출력 (첫 행은 이미 rs.next()로 읽었으므로 먼저 출력)
        do { // do-while 루프를 사용하여 첫 행부터 처리
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                String cellValue = (value != null) ? value.toString() : "NULL";

                String label = metaData.getColumnLabel(i); // 해당 컬럼의 헤더를 다시 가져와 너비 계산
                int currentWidth = Math.min(Math.max(label.length(), defaultColumnWidth), maxColumnWidth);

                // 최대 너비를 초과하면 ...으로 자르기
                if (cellValue.length() > currentWidth) {
                    cellValue = cellValue.substring(0, currentWidth - 3) + "...";
                }
                System.out.print(String.format("%-" + currentWidth + "s ", cellValue));
            }
            System.out.println();
        } while (rs.next()); // 다음 행이 있으면 계속
    }
}

