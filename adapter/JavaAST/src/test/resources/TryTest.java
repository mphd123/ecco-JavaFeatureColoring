import java.io.IOException;

public class TryTest  {

    public static void main(String[] args) {
        try {
            System.out.println("Hello World");
        } catch (IllegalAccessError e) {
            System.out.println("Access denied");
        } catch (InstantiationError e) {
            System.out.println("Instantiation error");
        } catch (RuntimeException e) {
            try {
                System.out.println("Runtime error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
