package Base;

import Matchmaking.RoundGenerator;
import People.PersonStore;
import Round.Round;
import School.SchoolArg;

public class VarsityMatchmaker {
    public Tournament CreateTournament(SchoolArg[] schoolArgs) {
        PersonStore people = new PersonStore(schoolArgs);
        RoundGenerator roundGenerator = new RoundGenerator(people);

        Round round1 = roundGenerator.Generate(null);
        Round round2 = roundGenerator.Generate(round1);

        return new Tournament(round1, round2);
    }
}