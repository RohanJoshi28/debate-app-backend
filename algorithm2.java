import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class algorithm2 {

    public static int playersToUse;
    public static int judgesToUse;

    public static void main(String[] args) throws Exception {
        try{
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
        }catch(Exception e){
            System.out.println("error");
        }
    } 
    public static String[][] JVMatches (int[] players, int[] judges) throws FileNotFoundException{
        int numMatches = 0;
        int playNum = sum(players);
        int judgeNum = sum(judges);
        if(playNum/2>judgeNum){//should be players/2
            numMatches = judgeNum;
            // add disclaimer saying that for lower ranked teams to play, they may need to be switched in(only top x(according to judge) teams are looked at)
            // may do differnt way potentially
            // int sumOthers = 0;
            // for(int i = 1; i<players.length; i++){
            //     sumOthers+=players[i];
            // }
            // if(players[0]>sumOthers){
            //     players[0] = sumOthers;
            // }
            // // for(int i = 0; i <players.length; i++){
            // //     if(players[i]>judgeNum){
            // //         players[i] = judgeNum;
            // //     }
            // // }
        }else{
            // if(playNum%2==1){
            //     if(players[0]>0){
            //         players[0]--;
            //         playNum--;
            //     }
            // }
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
        for(int i = 0; i < judgers.length; i++){
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
        long startTime = System.currentTimeMillis();
        long twentySeconds = 20*1000;
        String[] match = new String[numMatches];
        int pos = players.length-1;
        
        for(int i = 0; i<numMatches; i++){
            if(System.currentTimeMillis()-startTime>twentySeconds){
                System.out.println("Match Failed");
                String[] failedTest = {"Test Failed"};
                return failedTest;
            }
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
            if(pos < 0){
                pos = players.length-1;
                
            }
        }

        int timeSinceMatch = 0;

        for(int i = 0; i<numMatches; i++){
            if(System.currentTimeMillis()-startTime>twentySeconds){
                System.out.println("Match Failed");
                String[] failedTest = {"Test Failed"};
                return failedTest;
            }
            if(players[pos].length>0&&allUsed(players[pos])){
                String matched = "";
                for(int l = 0; l < match.length; l++){
                    matched+=match[l];
                }
                for(int b = 0; b<players[pos].length; b++){
                    String check = players[pos][b].team+"~"+players[pos][b].number+"|";
                    if(matched.contains(check)){
                        players[pos][b].used = true;
                    }else{
                        players[pos][b].used = false;
                        playersToUse++;
                    }
                }
            }
            
            if(players[pos].length>0&&match[i].split("~")[0].equals(players[pos][0].team)&&timeSinceMatch<players.length-1){
                i--;
                timeSinceMatch++;
            }else{
                boolean matchMade = false;
                for(int j = 0; j<players[pos].length; j++){
                    if(prev.length>0){
                        //check if faced last match
                        boolean foundPrev = false;
                        for(int k = 0; k <prev.length; k++){
                            if(prev[k].contains(match[i])){
                                if(prev[k].contains(players[pos][j].team+"~"+players[pos][j].number+"|")){
                                    foundPrev = true;
                                    break;
                                }
                            }
                        }
                        if(foundPrev){
                            if(timeSinceMatch>players.length-1&&!players[pos][j].used){
                                if(swap(match, i, players[pos][j], prev)){
                                    timeSinceMatch = 0;
                                    matchMade = true;
                                    players[pos][j].used = true;
                                    playersToUse--;
                                    break;
                                }
                            }
                            continue;
                        }
                        
                    }
                    //given a circulation without match has occured, swap if player matches a team
                    if(match[i].split("~")[0].equals(players[pos][0].team)&&
                    timeSinceMatch>=judges.length-1&&!players[pos][j].used){
                        if(swap(match, i, players[pos][j], prev)){
                            timeSinceMatch = 0;
                            matchMade = true;
                            players[pos][j].used = true;
                            playersToUse--;
                            break;
                        }
                    }
                    
                    //attempt match, if not all player are used for this team, match should be forced
                    if(!players[pos][j].used){
                        players[pos][j].used = true;
                        matchMade = true;
                        timeSinceMatch = 0;
                        playersToUse--;
                        match[i]+=players[pos][j].team+"~"+players[pos][j].number+"|";
                        break;
                    }
                }
                if(!matchMade){
                    i--;
                    timeSinceMatch++;
                }
            }
            
            pos--;

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

            if(pos < 0){
                pos = players.length-1; 
                
            }
        }

        timeSinceMatch = 0;
        pos = judges.length-1;
        //System.out.println("j");
        for(int i = 0; i < numMatches; i++){
            if(System.currentTimeMillis()-startTime>twentySeconds){
                System.out.println("Match Failed");
                String[] failedTest = {"Test Failed"};
                return failedTest;
            }
            // if(prev.length>0&&timeSenseMatch<6){
            //     System.out.println(i+" "+timeSenseMatch);
            //     for(int j = 0; j < prev.length; j++){
            //         System.out.print(prev[j]+" ");
            //     }
            //     System.out.println();
            //     for(int j = 0; j < match.length; j++){
            //         System.out.print(match[j]+" ");
            //     }
            //     System.out.println();
            // }

            // if(pos == 2 && prev.length>0){
            //     System.out.println("hello");
            // }

            //make sure judges of all teams are available for use
            if(judges[pos].length>0&&allUsed(judges[pos])){
                String matched = "";
                for(int l = 0; l < match.length; l++){
                    matched+=match[l];
                }
                for(int b = 0; b<judges[pos].length; b++){
                    String check = "J"+judges[pos][b].team+"~"+judges[pos][b].number;
                    if(matched.contains(check)){
                        judges[pos][b].used = true;
                    }else{
                        judges[pos][b].used = false;
                        judgesToUse++;
                    }
                }
            }

            //check if any judge is from same team as debater,(only if full rotation has not occuredd)
            //System.out.println(match[i]);
            if(judges[pos].length>0&&(match[i].split("~")[0].equals(judges[pos][0].team)||
            match[i].split("\\|")[1].split("~")[0].equals(judges[pos][0].team))&&
            timeSinceMatch<judges.length-1){
                i--;
                timeSinceMatch++;
            }else{
            //go through seeing if judge from this team can be added
                boolean matchMade = false;
                for(int j = 0; j < judges[pos].length; j++){
                    //do second round checks
                    if(prev.length>0){//exceptions for if timeSenseMatch is long
                        //check judge same teams last round
                        boolean foundPrev = false;;
                        for(int k = 0; k<prev.length; k++){
                            if(prev[k].split("\\|")[0].equals(match[i].split("\\|")[0])||
                            prev[k].split("\\|")[1].equals(match[i].split("\\|")[0])||
                            prev[k].split("\\|")[0].equals(match[i].split("\\|")[1])||
                            prev[k].split("\\|")[1].equals(match[i].split("\\|")[1])){
                                if(prev[k].contains("J"+judges[pos][j].team+"~"+judges[pos][j].number)){
                                    foundPrev = true;
                                    break;
                                }
                            }
                        }

                        //if a judge pairing was found, check if swap should be tried, otherwise continue
                        if(foundPrev){
                            if(timeSinceMatch>judges.length-1&&!judges[pos][j].used){
                                if(swap(match, i, pos, judges[pos][j], prev)){
                                    timeSinceMatch = 0;
                                    matchMade = true;
                                    judges[pos][j].used = true;
                                    judgesToUse--;
                                    break;
                                }
                            }
                            continue;
                        }
                        //check if a player should recieve support, if so match and break/continue
                        //gonna come back later lol
                    }

                    //given a circulation without match has occured, swap if judge matches a team
                    if((match[i].split("~")[0].equals(judges[pos][0].team)||
                    match[i].split("\\|")[1].split("~")[0].equals(judges[pos][0].team))&&
                    timeSinceMatch>=judges.length-1){
                        if(swap(match, i, pos, judges[pos][j], prev)){
                            timeSinceMatch = 0;
                            matchMade = true;
                            judges[pos][j].used = true;
                            judgesToUse--;
                            break;
                        }
                    }
                    
                    //attempt match, if not all judges are used for this team, match should be forced
                    if(!judges[pos][j].used){
                        judges[pos][j].used = true;
                        matchMade = true;
                        timeSinceMatch = 0;
                        judgesToUse--;
                        match[i]+="J"+judges[pos][j].team+"~"+judges[pos][j].number;
                        break;
                    }
                }
                //if match was not made(and it's been a while since last match) swap
                if(!matchMade){
                    i--;
                    timeSinceMatch++;
                }
            }

            //moves to next team
            pos--;

            //refills judges available for use(if not this round)
            if(judgesToUse==0){
                String matched = "";
                for(int l = 0; l < match.length; l++){
                    matched+=match[l];
                }
                for(int a = 0; a<judges.length; a++){
                    for(int b = 0; b<judges[a].length; b++){
                        String check = "J"+judges[a][b].team+"~"+judges[a][b].number;
                        if(matched.contains(check)){
                            judges[a][b].used = true;
                        }else{
                            judges[a][b].used = false;
                            judgesToUse++;
                        }
                    }
                }
            }

            //resets teams to beginning(so you swithc every time)
            if(pos<0){
                pos = judges.length-1;
            }
        }
        return match;
    }

    public static boolean swap(String[] matches, int max, debater player, String[] prev){
        String toSwap = player.team+"~"+player.number+"|";

        //go through and see if swap can be made
        for(int i = 0; i<max; i++){
            if(prev.length>0){
                boolean justContinue = false;
                for(int j = 0; j<prev.length; j++){
                    if(prev[j].contains(matches[i].split("\\|")[0]+"|")){
                        if(prev[j].contains(toSwap)){
                            justContinue = true;
                            break;
                        }
                    }
                }

                if(justContinue){
                    continue;
                }
            }

            //check if judges team matches any of the debaters team on the swapping team
            if(!(matches[i].split("\\|")[0].split("~")[0].equals(player.team)||
            matches[max].split("\\|")[0].split("~")[0].equals(matches[i].split("\\|")[1].split("~")[0]))){
                matches[max]+=matches[i].split("\\|")[1]+"|";
                matches[i] = matches[i].split("\\|")[0]+"|"+toSwap;
                return true;
            }
        }

        //force swap as long as teams(of debaters) don't match lol
        if(prev.length>0){
            for(int i = 0; i<max; i++){
                if(prev.length>0){
                    boolean justContinue = false;
                    for(int j = 0; j<prev.length; j++){
                        if(prev[j].contains(matches[i].split("\\|")[0]+"|")){
                            if(prev[j].contains(toSwap)){
                                justContinue = true;
                                break;
                            }
                        }
                    }
    
                    if(justContinue){
                        continue;
                    }
                }

                //check if player team matches any of the debaters team on the swapping team
                matches[max]+=matches[i].split("\\|")[1]+"|";
                matches[i] = matches[i].split("\\|")[0]+"|"+toSwap;
                return true;
            }
        }

        return false;
    }

    //if possible, will swap a value(returns true), if not, returns false
    public static boolean swap(String[] matches, int max, int pos, debater judge, String[] prev){
        // look into:
        // should never swap if teams overlap previous matchs
        // there are opportunities when swapping(when position matches) could be beneficial
        //also times with judge priority to be considered

        //System.out.println("here");
        
        String toSwap = "J"+judge.team+"~"+judge.number;

        //go through and see if swap can be made
        for(int i = 0; i<max; i++){
            if(prev.length>0){
                boolean justContinue = false;
                for(int j = 0; j<prev.length; j++){
                    if(prev[j].contains(matches[i].split("\\|")[0]+"|")||
                    prev[j].contains(matches[i].split("\\|")[1]+"|")){
                        if(prev[j].split("\\|")[2].equals(toSwap)){
                            justContinue = true;
                            break;
                        }
                    }

                    if(prev[j].contains(matches[max].split("\\|")[0]+"|")||
                    prev[j].contains(matches[max].split("\\|")[1]+"|")){
                        if(prev[j].split("\\|")[2].equals(matches[i].split("\\|")[2])){
                            justContinue = true;
                            break;
                        }
                    }
                }

                if(justContinue){
                    continue;
                }
            }

            //check if judges team matches any of the debaters team on the swapping team
            if(!(matches[i].split("\\|")[0].split("~")[0].equals(judge.team)||
            matches[i].split("\\|")[1].split("~")[0].equals(judge.team)||
            matches[max].split("\\|")[0].split("~")[0].equals(matches[i].split("J")[1].split("~")[0])||
            matches[max].split("\\|")[0].split("~")[0].equals(matches[i].split("J")[1].split("~")[0]))){
                matches[max]+=matches[i].split("\\|")[2];
                matches[i] = matches[i].split("J")[0]+toSwap;
                return true;
            }
        }

        //force swap as long as teams(of debaters) don't match lol
        if(prev.length>0){
            for(int i = 0; i<max; i++){
                if(prev.length>0){
                    boolean justContinue = false;
                    for(int j = 0; j<prev.length; j++){
                        if(prev[j].contains(matches[i].split("\\|")[0]+"|")||
                        prev[j].contains(matches[i].split("\\|")[1]+"|")){
                            if(prev[j].split("\\|")[2].equals(toSwap)){
                                justContinue = true;
                                break;
                            }
                        }

                        if(prev[j].contains(matches[max].split("\\|")[0]+"|")||
                        prev[j].contains(matches[max].split("\\|")[1]+"|")){
                            if(prev[j].split("\\|")[2].equals(matches[i].split("\\|")[2])){
                                justContinue = true;
                                break;
                            }
                        }
                    }

                    if(justContinue){
                        continue;
                    }
                }

                //check if judges team matches any of the debaters team on the swapping team
                matches[max]+=matches[i].split("\\|")[2];
                matches[i] = matches[i].split("J")[0]+toSwap;
                return true;
            }
        }


        return false;
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