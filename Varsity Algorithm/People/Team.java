package People;

import School.School;

public class Team {
    public School school;
    public int rank;
    public int wins;

    public Team(School school, int rank) {
        this.school = school;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "" + school + rank;
    }
}