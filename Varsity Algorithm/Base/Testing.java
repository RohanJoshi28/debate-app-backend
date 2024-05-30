package Base;

import School.SchoolArg;

public class Testing {
    public static void main(String[] args) {
        SchoolArg[] schoolArgs = new SchoolArg[] {
            new SchoolArg(5, 2),
            new SchoolArg(1, 3),
            new SchoolArg(2, 1)
        };

        int[] morningWins = new int[] {
            1, 1, 2, 1, 1, 1, 2, 1, 0, 0
        };
        
        VarsityMatchmaker matchmaker = new VarsityMatchmaker(schoolArgs);
        RoundSet morningTournament = matchmaker.CreateRoundSet();
        System.out.println("Morning:");
        System.out.println(morningTournament);

        matchmaker.SetTeamWins(morningWins);
        RoundSet afternoonTournament = matchmaker.CreateRoundSet();
        System.out.println("Afternoon");
        System.out.println(afternoonTournament);
    }
}