package Round;

import java.util.ArrayList;
import java.util.List;

import People.Judge;
import People.PersonStore;
import People.Team;

public class Round {
    private List<Team> unmatchedTeams = new ArrayList<>();
    private List<Judge> unmatchedJudges = new ArrayList<>();
    private List<Match> matches = new ArrayList<>();

    public Round(PersonStore people) {
        unmatchedTeams = people.GetTeams();
        unmatchedJudges = people.GetJudges();
    }

    public void AddMatch(Match match) {
        matches.add(match);
        unmatchedTeams.remove(match.affirmativeTeam);
        unmatchedTeams.remove(match.negativeTeam);
        unmatchedJudges.remove(match.judge);
    }

    public boolean IsTeamUnmatched(Team team) {
        return unmatchedTeams.contains(team);
    }

    public boolean JudgeJudgesTeam(Judge judge, Team team) {
        for (Match match : matches) {
            if (match.ContainsTeam(team) && match.judge == judge) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        String s = "Unmatched Teams:  ";

        for (Team team : unmatchedTeams) {
            s += team + ", ";
        }

        s = s.substring(0, s.length() - 2) + "\nUnmatched Judges:  ";

        for (Judge judge : unmatchedJudges) {
            s += judge + ", ";
        }

        s = s.substring(0, s.length() - 2) + "\n\n";

        for (Match match : matches) {
            s += match + "\n";
        }

        return s;
    }
}
