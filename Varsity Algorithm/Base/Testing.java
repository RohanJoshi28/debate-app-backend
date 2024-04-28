package Base;

import School.SchoolArg;

public class Testing {
    public static void main(String[] args) {
        SchoolArg[] schoolArgs = new SchoolArg[] {
            new SchoolArg(2, 1),
            new SchoolArg(2, 1),
            new SchoolArg(2, 1),
        };
        
        VarsityMatchmaker matchmaker = new VarsityMatchmaker();
        Tournament morningTournament = matchmaker.CreateTournament(schoolArgs);

        System.out.println(morningTournament);
    }
}