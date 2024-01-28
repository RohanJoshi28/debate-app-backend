import java.util.*;

public class JVMatchmaker {
    public static void main(String[] args) {    
        List<SchoolArg> schoolArgs = new ArrayList<>();
        schoolArgs.add(new SchoolArg(5, 1));
        schoolArgs.add(new SchoolArg(3, 2));

        List<School> schools = createSchools(schoolArgs);
        List<Debater> debaters = createDebaters(schoolArgs, schools);
        sortDebaters(debaters);

        System.out.println("Debaters:");
        for (Debater debater : debaters) {
            System.out.println(debater.name);
        }

        List<Debater> unmatchedDebaters = new ArrayList<>();
        List<Match> round1Matches = matchDebaters(debaters, unmatchedDebaters);

        System.out.println("Matches:");
        for (Match match : round1Matches) {
            System.out.println(match.toString());
        }

        System.out.println("\nUnmatched:");
        for (Debater debater : unmatchedDebaters) {
            System.out.println(debater.name);
        }
    }

    public static void getMatchmaking(List<SchoolArg> schoolArgs) {
        List<School> schools = createSchools(schoolArgs);
        List<Debater> debaters = createDebaters(schoolArgs, schools);

        List<Debater> unmatchedDebaters = new ArrayList<>();
        List<Match> round1Matches = createRound(debaters, unmatchedDebaters);
    }

    private static List<School> createSchools(List<SchoolArg> schoolArgs) {
        List<School> schools = new ArrayList<>();

        for (int i = 0; i < schoolArgs.size(); i++) {
            schools.add(new School(i));
        }

        return schools;
    }

    private static List<Debater> createDebaters(List<SchoolArg> schoolArgs, List<School> schools) {
        List<Debater> debaters = new ArrayList<>();

        for (int schoolIndex = 0; schoolIndex < schools.size(); schoolIndex++) {
            int debaterCount = schoolArgs.get(schoolIndex).debaterCount;

            for (int counter = 0; counter < debaterCount; counter++) {
                debaters.add(new Debater(schools.get(schoolIndex), counter));
            }
        }

        return debaters;
    }

    private static List<Match> createRound(List<Debater> debaters, List<Debater> unmatchedDebaters) {
        List<Match> matches = new ArrayList<>();
        sortDebaters(debaters);
        int index = 0;

        while (index < debaters.size() - 1) {
            Debater debater1 = debaters.get(index);
            Debater debater2 = debaters.get(index + 1);
            
            if (debater1.school != debater2.school) {
                matches.add(new Match(debater1, debater2));
            } else {
                break;
            }

            index += 2;
        }
        
        for (int i = index; i < debaters.size(); i++) {
            unmatchedDebaters.add(debaters.get(i));
        }

        return matches;
    }

    private static void sortDebaters(List<Debater> debaters) {
        debaters.sort((debater1, debater2) -> {
            if (debater1.index != debater2.index) {
                return debater1.index - debater2.index;
            }
            
            return debater1.school.index - debater2.school.index;
        });
    }

    private static class Match {
        public Debater debater1;
        public Debater debater2;

        public Match(Debater debater1, Debater debater2) {
            this.debater1 = debater1;
            this.debater2 = debater2;
        }

        @Override
        public String toString() {
            return "(" + debater1.name + ", " + debater2.name + ")";
        }
    }

    private static class Debater {
        public School school;
        public int index;
        public String name;

        public Debater(School school, int index) {
            this.school = school;
            this.index = index;
            this.name = "" + school.name + index;
        }
    }

    private static class School {
        public int index;
        public char name;
        

        public School(int index) {
            this.index = index;
            name = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(index);
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
}