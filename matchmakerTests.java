import java.io.FileNotFoundException;

public class matchmakerTests {
    public static void main(String[] args) throws FileNotFoundException {
        algorithm2 A = new algorithm2();
        JVMatchmaker JV = new JVMatchmaker();
        int[] test0Players = {2, 2, 3};
        int[] test0Judges = {2, 2, 1};
        String[][] matches0 = A.JVMatches(test0Players, test0Judges);
        System.out.println("test 0");
        test(matches0);
        int[] test1Players = {10, 10, 0};
        int[] test1Judges = {0, 0, 5};
        String[][] matches1 = A.JVMatches(test1Players, test1Judges);
        System.out.println("test 1");
        test(matches1);
        int[] test2Players = {5, 5, 0};
        int[] test2Judges = {0, 0, 5};
        String[][] matches2 = A.JVMatches(test2Players, test2Judges);
        System.out.println("test 2");
        test(matches2);
        int[] test3Players = {12, 1, 4, 4, 13, 4, 1};
        int[] test3Judges = {6, 3, 3, 2, 6, 8, 0};
        String[][] matches3 = A.JVMatches(test3Players, test3Judges);
        System.out.println("test 3");
        test(matches3);
        int[] test4Players = {14, 5, 3, 5, 1, 2, 15};
        int[] test4Judges = {6, 7, 4, 5, 0, 3, 5};
        String[][] matches4 = A.JVMatches(test4Players, test4Judges);
        System.out.println("test 4");
        test(matches4);
        int[] test5Players = {11, 6, 13, 3};
        int[] test5Judges = {6, 1, 5, 1};
        String[][] matches5 = A.JVMatches(test5Players, test5Judges);
        System.out.println("test 5");
        test(matches5);
        int[] test6Players = {5, 6, 15};
        int[] test6Judges = {5, 3, 12};
        String[][] matches6 = A.JVMatches(test6Players, test6Judges);
        System.out.println("test 6");
        test(matches6);
        int[] test7Players = {170, 101, 155, 123, 121, 120, 98, 81, 56, 98, 81, 56};
        int[] test7Judges = {80, 60, 75, 70, 60, 60, 46, 40, 28, 98, 81, 56};
        String[][] matches7 = A.JVMatches(test7Players, test7Judges);
        System.out.println("test 7");
        test(matches7);
    }

    public static void test(String[][] matches){
        for(int i = 0; i < matches.length; i++){
            System.out.print("[");
            for(int j = 0; j<matches[i].length; j++){
                System.out.print(matches[i][j]);
                if(j!=matches[i].length-1){
                    System.out.print(" ");
                }
            }
            System.out.println("]");
        }
        for(int i = 0; i<matches.length; i++){
            String match = "";
            for(int j = 0; j<matches[i].length; j++){
                match += matches[i][j];
            }
            for(int j = 0; j<matches[i].length; j++){
                String[] matchMembers = matches[i][j].split("\\|");
                for(int k = 0; k<matchMembers.length; k++){
                    int first = match.indexOf(matchMembers[k]);
                    int last = match.lastIndexOf(matchMembers[k]);
                    if(first!=last){
                        System.out.println("test failed, participant used multiple times");
                        System.out.println(matchMembers[k]);
                        return;
                    }
                }
                if(matchMembers[0].equals(matchMembers[1])||
                matchMembers[0].equals(matchMembers[2])||
                matchMembers[1].equals(matchMembers[2])){
                    System.out.println("Test Failed, player/judge is used multiple times in 1 match");
                    return;    
                }
                if(matchMembers[0].split("~")[0].equals(matchMembers[1].split("~")[0])||
                matchMembers[0].split("~")[0].equals(matchMembers[2].split("~")[0])||
                matchMembers[1].split("~")[0].equals(matchMembers[2].split("~")[0])){
                    System.out.println("Test Failed, members of the same team");
                    return;    
                }
                if(i==1){
                    for(int l = 0; l<matches[0].length; l++){
                        String[] prev = matches[0][l].split("\\|");
                        String[] cur = matches[i][j].split("\\|");
                        for(int k = 0; k<cur.length; k++){
                            if(cur[k].equals(prev[0])&&cur[0].equals(prev[1])||
                            cur[k].equals(prev[0])&&cur[1].equals(prev[2])||
                            cur[k].equals(prev[1])&&cur[2].equals(prev[2])){
                                System.out.println("Test Failed, same match in two different rounds");
                                return;  
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Test Passed");
    }
}
