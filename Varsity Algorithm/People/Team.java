package People;

import School.School;

public class Team {
    private School school;
    private int rank;

    public Team(School school, int rank) {
        this.school = school;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return school.toString() + rank;
    }
}