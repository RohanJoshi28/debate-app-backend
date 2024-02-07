public class matchmakerTests {
    public static void main(String[] args) {
        algorithm2 A = new algorithm2();
        JVMatchmaker JV = new JVMatchmaker();
        test1(A);
        test2(A);
    }

    public static void test1(algorithm2 A){
        boolean APassed = false;
        int[] players = {10, 10, 0};
        int[] judges = {0, 0, 5};
        String[][] matches = A.JVMatches(players, judges);
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
            for(int j = 0; j<matches[i].length; j++){
                String[] matchMembers = matches[i][j].split("\\|");
                if(matchMembers[0].equals(matchMembers[1])||
                matchMembers[0].equals(matchMembers[2])||
                matchMembers[1].equals(matchMembers[2])){
                    System.out.println("Test 1 Failedj");
                    return;    
                }
                if(matchMembers[0].split("~")[0].equals(matchMembers[1].split("~")[0])||
                matchMembers[0].split("~")[0].equals(matchMembers[2].split("~")[0])||
                matchMembers[1].split("~")[0].equals(matchMembers[2].split("~")[0])){
                    System.out.println("Test 1 Failedk");
                    return;    
                }
                if(i==1){
                    for(int l = 0; l<matches[0].length; l++){
                        String[] prev = matches[0][l].split("\\|");
                        String[] cur = matches[i][j].split("\\|");
                        for(int k = 0; k<cur.length; k++){
                            if(cur[k].equals(prev[0])&&cur[0].equals(prev[1])||
                            cur[k].equals(prev[0])&&cur[0].equals(prev[2])||
                            cur[k].equals(prev[1])&&cur[0].equals(prev[2])){
                                System.out.println("Test 1 Failedl");
                                return;  
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Test 1 Passed");
    }

    public static void test2(algorithm2 A){
        boolean APassed = false;
        int[] players = {5, 5, 0};
        int[] judges = {0, 0, 5};
        String[][] matches = A.JVMatches(players, judges);
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
            for(int j = 0; j<matches[i].length; j++){
                String[] matchMembers = matches[i][j].split("\\|");
                if(matchMembers[0].equals(matchMembers[1])||
                matchMembers[0].equals(matchMembers[2])||
                matchMembers[1].equals(matchMembers[2])){
                    System.out.println("Test 2 Failedj");
                    return;    
                }
                if(matchMembers[0].split("~")[0].equals(matchMembers[1].split("~")[0])||
                matchMembers[0].split("~")[0].equals(matchMembers[2].split("~")[0])||
                matchMembers[1].split("~")[0].equals(matchMembers[2].split("~")[0])){
                    System.out.println("Test 2 Failedk");
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
                                System.out.println("Test 2 Failedl");
                                return;  
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Test 2 Passed");
    }
}
