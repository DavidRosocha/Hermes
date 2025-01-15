package utils;

public class AccountUtil {
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "XPI76SZUqyCjVxgnUjm0";
    public static boolean checkAdmin(String username, String password) {
        return ADMIN_USER.equals(username) && ADMIN_PASSWORD.equals(password);
    }
}
