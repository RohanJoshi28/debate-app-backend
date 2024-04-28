package Base;

import School.SchoolArg;

public class Testing {
    public static void main(String[] args) {
        SchoolArg[] schoolArgs = new SchoolArg[] {
            new SchoolArg(4, 2),
            new SchoolArg(4, 2),
            new SchoolArg(4, 2),
        };

        int[] morningWins = new int[] {
            1, 2, 1, 0, 1, 2, 2, 1, 2, 0, 0, 2
        };
        
        VarsityMatchmaker matchmaker = new VarsityMatchmaker(schoolArgs);
        Tournament morningTournament = matchmaker.CreateTournament();
        System.err.println("Morning:");
        System.out.println(morningTournament);

        matchmaker.SetTeamWins(morningWins);
        Tournament afternoonTournament = matchmaker.CreateTournament();
        System.out.println("Afternoon");
        System.out.println(afternoonTournament);
    }
}