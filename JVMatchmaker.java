import java.util.*;

public class JVMatchmaker {
    public static void main(String[] args) {
        School[] schools = new School[] {
            new School(2, 3, "R")
        };

        Debater[] debaters = generateDebaters(schools);

        for (Debater debater : debaters) {
            System.out.println(debater.school.name);
        }
    }

    public static Match[] generateTournament(School[] schools, int[] roomNumbers) {
        
        //generate all debaters and judges
        Debater[] debaters = generateDebaters(schools);
        Judge[] judges = generateJudges(schools);
        
        //generate all possible matches
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

    private static Debater[] generateDebaters(School[] schools) {
        List<Debater> debaters = new ArrayList<>();

        for (School school : schools) {
            for (int i = 0; i < school.debaterCount; i++) {
                debaters.add(new Debater(school));
            }
        }

        return debaters.toArray(new Debater[0]);
    }

    private static Judge[] generateJudges(School[] schools) {
        List<Judge> judges = new ArrayList<>();

        for (School school : schools) {
            for (int i = 0; i < school.judgeCount; i++) {
                judges.add(new Judge(school));
            }
        }

        return judges.toArray(new Judge[0]);
    }
    /*
    private static Match[] generateMatches(Debater[] debaters, Judge[] judges) {
        List<Match> matches = new ArrayList<>();

        for (Judge judge : judges) {
            for (Debater debater1 : debaters) {
                for (Debater debater1 : debaters) {
                }
            }
        }
    }
    */

    //Class definitions
    private static class Debater {
        public School school;

        public Debater(School school) {
            this.school = school;
        }
    }

    private static class Judge {
        public School school;

        public Judge(School school) {
            this.school = school;
        }    
    }

    private static class School {
        public int debaterCount;
        public int judgeCount;
        public String name;

        public School(int debaterCount, int judgeCount, String name) {
            this.debaterCount = debaterCount;
            this.judgeCount = judgeCount;
            this.name = name;
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
    }
}