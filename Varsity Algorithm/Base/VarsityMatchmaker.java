package Base;

import Matchmaking.RoundGenerator;
import People.PersonStore;
import Round.Round;
import School.SchoolArg;

public class VarsityMatchmaker {
    private PersonStore people;

    public VarsityMatchmaker(SchoolArg[] schoolArgs) {
        people = new PersonStore(schoolArgs);
    }

    public RoundSet CreateRoundSet() {
        RoundGenerator roundGenerator = new RoundGenerator(people);

        Round round1 = roundGenerator.Generate(null);
        Round round2 = roundGenerator.Generate(round1);

        return new RoundSet(round1, round2);
    }

    public void SetTeamWins(int[] wins) {
        people.SetTeamWins(wins);
    }
}