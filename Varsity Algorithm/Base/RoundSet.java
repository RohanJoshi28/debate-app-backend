package Base;

import Round.Round;

public class RoundSet {
    public Round round1;
    public Round round2;

    public RoundSet(Round round1, Round round2) {
        this.round1 = round1;
        this.round2 = round2;
    }

    @Override
    public String toString() {
        return "Round 1:\n" + round1 + "\nRound 2:\n" + round2;
    }

    public String[] matchFormat1(){
        String[] rnd = new String[round1.matches.size()];
        for(int i = 0; i < round1.matches.size(); i++){
            rnd[i] = round1.matches.get(i).affirmativeTeam.school.toString()+"~"+round1.matches.get(i).affirmativeTeam.rank+"|"+round1.matches.get(i).negativeTeam.school.toString()+"~"+round1.matches.get(i).negativeTeam.rank+"|"+round1.matches.get(i).judge.toString();
        }
        return rnd;
    }

    public String[] matchFormat2(){
        String[] rnd = new String[round2.matches.size()];
        for(int i = 0; i < round2.matches.size(); i++){
            rnd[i] = round2.matches.get(i).affirmativeTeam.school.toString()+"~"+round2.matches.get(i).affirmativeTeam.rank+"|"+round2.matches.get(i).negativeTeam.school.toString()+"~"+round2.matches.get(i).negativeTeam.rank+"|"+round2.matches.get(i).judge.toString();
        }
        return rnd;
    }
}
