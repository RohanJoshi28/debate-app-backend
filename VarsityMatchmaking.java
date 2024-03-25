import java.util.*;

public class VarsityMatchmaking {
    public static void main(String[] args) {
        int[] d = new int[] { 3, 2, 1 };
        int[] j = new int[] { 1, 1, 1 };
        matchmake(d, j);
    }

    private static void matchmake(int[] debaterCounts, int[] judgeCounts) {
        List<School> schools = createSchools(debaterCounts.length);
        List<Debater> debaters = createDebaters(debaterCounts, schools);
        List<Judge> judges = createJudges(judgeCounts, schools);

        // Morning Rounds
        Round round1 = createRound(debaters, judges, null);
        Round round2 = createRound(debaters, judges, round1);

        // Afternoon Rounds
        List<Debater> winningDebaters = getWinningDebaters(round1, round2);
        List<Debater> afternoonDebaters = new ArrayList<>(winningDebaters);

        Round round3 = createRound(afternoonDebaters, afternoonJudges, null);
        Round round4 = createRound(afternoonDebaters, afternoonJudges, round3);

        System.out.println("Morning Rounds:");
        System.out.println(round1);
        System.out.println(round2);
        System.out.println("Afternoon Rounds:");
        System.out.println(round3);
        System.out.println(round4);
    }

    private static List<Debater> getWinningDebaters(Round round1, Round round2) {
        List<Debater> winningDebaters = new ArrayList<>();
        for (Match match : round1.matches) {
            if (match.winner()) {
                winningDebaters.add(match.debater1);
            }
        }
        for (Match match : round2.matches) {
            if (match.winner()) {
                winningDebaters.add(match.debater1);
            }
        }
        return winningDebaters;
    }

    private static List<School> createSchools(int count) {
        List<School> schools = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            schools.add(new School(i));
        }

        return schools;
    }

    private static List<Debater> createDebaters(int[] debaterCounts, List<School> schools) {
        List<Debater> debaters = new ArrayList<>();

        for (int schoolIndex = 0; schoolIndex < debaterCounts.length; schoolIndex++) {
            for (int index = 0; index < debaterCounts[schoolIndex]; index++) {
                debaters.add(new Debater(schools.get(schoolIndex), index));
            }
        }

        debaters.sort(Comparable::compareTo);
        return debaters;
    }

    private static List<Judge> createJudges(int[] judgeCounts, List<School> schools) {
        List<Judge> judges = new ArrayList<>();

        for (int schoolIndex = 0; schoolIndex < judgeCounts.length; schoolIndex++) {
            for (int index = 0; index < judgeCounts[schoolIndex]; index++) {
                judges.add(new Judge(schools.get(schoolIndex), index));
            }
        }

        judges.sort(Comparable::compareTo);
        return judges;
    }

    private static Round createRound(List<Debater> debaters, List<Judge> judges, Round lastRound) {
        List<Match> matches = new ArrayList<>();
        List<Debater> matchedDebaters = new ArrayList<>();
        List<Judge> matchedJudges = new ArrayList<>();

        for (int debater1Index = 0; debater1Index < debaters.size(); debater1Index++) {
            Debater debater1 = debaters.get(debater1Index);

            if (matchedDebaters.contains(debater1)) continue;

            for (int debater2Index = 0; debater2Index < debaters.size(); debater2Index++) {
                Debater debater2 = debaters.get(debater2Index);

                if (matchedDebaters.contains(debater2)) continue;
                if (!canDebatersMatch(debater1, debater2, lastRound)) continue;

                Judge judge = getJudge(judges, matchedJudges, debater1, debater2);

                if (judge == null) break;

                matches.add(new Match(debater1, debater2, judge));
                matchedDebaters.add(debater1);
                matchedDebaters.add(debater2);
                matchedJudges.add(judge);
                break;
            }
        }

        return new Round(matches);
    }

    private static boolean canDebatersMatch(Debater debater1, Debater debater2, Round lastRound) {
        if (debater1.school == debater2.school) return false;
        if (lastRound == null) return true;

        return !lastRound.hasMatchup(debater1, debater2);
    }

    private static Judge getJudge(List<Judge> judges, List<Judge> matchedJudges, Debater debater1, Debater debater2) {
        for (Judge judge : judges) {
            if (matchedJudges.contains(judge)) continue;
            
            if (judge.school != debater1.school && judge.school != debater2.school) {
                return judge;
            }
        }
        
        for (Judge judge : judges) {
            if (matchedJudges.contains(judge)) {
                continue;
            }

            return judge;
        }

        return null;
    }

    private static class Round {
        private List<Match> matches;

        public Round(List<Match> matches) {
            this.matches = matches;
        }

        public boolean hasMatchup(Debater debater1, Debater debater2) {
            for (Match match : matches) {
                if (match.debater1 == debater1 && match.debater2 == debater2 ||
                match.debater1 == debater2 && match.debater2 == debater1) return true;
            }

            return false;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Round:\n");

            for (Match match : matches) {
                builder.append(match).append("\n");
            }

            return builder.toString();
        }
    }

    private static class Match {
        private Debater debater1;
        private Debater debater2;
        private Judge judge;

        public Match(Debater debater1, Debater debater2, Judge judge) {
            this.debater1 = debater1;
            this.debater2 = debater2;
            this.judge = judge;
        }

        public boolean winner() {
            // Placeholder logic for determining the winner (which will need to be passed in)
            // For simplicity right now, a random winner is chosen
            Random random = new Random();
            return random.nextBoolean();
        }

        @Override
        public String toString() {
            return "(" + debater1 + ", " + debater2 + ", " + judge + ")";
        }
    }

    private abstract static class Person implements Comparable<Person> {
        public School school;
        private int index;

        public Person(School school, int index) {
            this.school = school;
            this.index = index;
        }

        @Override
        public int compareTo(Person otherPerson) {
            if (index != otherPerson.index) {
                return index - otherPerson.index;
            }

            return school.compareTo(otherPerson.school);
        }

        @Override
        public String toString() {
            return school.toString() + index;
        }
    }

    private static class Judge extends Person {
        public Judge(School school, int index) {
            super(school, index);
        }

        @Override
        public String toString() {
            return "J" + super.toString();
        }
    }

    private static class Debater extends Person {
        public Debater(School school, int index) {
            super(school, index);
        }
    }

    private static class School implements Comparable<School> {
        private int index;

        public School(int index) {
            this.index = index;
        }

        @Override
        public int compareTo(School otherSchool) {
            return index - otherSchool.index;
        }

        @Override
        public String toString() {
            return "" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(index);
        }
    }
}
