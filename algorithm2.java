public class algorithm2 {
    public static String[][] JVMatches (int[] players, int[] judges){
        int numMatches = 0;
        if(sum(players)/2>sum(judges)){//should be players/2
            numMatches = sum(judges);
        }else{
            numMatches = sum(players)/2;
        }
        //the following does not perfectly take into account edge cases
        //the main edge case not accounted four is too many teams
        //in the case of too many teams the largest team will be penalized rather than the host
        //number of matches will be based on judges or players and distributed to teams equally
        int[] playCopy = players.clone();
        int[] judgeCopy = judges.clone();
        String[] round1 =roundjv(numMatches, players, judges, new String[0], playCopy, judgeCopy);
        System.out.println("here");
        String[] round2 = roundjv(numMatches, players, judges, round1, playCopy, judgeCopy);
        System.out.println("here2");
        String[][] matches = new String[2][];
        matches[0] = round1;
        matches[1] = round2;
        return matches;
    }
    
    public static String[] roundjv(int numMatches, int[] players, int[] judges, String[] prev, int[] playerCopy, int[] judgesCopy){
        String[] match = new String[numMatches];
        int pos = players.length-1;
        System.out.println("here3");
        for(int i = 0; i<numMatches; i++){
            if(players[pos]!=0){
                match[i] = Integer.toString(pos)+"~"+Integer.toString(players[pos]--)+"|";
            }else{
                i--;
            }
            pos--;
            if(pos < 0){
                pos = players.length-1;
                if(sum(players)==0){
                    players = playerCopy;
                }
            }
        }
        System.out.println("here4");
        for(int i = 0; i<numMatches; i++){
            if(players[pos]!=0){
                String[] prevMatch = match[i].split("~");
                if(prevMatch[0].equals(Integer.toString(pos))){
                    i--;
                }else{
                    match[i] += Integer.toString(pos)+"~"+Integer.toString(players[pos]--)+"|";
                }
            }else{
                i--;
            }
            pos--;
            if(pos < 0){
                pos = players.length-1;
                if(sum(players)==0){
                    players = playerCopy;
                }
            }
        }

        for(int i = 0; i<numMatches; i++){
            if(judges[pos]!=0){
                String[] prevMatch = match[i].split("~");
                if(prevMatch[0].equals(Integer.toString(pos))||prevMatch[2].equals(Integer.toString(pos))){
                    i--;
                }else{
                    match[i] += Integer.toString(pos)+"~"+Integer.toString(judges[pos]--)+"|";
                }
            }else{
                i--;
            }
            pos--;
            if(pos < 0){
                pos = judges.length-1;
                if(sum(judges)==0){
                    judges = judgesCopy;
                }
            }
        }
        return match;
    }

    public static int sum(int[] array){
        int sum = 0;
        for(int i = 0; i<array.length; i++){
            sum+=array[i];
        }
        return sum;
    }
}
