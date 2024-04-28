package Base;

import Round.Round;

public class Tournament {
    public Round round1;
    public Round round2;

    public Tournament(Round round1, Round round2) {
        this.round1 = round1;
        this.round2 = round2;
    }

    @Override
    public String toString() {
        return "Round 1:\n" + round1 + "\nRound 2:\n" + round2;
    }
}
