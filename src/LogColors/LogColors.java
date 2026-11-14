package LogColors;

public class LogColors {
    // Uses ANSI-color codes
    // RESET is used at the end of a colored message.
    private static final String RESET = "\u001B[0m";

    // Text Colors (Standard)
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    // Styles
    private static final String BOLD = "\u001B[1m";
    private static final String UNDERLINE = "\u001B[4m";

    // Background colors
    private static final String BG_BLACK = "\u001B[40m";
    private static final String BG_RED = "\u001B[41m";
    private static final String BG_GREEN = "\u001B[42m";
    private static final String BG_YELLOW = "\u001B[43m";
    private static final String BG_BLUE = "\u001B[44m";
    private static final String BG_PURPLE = "\u001B[45m";
    private static final String BG_CYAN = "\u001B[46m";
    private static final String BG_WHITE = "\u001B[47m";

    public static void printPurple(String message){ System.out.printf("%s%s%n", PURPLE + BOLD, message + RESET);}

    public static void printInfo(String message){
        System.out.printf("%s%s%n",BLUE + BOLD, message + RESET);
    }

    public static void printSuccess(String message) { System.out.printf("%s%s%n", GREEN + BOLD, message + RESET); }

    public static void printWarn(String message){
        System.out.printf("%s[WARN] %s%n", YELLOW + BOLD, message + RESET);
    }

    public static void printError(String message){ System.out.printf("%s[ERROR] %s%n", RED + BOLD, message + LogColors.RESET);
    }

    // Add trace?
}