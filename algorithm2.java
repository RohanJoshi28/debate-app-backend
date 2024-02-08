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
        debater[][] debaters = new debater[players.length][];
        for(int i = 0; i < debaters.length; i++){
            debaters[i] = new debater[players[i]];
            for(int j = 0; j<players[i]; j++){
                debaters[i][j] = new debater(Integer.toString(i), Integer.toString(j));
            }
        }
        
        debater[][] playCopy = debaters.clone();
        debater[][] judgers = new debater[judges.length][];
        for(int i = 0; i < debaters.length; i++){
            judgers[i] = new debater[judges[i]];
            for(int j = 0; j<judges[i]; j++){
                judgers[i][j] = new debater(Integer.toString(i), Integer.toString(j));
            }
        }
        debater[][] judgeCopy = judgers.clone();
        String[] round1 =roundjv(numMatches, debaters, judgers, new String[0], playCopy, judgeCopy);
        String[] round2 = roundjv(numMatches, debaters, judgers, round1, playCopy, judgeCopy);
        String[][] matches = new String[2][];
        matches[0] = round1;
        matches[1] = round2;
        return matches;
    }
    
    public static String[] roundjv(int numMatches, debater[][] players, debater[][] judges, String[] prev, debater[][] playerCopy, debater[][] judgesCopy){
        String[] match = new String[numMatches];
        int pos = players.length-1;
        
        for(int i = 0; i<numMatches; i++){
            if(!allUsed(players[pos])){
                for(int j = 0; j<players[pos].length; j++){
                    if(!players[pos][j].used){
                        match[i] = players[pos][j].team+"~"+players[pos][j].number+"|";
                        players[pos][j].used = true;
                        break;
                    }
                }
            }else{
                i--;
            }
            pos--;
            if(pos < 0){
                pos = players.length-1;
                if(allUsed(players)){
                    String matched = "";
                    for(int l = 0; l <= i; l++){
                        matched+=match[l];
                    }
                    for(int a = 0; a<players.length; a++){
                        for(int b = 0; b<players[a].length; b++){
                            String check = players[a][b].team+"~"+players[a][b].number+"|";
                            if(matched.contains(check)){
                                players[a][b].used = true;
                            }else{
                                players[a][b].used = false;
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i<numMatches; i++){
            if(!allUsed(players[pos])){
                String[] prevMatch = match[i].split("~");
                if(prevMatch[0].equals(Integer.toString(pos))){
                    i--;
                }else{
                    boolean allFail = false;
                    for(int j = 0; j < players[pos].length; j++){
                        if(prev.length>0){
                            String prospective = match[i] + players[pos][j].team+"~"+players[pos][j].number+"|";
                            String[] pros = prospective.split("\\|");
                            boolean fails = false;
                            for(int k = 0; k < prev.length; k++){
                                String[] pre = prev[k].split("\\|");
                                if((pre[0].equals(pros[0])||pre[0].equals(pros[1]))&&
                                (pre[1].equals(pros[0])||pre[1].equals(pros[1]))){
                                    fails = true;
                                    allFail = true;
                                }
                            }
                            if(!fails){
                                if(!players[pos][j].used){
                                    match[i] = prospective;
                                    players[pos][j].used = true;
                                    allFail = false;
                                    break;
                                }else{
                                    allFail = true;
                                }
                            }
                        }else{
                            if(!players[pos][j].used){
                                match[i] += players[pos][j].team+"~"+players[pos][j].number+"|";
                                players[pos][j].used = true;
                                allFail = false;
                                break;
                            }
                        }
                    }
                    if(allFail){
                        if(i>0){
                            String swap = match[0].split("\\|")[1];
                            for(int j = 0; j<players[pos].length;j++){
                                if(!players[pos][j].used){
                                    match[0] = match[0].split("\\|")[0]+"|"+players[pos][j].team+"~"+players[pos][j].number+"|";
                                    players[pos][j].used = true;
                                    break;
                                }
                            }
                            match[i] += swap+"|";
                        }else{
                            i--;
                        }
                    }
                }
            }else{
                i--;
            }
            pos--;
            if(pos < 0){
                pos = players.length-1;
                if(allUsed(players)){
                    String matched = "";
                    for(int l = 0; l < match.length; l++){
                        matched+=match[l];
                    }
                    for(int a = 0; a<players.length; a++){
                        for(int b = 0; b<players[a].length; b++){
                            String check = players[a][b].team+"~"+players[a][b].number+"|";
                            if(matched.contains(check)){
                                players[a][b].used = true;
                            }else{
                                players[a][b].used = false;
                            }
                        }
                    }
                }
            }
        }
        
        for(int i = 0; i<numMatches; i++){
            if(!allUsed(judges[pos])){
                String[] prevMatch = match[i].split("~");
                if(prevMatch[0].equals(Integer.toString(pos))||prevMatch[2].equals(Integer.toString(pos))){
                    i--;
                }else{
                    for(int j = 0; j<judges[pos].length; j++){
                        if(!judges[pos][j].used){
                            match[i] += "J"+ judges[pos][j].team+"~"+judges[pos][j].number;
                            judges[pos][j].used = true;
                            break;
                        }
                    }
                }
            }else{
                i--;
            }
            pos--;
            if(pos < 0){
                pos = judges.length-1;
                if(allUsed(judges)){
                    String matched = "";
                    for(int l = 0; l < match.length; l++){
                        matched+=match[l];
                    }
                    for(int a = 0; a<judges.length; a++){
                        for(int b = 0; b<judges[a].length; b++){
                            String check = "J"+ judges[a][b].team+"~"+judges[a][b].number;
                            if(matched.contains(check)){
                                judges[a][b].used = true;
                            }else{
                                judges[a][b].used = false;
                            }
                        }
                    }
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

    public static boolean allUsed(debater[] array){
        for(int i = 0; i < array.length; i++){
            if(!array[i].used){
                return false;
            }
        }
        return true;
    }

    public static boolean allUsed(debater[][] array){
        for(int i = 0; i < array.length; i++){
            for(int j = 0; j<array[i].length; j++){
                if(!array[i][j].used){
                    return false;
                }
            }
        }
        return true;
    }
}

class debater{
    public String team;
    public String number;
    public boolean used;

    public debater(String team, String num){
        this.team = team;
        this.number = num;
        this.used = false;
    }
}