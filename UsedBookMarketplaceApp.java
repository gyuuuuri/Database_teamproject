import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Random; // Random 클래스 추가
import java.time.LocalDate; // LocalDate 클래스 추가 (날짜 계산용)

public class UsedBookMarketplaceApp {

    // 데이터베이스 연결 정보 (실제 MySQL 정보로 변경해주세요!)
    // 예시: "jdbc:mysql://localhost:3306/your_database_name?serverTimezone=UTC"
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore?serverTimezone=UTC";
    private static final String DB_USER = "root"; // MySQL 사용자 이름
    private static final String DB_PASSWORD = "1234"; // MySQL 비밀번호

    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random(); // 랜덤 객체 생성

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // JDBC 드라이버 로드 (MySQL 8.0 이상에서는 보통 생략 가능하지만 명시적으로 작성)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(true); // 자동 커밋 설정 (각 쿼리마다 바로 데이터베이스에 반영)

            System.out.println("=========================================");
            System.out.println("  중고 도서 마켓플레이스 프로그램에 오신 것을 환영합니다!  ");
            System.out.println("=========================================");


            while (true) {
                printMainMenu();
                int choice = getUserChoice();

                switch (choice) {
                   
                        inquireStock(conn); 
                        break;

                    case 0:
                        System.out.println("프로그램을 종료합니다.");
                        return; // 프로그램 종료
                    default:
                        System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
                }
                System.out.println("\n=========================================\n"); // 메인 메뉴 구분선
            }

        } catch (ClassNotFoundException e) {
            System.err.println("오류: JDBC 드라이버를 찾을 수 없습니다. MySQL JDBC 드라이버가 클래스패스에 포함되어 있는지 확인하세요.");
            System.err.println("자세한 오류: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("오류: 데이터베이스 연결 또는 쿼리 실행 중 문제가 발생했습니다.");
            System.err.println("SQL 상태: " + e.getSQLState());
            System.err.println("오류 코드: " + e.getErrorCode());
            System.err.println("자세한 오류: " + e.getMessage());
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
        System.out.print("메뉴를 선택하세요: ");
    }

    // 사용자로부터 정수 입력을 받는 헬퍼 메소드
    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("유효한 숫자를 입력해주세요.");
            scanner.next(); // 잘못된 입력 버리기
            System.out.print("메뉴를 선택하세요: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기 (nextInt() 후 남아있는 개행 문자 처리)
        return choice;
    }

    // --- 재고 조회 및 관리 기능 (SELECT, UPDATE 등) ---
    // 프로젝트 요구사항: 최소 2개의 select 쿼리, 1개는 group by/having, 1개는 다중 테이블 조인
    // 서브쿼리, 윈도우 함수, 다차원 분석 OLAP 쿼리 포함
    // 이 메뉴에 머무르며 다양한 재고 관련 기능을 수행

    private static void inquireStock(Connection conn) throws SQLException {
        while (true) { // 재고 조회 메뉴에 머무르기 위한 루프
            System.out.println("\n--- 재고 관리 메뉴 ---");
            System.out.println("1. 도서 제목별 판매중 재고 현황 (기본 조회, 개수)"); // SELECT, JOIN, GROUP BY
            System.out.println("2. 도서 제목별 판매중 재고 요약 (GROUP BY, HAVING)"); // SELECT, JOIN, GROUP BY, HAVING
            System.out.println("3. 평균 가격보다 비싼 판매중 도서 (SUBQUERY)"); // SELECT, SUBQUERY
            System.out.println("4. 평균 가격 초과 도서 일괄 할인 (UPDATE)"); // UPDATE, SUBQUERY
            System.out.println("5. 출판사별 재고 가치 순위 (WINDOW FUNCTION)"); // SELECT, WINDOW FUNCTION
            System.out.println("6. 작가/출판년도별 판매중 재고 가치 집계 (OLAP: UNION ALL 우회)"); // SELECT, OLAP (UNION ALL)
            System.out.println("7. 작가/출판사별 판매중 재고 가치 집계 (OLAP: UNION ALL 우회)"); // SELECT, OLAP (UNION ALL)
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
                    updateBookPrice(conn); // 재고 조회 하위 기능으로 호출 (UPDATE 쿼리 포함)
                    break;
                case 5:
                    displayPublisherStockRank(conn);
                    break;
                case 6: // 작가/출판년도별 OLAP 쿼리 (UNION ALL 우회)
                    displayAuthorPublishedYearStockCube(conn);
                    break;
                case 7: // 작가/출판사별 OLAP 쿼리 (UNION ALL 우회)
                    displayAuthorPublisherStockCube(conn);
                    break;
                case 0:
                    return; // 이전 메뉴 (메인 메뉴)로 돌아가기
                default:
                    System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요.");
            }
            System.out.println("\n------------------------------------\n"); // 각 재고 기능 실행 후 구분선
        }
    }

    // 5.1 도서 제목별 판매중 재고 현황 (기본 조회, 개수) - SELECT, JOIN, GROUP BY
    // (기존의 "기본 재고 조회"를 개수 기반으로 변경)
    private static void displayStockCountByTitle(Connection conn) throws SQLException {
        System.out.println("\n--- 도서 제목별 판매중 재고 현황 ---");
        String sql = """
            SELECT
                b.title AS book_title,
                b.author,
                b.publisher,
                COUNT(ub.used_book_id) AS available_stock_count, -- 현재 판매중인 중고 도서의 개수
                SUM(ub.price) AS total_stock_value_current_price -- 현재 판매 가격 기준 총 재고 가치
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            GROUP BY
                b.book_id, b.title, b.author, b.publisher -- book_id로 그룹화하여 정확한 도서별 집계
            ORDER BY
                book_title;
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            printResultSet(rs);
        }
    }

    // 5.2 도서 제목별 판매중 재고 요약 (GROUP BY, HAVING) - SELECT, JOIN, GROUP BY, HAVING
    private static void displayStockSummary(Connection conn) throws SQLException {
        System.out.println("\n--- 도서 제목별 판매중 재고 요약 (재고 2권 이상) ---");
        String sql = """
            SELECT
                b.title AS book_title,
                COUNT(ub.used_book_id) AS available_stock_count,
                SUM(ub.price) AS total_price_of_available_stock -- 현재 판매 가격 기준 총 재고 가치
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            printResultSet(rs);
        }
    }

    // 5.3 평균 가격보다 비싼 판매중 도서 (SUBQUERY) - SELECT, SUBQUERY
    private static void displayExpensiveStock(Connection conn) throws SQLException {
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            printResultSet(rs);
        }
    }

    // 5.4 평균 가격 초과 도서 일괄 할인 기능 (UPDATE) - UPDATE, SUBQUERY
    // 프로젝트 요구사항: 최소 1개의 update 쿼리가 포함되어야 한다. (Prepared Statement 방식)
    private static void updateBookPrice(Connection conn) throws SQLException {
        System.out.println("\n--- 평균 가격 초과 도서 일괄 할인 ---");
        
        // 1. 할인을 적용할 대상 도서 목록 조회 (할인 전 가격)
        System.out.println("--- 할인 대상 도서 목록 (할인 전 가격) ---");
        String selectBeforeDiscountSql = """
            SELECT ub.used_book_id, b.title, ub.price
            FROM UsedBook ub
            JOIN Book b ON ub.book_id = b.book_id
            WHERE ub.status = '판매중' AND ub.price > (SELECT AVG(price) FROM UsedBook WHERE status = '판매중');
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(selectBeforeDiscountSql);
             ResultSet rs = pstmt.executeQuery()) {
            printResultSet(rs);
        }
        
        System.out.print("적용할 할인율 (예: 10% 할인은 0.1 입력, 취소하려면 0 입력): ");
        double discountRate = scanner.nextDouble();
        scanner.nextLine(); // 버퍼 비우기

        if (discountRate <= 0 || discountRate >= 1) { // 0이하 또는 1이상은 유효하지 않음
            System.out.println("할인 적용을 취소하거나 유효한 할인율이 아닙니다. 0보다 크고 1보다 작은 값을 입력하세요.");
            return;
        }
        
        System.out.print("위 도서들에 " + (discountRate * 100) + "% 할인을 적용하시겠습니까? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes")) {
            System.out.println("할인 적용을 취소합니다.");
            return;
        }

        // 2. UsedBook 테이블의 가격 업데이트 (UPDATE)
        // Secondary engine operation 오류 (1093) 해결을 위해 서브쿼리 한 번 더 래핑
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
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setDouble(1, (1 - discountRate)); // 할인율 적용: 1 - 할인율 (예: 0.9)
            int rowsAffected = pstmt.executeUpdate(); // 쿼리 실행
            System.out.println(rowsAffected + "개 도서의 가격이 할인되었습니다.");
        }
    }

    // 5.5 출판사별 재고 가치 순위 (WINDOW FUNCTION) - SELECT, WINDOW FUNCTION
    private static void displayPublisherStockRank(Connection conn) throws SQLException {
        System.out.println("\n--- 출판사별 재고 가격 순위 ---");
        String windowSql = """
            SELECT
                book_publisher,
                book_title,
                available_stock_count,
                total_price_of_available_stock,
                RANK() OVER (PARTITION BY book_publisher ORDER BY total_price_of_available_stock DESC) AS publisher_stock_rank
            FROM (
                SELECT
                    b.publisher AS book_publisher,
                    b.title AS book_title,
                    COUNT(ub.used_book_id) AS available_stock_count,
                    SUM(ub.price) AS total_price_of_available_stock -- 현재 판매 가격 기준 총 재고 가치
                FROM
                    UsedBook ub
                JOIN
                    Book b ON ub.book_id = b.book_id
                WHERE
                    ub.status = '판매중'
                GROUP BY
                    b.publisher, b.title
            ) AS SubqueryAlias
            WHERE
                available_stock_count > 0;
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(windowSql);
             ResultSet rs = pstmt.executeQuery()) {
            printResultSet(rs);
        }
    }

    // 5.6 작가/출판년도별 판매중 재고 가치 집계 (OLAP: UNION ALL 우회)
    private static void displayAuthorPublishedYearStockCube(Connection conn) throws SQLException {
        System.out.println("\n--- 작가/출판년도별 판매중 재고 가치 집계 (OLAP: UNION ALL 우회) ---");
        // 강의 자료에서 다룬 UNION ALL 또는 CASE 구문 활용 방식으로 CUBE/ROLLUP 기능 우회
        String unionAllSql = """
            -- 1. 작가별/출판년도별 집계
            SELECT
                b.author AS author,
                b.published_date AS published_year,
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            GROUP BY b.author, b.published_date

            UNION ALL

            -- 2. 작가별 총계 (published_year = NULL)
            SELECT
                b.author AS author,
                NULL AS published_year, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            GROUP BY b.author

            UNION ALL

            -- 3. 출판년도별 총계 (author = NULL)
            SELECT
                NULL AS author, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                b.published_date AS published_year,
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            GROUP BY b.published_date

            UNION ALL

            -- 4. 전체 총계 (author = NULL, published_year = NULL)
            SELECT
                NULL AS author, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                NULL AS published_year, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            ORDER BY author, published_year;
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(unionAllSql);
             ResultSet rs = pstmt.executeQuery()) {
            printResultSet(rs);
        }
    }

    // 5.7 작가/출판사별 판매중 재고 가치 집계 (OLAP: UNION ALL 우회)
    private static void displayAuthorPublisherStockCube(Connection conn) throws SQLException {
        System.out.println("\n--- 작가/출판사별 판매중 재고 가치 집계 (OLAP: UNION ALL 우회) ---");
        // 강의 자료에서 다룬 UNION ALL 또는 CASE 구문 활용 방식으로 CUBE/ROLLUP 기능 우회
        String unionAllSql = """
            -- 1. 작가별/출판사별 집계
            SELECT
                b.author AS author,
                b.publisher AS publisher,
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            GROUP BY b.author, b.publisher

            UNION ALL

            -- 2. 작가별 총계 (publisher = NULL)
            SELECT
                b.author AS author,
                NULL AS publisher, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            GROUP BY b.author

            UNION ALL

            -- 3. 출판사별 총계 (author = NULL)
            SELECT
                NULL AS author, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                b.publisher AS publisher,
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            GROUP BY b.publisher

            UNION ALL

            -- 4. 전체 총계 (author = NULL, publisher = NULL)
            SELECT
                NULL AS author, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                NULL AS publisher, -- 'NULL' 문자열 대신 실제 NULL 값 사용
                COUNT(ub.used_book_id) AS total_used_books,
                SUM(ub.price) AS total_stock_value
            FROM
                UsedBook ub
            JOIN
                Book b ON ub.book_id = b.book_id
            WHERE
                ub.status = '판매중'
            ORDER BY author, publisher;
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(unionAllSql);
             ResultSet rs = pstmt.executeQuery()) {
            printResultSet(rs);
        }
    }

    // ResultSet을 콘솔에 깔끔하게 출력하는 헬퍼 메소드
    private static void printResultSet(ResultSet rs) throws SQLException {
        // 결과셋이 비어있는지 확인 (rs.isBeforeFirst()는 커서가 첫 행 앞에 있는지 확인)
        boolean hasResults = rs.isBeforeFirst(); // isBeforeFirst()는 커서가 첫 행 앞에 있을 때 true

        if (!hasResults) {
            System.out.println("조회된 결과가 없습니다.");
            return;
        }

        // 컬럼 메타데이터 가져오기
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // 헤더 출력
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(String.format("%-25s", metaData.getColumnLabel(i))); // 컬럼 라벨 사용
        }
        System.out.println();
        // 구분선 출력
        for (int i = 0; i < columnCount * 25; i++) {
            System.out.print("-");
        }
        System.out.println();

        // 데이터 출력
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                // NULL 값은 "NULL" 문자열로 표시
                System.out.print(String.format("%-25s", value != null ? value.toString() : "NULL"));
            }
            System.out.println();
        }
    }
}
