public class LoopTest {

    public static void main(String[] args) {
        do {
            System.out.println("this is a loop test");
            for (int i = 1; i < 10; i++) {
                System.out.println(2 * i);
                while (false) {
                    System.out.println(" ");
                }
            }
        } while (true);

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
        while (true) {
            System.out.println();
        }
    }
}
