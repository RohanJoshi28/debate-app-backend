package People;
import java.util.ArrayList;
import java.util.List;

import School.School;
import School.SchoolArg;

public class PersonGenerator {
    public List<Team> teams = new ArrayList<>();

    public PersonGenerator(SchoolArg[] schoolArgs) {
        for (int schoolIndex = 0; schoolIndex < schoolArgs.length; schoolIndex++) {
            SchoolArg schoolArg = schoolArgs[schoolIndex];
            School school = new School(schoolIndex);

            for (int rank = 0; rank < schoolArg.teamCount; rank++) {
                Team team = new Team(school, rank);
                teams.add(team);
            }
        }
    }
}
