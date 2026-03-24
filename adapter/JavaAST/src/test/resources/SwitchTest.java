public class SwitchTest {

    public static void main(String[] args) {
        int i = 0;

        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
                System.out.println("switch");
            case 4:
            case 5:
                System.out.println("break");
                break;
            case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    int second = 99;
                    switch (second) {
                        case 0: case 1: case 2:
                            System.out.println("switch2");
                            break;
                        default:
                                System.out.println("default2");
                    }
                    break;
             default:
                 System.out.println("default");
        }
    }
}
