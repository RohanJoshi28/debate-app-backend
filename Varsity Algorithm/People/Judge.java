package People;

import School.School;

public class Judge {
    public School school;
    private int index;

    public Judge(School school, int index) {
        this.school = school;
        this.index = index;
    }

    @Override
    public String toString() {
        return "J" + school + index;
    }
}