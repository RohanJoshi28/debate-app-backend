import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VarsityMatchmaking {
    public static void main(String[] args) {
    }

    private static void matchmake(int[] debaterCounts, int[] judgeCounts) {
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
