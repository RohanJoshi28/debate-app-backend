package Base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import School.SchoolArg;

public class Testing {
    public static void main(String[] args) throws Exception{
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Reading data from stdin
            String playersLine = reader.readLine();
            String judgesLine = reader.readLine();
            String winsLine = reader.readLine();


            // Parsing the comma-separated strings into arrays
            int[] test1Players = Arrays.stream(playersLine.split(",")).mapToInt(Integer::parseInt).toArray();
            int[] test1Judges = Arrays.stream(judgesLine.split(",")).mapToInt(Integer::parseInt).toArray();
            int[] morningWins = Arrays.stream(winsLine.split(",")).mapToInt(Integer::parseInt).toArray();

            // Call the JVMatches method
            // String[][] matches = JVMatches(test1Players, test1Judges);

            

            SchoolArg[] schoolArgs = new SchoolArg[test1Players.length];
            for(int i = 0; i < schoolArgs.length; i++){
                schoolArgs[i] = new SchoolArg(test1Players[i], test1Judges[i]);
            }

            VarsityMatchmaker matchmaker = new VarsityMatchmaker(schoolArgs);
            RoundSet morningTournament = matchmaker.CreateRoundSet();
            // System.out.println("Morning:");
            // System.out.println(morningTournament);

            matchmaker.SetTeamWins(morningWins);
            RoundSet afternoonTournament = matchmaker.CreateRoundSet();
            // System.out.println("Afternoon");
            // System.out.println(afternoonTournament);

            String[][] matches = new String[4][];
            matches[0] = morningTournament.matchFormat1();
            matches[1] = morningTournament.matchFormat2();
            matches[2] = afternoonTournament.matchFormat1();
            matches[3] = afternoonTournament.matchFormat2();

            // Output the matches as a simple format (e.g., line-separated values)
            for (String[] match : matches) {
                System.out.println(String.join(",", match));
            }
        }catch(Exception e){
            System.out.println("error");
        }

        // SchoolArg[] schoolArgs = new SchoolArg[] {
        //     new SchoolArg(5, 2),
        //     new SchoolArg(1, 3),
        //     new SchoolArg(2, 1)
        // };

        // int[] morningWins = new int[] {
        //     1, 1, 2, 1, 1, 1, 2, 1, 0, 0
        // };
        
        // VarsityMatchmaker matchmaker = new VarsityMatchmaker(schoolArgs);
        // RoundSet morningTournament = matchmaker.CreateRoundSet();
        // System.out.println("Morning:");
        // System.out.println(morningTournament);

        // matchmaker.SetTeamWins(morningWins);
        // RoundSet afternoonTournament = matchmaker.CreateRoundSet();
        // System.out.println("Afternoon");
        // System.out.println(afternoonTournament);
    }
}