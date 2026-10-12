package com.dgca.flightlogbook.ui;

public class Session {
    private static String token;
    private static Long userId;
    private static String fullName;
    private static String role;

    public static void set(String tokenVal, Long userIdVal, String fullNameVal, String roleVal) {
        token = tokenVal;
        userId = userIdVal;
        fullName = fullNameVal;
        role = roleVal;
    }

    public static String getToken() {
        return token;
    }

    public static Long getUserId() {
        return userId;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getRole() {
        return role;
    }

    public static boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public static void clear() {
        token = null;
        userId = null;
        fullName = null;
        role = null;
    }
}
