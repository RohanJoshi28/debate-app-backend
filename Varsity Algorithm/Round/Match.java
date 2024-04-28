package Round;

import People.Judge;
import People.Team;

public class Match {
    public Team affirmativeTeam;
    public Team negativeTeam;
    public Judge judge;

    public Match(Team affirmativeTeam, Team negativeTeam, Judge judge) {
        this.affirmativeTeam = affirmativeTeam;
        this.negativeTeam = negativeTeam;
        this.judge = judge;
    }

    public boolean ContainsTeam(Team team) {
        return affirmativeTeam == team || negativeTeam == team;
    }

    @Override
    public String toString() {
        return "(" + affirmativeTeam + ", " + negativeTeam + " : " + judge + ")";
    }
}
