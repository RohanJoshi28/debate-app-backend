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
            int i = 0;
            //in this case add all judges, but give advantage to teams later
            //attempt to balance players, premptively because too few judges, may have to be done later/while algorithm runs
            // while(sum(players)>numMatches*2){
            //     if(players[i]>judges[i]*2){
            //         players[i]--;
            //     }
            //     i++;
            //     if(i>=players.length){
            //         i = 0;
            //     }
            // }
        }else{
            numMatches = playNum/2;
        }
        playersToUse = playNum;
        judgesToUse = judgeNum;

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
        long fiveSeconds = 20*1000;
        String[] match = new String[numMatches];
        int pos = players.length-1;
        
        for(int i = 0; i<numMatches; i++){
            if(System.currentTimeMillis()-startTime>fiveSeconds){
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
            if(System.currentTimeMillis()-startTime>fiveSeconds){
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
        
        for(int i = 0; i < numMatches; i++){
            if(System.currentTimeMillis()-startTime>fiveSeconds){
                System.out.println("Match Failed");
                String[] failedTest = {"Test Failed"};
                return failedTest;
            }

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

            //check if any judge is from same team as debater,(only if full rotation has not occured)
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
                                    String matches = "";
                                    for(int w = 0; w<i; w++){
                                        matches+=match[w];
                                    }
                                    if(matches.contains("J"+judges[pos][j].team+judges[pos][j].number)){ 
                                        judges[pos][j].used = true;
                                        continue;
                                    }
                                    timeSinceMatch = 0;
                                    matchMade = true;
                                    judges[pos][j].used = true;
                                    judgesToUse--;
                                    if(match[i].lastIndexOf(judges[pos][j].team+"~")!=(match[i].indexOf(judges[pos][j].team+"~"))){
                                        if(match[i].lastIndexOf(judges[pos][j].team+"~")!=(match[i].indexOf(judges[pos][j].team+"~"))){
                                            if(match[i].split("\\|")[0].split("~")[0].equals(judges[pos][j].team)){
                                                players[Integer.parseInt(match[i].split("\\|")[1].split("~")[0])][Integer.parseInt(match[i].split("\\|")[1].split("~")[1])].judgeSupport = true;
                                            }else{
                                                players[Integer.parseInt(match[i].split("\\|")[0].split("~")[0])][Integer.parseInt(match[i].split("\\|")[0].split("~")[1])].judgeSupport = true;
                                            }  
                                        } 
                                    }
                                    break;
                                }
                            }
                            continue;
                        }
                        
                        String pair1 = match[i].split("\\|")[0];
                        String pair2 = match[i].split("\\|")[1];
                        String pair = "";

                        //get pair that needs support
                        if(players[Integer.parseInt(pair1.split("~")[0])][Integer.parseInt(pair1.split("~")[1])].judgeSupport){
                            pair = pair1;
                        }else if(players[Integer.parseInt(pair2.split("~")[0])][Integer.parseInt(pair2.split("~")[1])].judgeSupport){
                            pair = pair2;
                        }

                        //check for whether a judge support pair was found
                        if(!pair.equals("")){
                            //change position to target judge of same type
                            int realPos = pos;
                            pos = Integer.parseInt(pair.split("~")[0]);

                            //ensure all available judges(of team) are there
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
                            
                            //if not all judges on your team were used add one to yourself
                            if(!allUsed(judges[pos])){
                                for(int n = 0; n<judges[pos].length; n++){
                                    if(!judges[pos][n].used){
                                        String matches = "";
                                        for(int w = 0; w<i; w++){
                                            matches+=match[w];
                                        }
                                        if(matches.contains("J"+judges[pos][n].team+judges[pos][n].number)){ 
                                            judges[pos][j].used = true;
                                            continue;
                                        }
                                        judges[pos][n].used = true;
                                        matchMade = true;
                                        timeSinceMatch = 0;
                                        judgesToUse--;
                                        match[i]+="J"+judges[pos][n].team+"~"+judges[pos][n].number;
                                        pos = realPos;
                                        break;
                                    }
                                }
                                break;
                            }

                            //if all judges on your team were used, look for whether 
                            //it's possible to swap with a already used judge on your team
                            //given that match is not given judge support                            
                            for(int n = 0; n<i; n++){
                                if(match[n].split("J")[1].split("~")[0].equals(Integer.toString(pos))){
                                    if(match[n].split("\\|")[0].split("~")[0].equals(Integer.toString(pos))){
                                        if(players[pos][Integer.parseInt(match[n].split("\\|")[0].split("~")[1])].judgeSupport){
                                            continue;
                                        }
                                    }else if(match[n].split("\\|")[1].split("~")[0].equals(Integer.toString(pos))){
                                        if(players[pos][Integer.parseInt(match[n].split("\\|")[1].split("~")[1])].judgeSupport){
                                            continue;
                                        }
                                    }
                                    String switch1 = match[i]+"J"+match[n].split("J")[1];
                                    String switch2 = match[n].split("J")[0];
                                    match[n] = switch1;
                                    match[i] = switch2;
                                    break;
                                }
                            }

                            //regardless if match was possible, since a swap was performed, reset i, and restart search
                            i--;
                            players[Integer.parseInt(pair.split("~")[0])][Integer.parseInt(pair.split("~")[1])].judgeSupport = false;
                            pos=realPos;
                            matchMade = true;
                            timeSinceMatch = 0;
                            break;
                        }
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
                            if(match[i].lastIndexOf(judges[pos][j].team+"~")!=(match[i].indexOf(judges[pos][j].team+"~"))){
                                if(match[i].lastIndexOf(judges[pos][j].team+"~")!=(match[i].indexOf(judges[pos][j].team+"~"))){
                                    if(match[i].split("\\|")[0].split("~")[0].equals(judges[pos][j].team)){
                                        players[Integer.parseInt(match[i].split("\\|")[1].split("~")[0])][Integer.parseInt(match[i].split("\\|")[1].split("~")[1])].judgeSupport = true;
                                    }else{
                                        players[Integer.parseInt(match[i].split("\\|")[0].split("~")[0])][Integer.parseInt(match[i].split("\\|")[0].split("~")[1])].judgeSupport = true;
                                    }  
                                }  
                            }
                            break;
                        }
                    }
                    
                    //attempt match, if not all judges are used for this team, match should be forced
                    if(!judges[pos][j].used){
                        String matches = "";
                        for(int w = 0; w<i; w++){
                            matches+=match[w];
                        }
                        if(matches.contains("J"+judges[pos][j].team+judges[pos][j].number)){ 
                            judges[pos][j].used = true;
                            continue;
                        }
                        judges[pos][j].used = true;
                        matchMade = true;
                        timeSinceMatch = 0;
                        judgesToUse--;
                        match[i]+="J"+judges[pos][j].team+"~"+judges[pos][j].number;
                        if(match[i].lastIndexOf(judges[pos][j].team+"~")!=(match[i].indexOf(judges[pos][j].team+"~"))){
                            if(match[i].split("\\|")[0].split("~")[0].equals(judges[pos][j].team)){
                                players[Integer.parseInt(match[i].split("\\|")[1].split("~")[0])][Integer.parseInt(match[i].split("\\|")[1].split("~")[1])].judgeSupport = true;
                            }else{
                                players[Integer.parseInt(match[i].split("\\|")[0].split("~")[0])][Integer.parseInt(match[i].split("\\|")[0].split("~")[1])].judgeSupport = true;
                            }  
                        }
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
        //give advantage to teams that brought enough judges
        for(int i = 0; i < players.length; i++){
            int upto = maxPlayersOnTeam;
            if(players.team.notpunished && players.team.size<maxPlayersOnTeam){
                for(int j = 0; j<playesr[i].length; j++){
                    if(!players[i][j].used){
                        boolean done = false;
                        for(int k = 0; k<match.length; k++){
                            if(!match[k].contains(player.team)){
                                String temp = match[k].split("\\|");
                                if(!temp[k].team.notpunished){
                                    temp[k]= player[i][j];
                                }
                                match = temp[0]+"|"+temp[1]+"|"+temp[2];
                            }
                            done = true;
                            break;
                        }
                        if(done){
                            break;
                        }
                    }
                }
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