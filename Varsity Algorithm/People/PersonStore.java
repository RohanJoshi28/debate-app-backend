package People;
import java.util.ArrayList;
import java.util.List;

import School.School;
import School.SchoolArg;

public class PersonStore {
    private List<Team> teams = new ArrayList<>();
    private List<Judge> judges = new ArrayList<>();

    public PersonStore(SchoolArg[] schoolArgs) {
        for (int schoolIndex = 0; schoolIndex < schoolArgs.length; schoolIndex++) {
            //make school
            SchoolArg schoolArg = schoolArgs[schoolIndex];
            School school = new School(schoolIndex);

            //make teams
            for (int rank = 0; rank < schoolArg.teamCount; rank++) {
                Team team = new Team(school, rank);
                teams.add(team);
            }

            //make judges
            for (int rank = 0; rank < schoolArg.judgeCount; rank++) {
                Judge judge = new Judge(school, rank);
                judges.add(judge);
            }
        }        
    }

    public List<Team> GetTeams() {
        return new ArrayList<>(teams);
    }

    public List<Judge> GetJudges() {
        return new ArrayList<>(judges);
    }
}