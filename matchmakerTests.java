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
        int[] test3Players = {5, 5, 0};
        int[] test3Judges = {0, 0, 5};
        String[][] matches3 = A.JVMatches(test3Players, test3Judges);
        System.out.println("test 3");
        test(matches3);
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
