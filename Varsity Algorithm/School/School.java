package School;

public class School {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int index;

    public School(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return ALPHABET.charAt(index) + "";
    }
}
