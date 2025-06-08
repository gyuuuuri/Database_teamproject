package MyPrepared;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class PrintResultSet {
	// ResultSet을 콘솔에 깔끔하게 출력하는 헬퍼 메소드
    public static void printResultSet(ResultSet rs) throws SQLException {
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
                if (value instanceof Integer) {
                    rowData[i] = Integer.toString((Integer) value);
                } else if (value instanceof java.sql.Date) {
                    // 연도만 추출
                    rowData[i] = String.valueOf(((java.sql.Date) value).toLocalDate().getYear());
                } else {
                    rowData[i] = (value != null) ? value.toString() : "NULL";
                }
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
