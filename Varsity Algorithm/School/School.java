package School;

public class School {
    private int index;

    public School(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }
}
