import School.SchoolArg;
import People.*;

public class Testing {
    public static void main(String[] args) {
        SchoolArg[] schoolArgs = new SchoolArg[] {
            new SchoolArg(2, 1),
            new SchoolArg(2, 1),
            new SchoolArg(2, 1),
        };

        PersonGenerator personGenerator = new PersonGenerator(schoolArgs);

        for (Team team : personGenerator.teams) {
            System.out.println(team);
        }
    }
}