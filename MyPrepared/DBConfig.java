package MyPrepared;

public class DBConfig {
	public static String userID="testuser";
	public static String userPW="testpw";
	public static String dbName="bookstoredb";
	public static String header="jdbc:mysql://localhost:3306/";
	public static String encoding="useUnicode=true&characterEncoding=UTF-8";
	public static String url=header+dbName+"?"+encoding;

}
