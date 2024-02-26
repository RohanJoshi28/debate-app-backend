import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class algorithm2 {

    public static int playersToUse;
    public static int judgesToUse;

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Reading data from stdin
        String playersLine = reader.readLine();
        String judgesLine = reader.readLine();

        // Parsing the comma-separated strings into arrays
        int[] test1Players = Arrays.stream(playersLine.split(",")).mapToInt(Integer::parseInt).toArray();
        int[] test1Judges = Arrays.stream(judgesLine.split(",")).mapToInt(Integer::parseInt).toArray();

        // Call the JVMatches method
        String[][] matches = JVMatches(test1Players, test1Judges);

        // Output the matches as a simple format (e.g., line-separated values)
        for (String[] match : matches) {
            System.out.println(String.join(",", match));
        }
    }
    public static String[][] JVMatches (int[] players, int[] judges) throws FileNotFoundException{
        int numMatches = 0;
        int playNum = sum(players);
        int judgeNum = sum(judges);
        if(playNum/2>judgeNum){//should be players/2
            numMatches = judgeNum;
            // add disclaimer saying that for lower ranked teams to play, they may need to be switched in(only top x(according to judge) teams are looked at)
            // for(int i = 0; i <players.length; i++){
            //     if(players[i]>judgeNum){
            //         players[i] = judgeNum;
            //     }
            // }
        }else{
            if(playNum%2==1){
                if(players[0]>0){
                    players[0]--;
                    playNum--;
                }
            }
            numMatches = playNum/2;
        }
        playersToUse = playNum;
        judgesToUse = judgeNum;
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
        PrintWriter writer = new PrintWriter("matchResults.txt");
        for(int i = 0; i<matches.length; i++){
            writer.print("[");
            for(int j = 0; j<matches[i].length; j++){
                writer.print(matches[i][j]);
                if(j!=matches[i].length-1){
                    writer.print(" ");
                }
            }
            writer.println("]");
        }
        writer.close();
        return matches;
    }
    
    public static String[] roundjv(int numMatches, debater[][] players, debater[][] judges, String[] prev, debater[][] playerCopy, debater[][] judgesCopy){
        String[] match = new String[numMatches];
        int pos = players.length-1;
        
        for(int i = 0; i<numMatches; i++){
            if(!allUsed(players[pos])){
                boolean notUsed = false;
                for(int j = 0; j<players[pos].length; j++){
                    if(!players[pos][j].used){
                        if(prev.length>0){
                            notUsed = false;
                            String prospective = players[pos][j].team+"~"+players[pos][j].number;
                            for(int k = 0; k < prev.length; k++){
                                if(prev[k].split("\\|")[0].equals(prospective)){
                                    notUsed = true;
                                }
                            }
                            if(!notUsed){
                                match[i] = players[pos][j].team+"~"+players[pos][j].number+"|";
                                players[pos][j].used = true;
                                playersToUse--;
                                notUsed = false;
                                break;
                            }
                        }else{
                            match[i] = players[pos][j].team+"~"+players[pos][j].number+"|";
                            players[pos][j].used = true;
                            playersToUse--;
                            notUsed = false;
                            break;
                        }
                    }
                }
                if(notUsed){
                    i--;
                }
            }else{
                i--;
            }
            pos--;
            
            if(pos < 0){
                pos = players.length-1;
                if(playersToUse==0){
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
                                playersToUse++;
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
                            String prospective = match[i] + players[pos][j].team+"~"+players[pos][j].number;
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
                            for(int k = 0; k < prev.length; k++){
                                if(prev[k].split("\\|")[1].equals(players[pos][j].team+"~"+players[pos][j].number)){
                                    fails = true;
                                    allFail = true;
                                }
                            }
                            if(!fails){
                                if(!players[pos][j].used){
                                    match[i] = prospective+"|";
                                    if((players[pos][j].team+"~"+players[pos][j].number).equals("0~1")){
                                        System.out.println("here");
                                    }
                                    players[pos][j].used = true;
                                    playersToUse--;
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
                                playersToUse--;
                                allFail = false;
                                break;
                            }
                        }
                    }
                    if(allFail){
                        if(i>0){
                            boolean done = false;
                            for(int k = 0; k<i; k++){
                                String swap = match[k].split("\\|")[1];
                                if(swap.split("~")[0].equals(match[i].split("~")[0])){
                                    continue;
                                }
                                for(int j = 0; j<players[pos].length;j++){
                                    for(int l = 0; l < prev.length; l++){
                                        if(prev[l].split("\\|")[1].equals(players[pos][j].team+"~"+players[pos][j].number)){
                                            done = true;
                                        }
                                    }
                                    if(done){
                                        done = false;
                                        continue;
                                    }
                                    if(!players[pos][j].used){
                                        match[k] = match[k].split("\\|")[0]+"|"+players[pos][j].team+"~"+players[pos][j].number+"|";
                                        players[pos][j].used = true;
                                        playersToUse--;
                                        match[i] += swap+"|";
                                        done = true;
                                        break;
                                    }
                                }
                                if(done){
                                    break;
                                }
                            }
                            if(!done){
                                i--;
                            }
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
                if(playersToUse==0){
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
                                playersToUse++;
                            }
                        }
                    }
                }
            }
        }
        
        for(int i = 0; i<numMatches; i++){
            if(!allUsed(judges[pos])){
                String[] prevMatch = match[i].split("~");
                if(prevMatch[0].equals(Integer.toString(pos))||prevMatch[1].split("\\|")[1].equals(Integer.toString(pos))){
                    i--;
                }else{
                    for(int j = 0; j<judges[pos].length; j++){
                        if(!judges[pos][j].used){
                            match[i] += "J"+ judges[pos][j].team+"~"+judges[pos][j].number;
                            judges[pos][j].used = true;
                            judgesToUse--;
                            break;
                        }
                    }
                    // boolean allFail = false;
                    // for(int j = 0; j<judges[pos].length; j++){
                    //     if(prev.length>0){
                    //         boolean fails = false;
                    //         for(int k = 0; k<prev.length; k++){
                    //             if(prev[k].split("\\|")[2].equals("J"+ judges[pos][j].team+"~"+judges[pos][j].number)&&
                    //             (prev[k].split("\\|")[0].equals(match[i].split("\\|")[0])||
                    //             prev[k].split("\\|")[0].equals(match[i].split("\\|")[1])||
                    //             prev[k].split("\\|")[1].equals(match[i].split("\\|")[0])||
                    //             prev[k].split("\\|")[1].equals(match[i].split("\\|")[1]))){
                    //                 fails = true;
                    //                 allFail = true;
                    //             }
                    //         }
                    //         if(!fails){
                    //             if(!judges[pos][j].used){
                    //                 match[i]+="J"+ judges[pos][j].team+"~"+judges[pos][j].number;
                    //                 judges[pos][j].used = true;
                    //                 allFail = false;
                    //                 break;
                    //             }else{
                    //                 allFail = true;
                    //             }
                    //         }
                    //     }else{
                    //         if(!judges[pos][j].used){
                    //             match[i] += "J"+ judges[pos][j].team+"~"+judges[pos][j].number;
                    //             judges[pos][j].used = true;
                    //             break;
                    //         }
                    //     }
                    // }
                    // if(allFail){
                    //     if(i>0){
                    //         boolean done = false;
                    //         for(int k =0; k<i; k++){
                    //             String swap = match[k].split("\\|")[2];
                    //             if(swap.split("~").equals(match[i].split("~")[0])){
                    //                 continue;
                    //             }
                    //             for(int j = 0; j<players[pos].length; j++){
                    //                 for(int l = 0; l<prev.length; l++){
                    //                     if(prev[l].split("\\|")[2].equals("J"+ judges[pos][j].team+"~"+judges[pos][j].number)&&
                    //                     (prev[l].split("\\|")[0].equals(match[i].split("\\|")[0])||
                    //                     prev[l].split("\\|")[0].equals(match[i].split("\\|")[1])||
                    //                     prev[l].split("\\|")[1].equals(match[i].split("\\|")[0])||
                    //                     prev[l].split("\\|")[1].equals(match[i].split("\\|")[1]))){
                    //                         done = true;
                    //                     }
                    //                 }
                    //                 if(done){
                    //                     done = false;
                    //                     continue;
                    //                 }
                    //                 if(!players[pos][j].used){
                    //                     match[k] = match[k].split("\\|")[0] +match[k].split("\\|")[1]+"J"+ judges[pos][j].team+"~"+judges[pos][j].number;
                    //                     judges[pos][j].used = true;
                    //                     match[i]+=swap;
                    //                     done = true;
                    //                     break;
                    //                 }
                    //             }                                
                    //             if(done){
                    //                 break;
                    //             }
                    //         }
                    //         if(!done){
                    //             i--;
                    //         }
                    //     }else{
                    //         i--;
                    //     }
                    // }
                }
            }else{
                i--;
            }
            pos--;
            
            if(pos < 0){
                pos = judges.length-1;
                if(judgesToUse==0){
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
                                judgesToUse++;
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
    public boolean judgeSupport;

    public debater(String team, String num){
        this.team = team;
        this.number = num;
        this.used = false;
        this.judgeSupport = false;
    }
}