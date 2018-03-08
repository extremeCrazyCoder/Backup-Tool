
public class ErrorHandler {
    public static void showErr(Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    public static void showDebug(String debug) {
        System.out.println(debug);
    }
}
