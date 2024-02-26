import java.util.ArrayList;
import java.util.List;

public class VarsityMatchmaking {
    public static void main(String[] args) {
    }

    private static void matchmake(int[] debaterCounts, int[] judgeCounts) {
        List<School> schools = createSchools(debaterCounts.length);
        List<Debater> debaters = createDebaters(debaterCounts, schools);
        List<Judge> judges = createJudges(judgeCounts, schools);
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
