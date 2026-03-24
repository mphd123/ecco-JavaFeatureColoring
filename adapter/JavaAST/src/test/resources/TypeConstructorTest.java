public class TypeConstructorTest {
    private String data;
    private int rank;

    public <E extends Rankable & Serializable> TypeConstructorTest(E element) {
        this.data = element.toString();
        this.rank = element.getRank();
    }

}