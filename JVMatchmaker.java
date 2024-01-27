import java.util.*;

public class JVMatchmaker {
    public static void main(String[] args) {
        School[] schools = new School[] {
            new School(new SchoolArg(2, 1), 0),
            new School(new SchoolArg(1, 1), 1),
        };

        //generate all debaters and judges
        Debater[] debaters = generateDebaters(schools);
        Judge[] judges = generateJudges(schools);

        for (Debater debater : debaters) {
            System.out.println(debater.name);
        }

        for (Judge judge : judges) {
            System.out.println(judge.name);
        }

        //generate all possible matches
        Match[] matches = generateMatches(debaters, judges);

        for (Match match : matches) {
            System.out.println(match.toString());
        }
    }

    public static Match[] generateTournament(SchoolArg[] schoolArgs, int[] roomNumbers) {
        //generate schools
        School[] schools = generateSchools(schoolArgs);

        //generate all debaters and judges
        Debater[] debaters = generateDebaters(schools);
        Judge[] judges = generateJudges(schools);
        
        //generate all possible matches
        Match[] matches = generateMatches(debaters, judges);

        //generate all possible rounds


        //generate all possible n-round tournaments (2 in this case for JV)
        //score all tournaments:
        //  -debaters should not be against themselves
        //  -debaters should not be against their own school
        //  -judges should not be from a debater's school
        //  -debaters should not go against the same opponent twice
        //assign rooms to each match
        //format the result
        //return
        return null;
    }

    private static School[] generateSchools(SchoolArg[] schoolArgs) {
        School[] schools = new School[schoolArgs.length];

        for (int i = 0; i < schoolArgs.length; i++) {
            schools[i] = new School(schoolArgs[i], i);
        }

        return schools;
    }

    private static Debater[] generateDebaters(School[] schools) {
        List<Debater> debaters = new ArrayList<>();

        for (School school : schools) {
            for (int i = 0; i < school.debaterCount; i++) {
                debaters.add(new Debater(school, i));
            }
        }

        return debaters.toArray(new Debater[0]);
    }

    private static Judge[] generateJudges(School[] schools) {
        List<Judge> judges = new ArrayList<>();

        for (School school : schools) {
            for (int i = 0; i < school.judgeCount; i++) {
                judges.add(new Judge(school, i));
            }
        }

        return judges.toArray(new Judge[0]);
    }

    private static Match[] generateMatches(Debater[] debaters, Judge[] judges) {
        List<Match> matches = new ArrayList<>();

        for (Judge judge : judges) {
            for (Debater debater1 : debaters) {
                for (Debater debater2 : debaters) {
                    matches.add(new Match(debater1, debater2, judge));
                }
            }
        }

        return matches.toArray(new Match[0]);
    }

    /*
    private static Round[] generateRounds(Match[] matches) {
        for (Match match : matches) {
            for (Match match2 : matches) {
                
            }
        }
    }
    */

    //Class definitions
    private static class Debater {
        public String name;

        public Debater(School school, int number) {
            name = school.name + number;
        }
    }

    private static class Judge {
        public String name;

        public Judge(School school, int number) {
            name = "J" + school.name + number;
        }
    } 

    private static class School {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public int debaterCount;
        public int judgeCount;
        public String name;

        public School(SchoolArg schoolArg, int number) {
            this.debaterCount = schoolArg.debaterCount;
            this.judgeCount = schoolArg.judgeCount;
            this.name = "" + ALPHABET.charAt(number);
        }
    }

    private static class SchoolArg {
        public int debaterCount;
        public int judgeCount;

        public SchoolArg(int debaterCount, int judgeCount) {
            this.debaterCount = debaterCount;
            this.judgeCount = judgeCount;
        }        
    }

    private static class Match {
        public Debater debater1;
        public Debater debater2;
        public Judge judge;

        public Match(Debater debater1, Debater debater2, Judge judge) {
            this.debater1 = debater1;
            this.debater2 = debater2;
            this.judge = judge;
        }

        @Override
        public String toString() {
            return "(" + debater1.name + ", " + debater2.name + ", " + judge.name + ")";
        }
    }

    private static class Round {
        public Match[] matches;

        public Round(Match[] matches) {
            this.matches = matches;
        }
    }
}