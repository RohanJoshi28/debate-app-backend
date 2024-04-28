package Round;

import java.util.ArrayList;
import java.util.List;

import People.Judge;
import People.Team;

public class Round {
    public List<Match> matches = new ArrayList<>();

    public boolean IsTeamMatched(Team team) {
        for (Match match : matches) {
            if (match.ContainsTeam(team)) {
                return true;
            }
        }

        return false;
    }

    public boolean IsJudgeMatched(Judge judge) {
        for (Match match : matches) {
            if (match.judge == judge) {
                return true;
            }
        }

        return false;
    }

    public boolean JudgeJudgesTeam(Judge judge, Team team) {
        for (Match match : matches) {
            if (match.ContainsTeam(team) && match.judge == judge) {
                return true;
            }
        }

        return false;
    }

    public boolean AreTeamsMatched(Team team1, Team team2) {
        for (Match match : matches) {
            if (match.ContainsTeam(team1) && match.ContainsTeam(team2)) {
                return true;
            }
        }

        return false;
    }

    public boolean IsTeamAffirmative(Team team) {
        for (Match match : matches) {
            if (match.affirmativeTeam == team) {
                return true;
            }
        }

        return false;
    }

    public boolean IsTeamNegative(Team team) {
        for (Match match : matches) {
            if (match.negativeTeam == team) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (Match match : matches) {
            stringBuilder.append(match + "\n");
        }

        return stringBuilder.toString();
    }
}
