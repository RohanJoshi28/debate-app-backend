import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class algorithm3{
    public static void main(String[] args) throws Exception{
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


    public static String[][] JVMatches(int[] players, int[] Judges){
        //create arrays for debaters and judges
        debater[][] debaters = new debater[players.length][];
        debater[][] judges = new debater[Judges.length][];

        int debaterCount = 0;
        int judgeCount = 0;

        //fill debater and judge arrays with debater class, matching to teams
        for(int i = 0; i < players.length; i++){
            debaters[i] = new debater[players[i]];
            judges[i] = new debater[Judges[i]];

            for(int j = 0; j<debaters[i].length; j++){
                debaters[i][j] = new debater(false, i, j);
                debaterCount++;
            }
            for(int j = 0; j<judges[i].length; j++){
                judges[i][j] = new debater(true, i, j);
                judgeCount++;
            }
        }

        String teamsDisavantaged = "";

        //finds how many matches should occur based on # of debaters and judges
        int rounds = 0;
        if(debaterCount/2>judgeCount){
            rounds = judgeCount;
            for(int i = 0; i<debaters.length; i++){
                if(debaters[i].length/2>judges[i].length){
                    for(int j = 0; j<debaters[i].length; j++){
                        debaters[i][j].disadvantage = true;
                        teamsDisavantaged += ","+i+",";
                    }
                }
            }
        }else{
            rounds = debaterCount/2;
        }

        //generates matches for rounds 1 and 2
        debater[][] round1 = round1(rounds, debaters, judges);
        debater[][] round2 = round2(round1, rounds, debaters, judges, teamsDisavantaged);

        //changes round results to a returnable format
        String[][] finished = new String[2][round1.length];
        for(int i = 0; i < round1.length; i++){
            finished[0][i] = round1[i][0].team+"~"+round1[i][0].number+"|"+round1[i][1].team+"~"+round1[i][1].number+"|"+"J"+round1[i][2].team+"~"+round1[i][2].number;
            finished[1][i] = round2[i][0].team+"~"+round2[i][0].number+"|"+round2[i][1].team+"~"+round2[i][1].number+"|"+"J"+round2[i][2].team+"~"+round2[i][2].number;
        }

        return finished;
    }

    public static debater[][] round1(int rounds, debater[][] debaters, debater[][] judges){
        debater[][] round1 = new debater[rounds][3];

        int pos = 0;

        //generates affirmitive for round 1, adding teams in order, only checking to see debater hasn't been used
        for(int i = 0; i < round1.length; i++){
            for(int j = 0; j < debaters[pos].length; j++){
                if(!debaters[pos][j].used){
                    debaters[pos][j].used = true;
                    debaters[pos][j].roundUsed = 1;
                    debaters[pos][j].debatorType = 1;
                    round1[i][0] = debaters[pos][j];
                    i++;
                    break;
                }
            }

            if(++pos>=debaters.length){
                pos = 0;
            }
            
            i--;
        }

        //generates negative debators
        int time = 0;;
        for(int i = 0; i < round1.length; i++){

            //before trying to change current affirmative debator, check if you can swap negative ones
            //if loop without match has been made, see if you can swap around a couple debators
            if(time%rounds==0&&time>0){
                boolean matchmade = false;
                for(int j = 0; j<i; j++){
                    if(round1[j][1].team!=round1[i][0].team){
                        for(int k = 0; k<debaters.length; k++){
                            for(int l = 0; l < debaters[k].length; l++){
                                if(!debaters[k][l].used&&round1[j][0].team!=debaters[k][l].team){
                                    debaters[k][l].used = true;
                                    debaters[k][l].roundUsed = 1;
                                    debaters[k][l].debatorType = 2;
                                    round1[i][1] = round1[j][1];
                                    round1[j][1] = debaters[k][l];
                                    time = 0;
                                    //i++;
                                    pos = ++pos%debaters.length;
                                    matchmade = true;
                                    break;
                                }
                            }
                            if(matchmade){
                                break;
                            }
                        }
                    }
                    if(matchmade){
                        break;
                    }
                }
                if(matchmade){
                    continue;
                }
            }

            //if gone through a full pass, try seeing if changing the affirmative team works
            if(time%rounds==0&&time>0){
                int orig = pos;
                pos = (round1[i][0].team+1)%debaters.length;
                while(pos!=round1[i][0].team){
                    for(int j = 0; j<debaters[pos].length; j++){
                        if(!debaters[pos][j].used){
                            debaters[pos][j].used = true;
                            debaters[pos][j].roundUsed = 1;
                            debaters[pos][j].debatorType = 2;
                            debaters[round1[i][0].team][round1[i][0].number].used = false;                            
                            debaters[round1[i][0].team][round1[i][0].number].debatorType = 0;                            
                            debaters[round1[i][0].team][round1[i][0].number].roundUsed = 0;
                            round1[i][0] = debaters[pos][j];
                            pos = round1[i][0].team-1;
                            break;
                        }
                    }
                    pos = ++pos%debaters.length;
                }
                pos = orig;
            }

            //only match with own team if you absolutely have to
            if(round1[i][0].team == pos && time<rounds*rounds){
                i--;
                pos = ++pos%debaters.length;
                time++;
                continue;
            }

            for(int j = 0; j < debaters[pos].length; j++){
                if(!debaters[pos][j].used){
                    debaters[pos][j].used = true;
                    round1[i][1] = debaters[pos][j];
                    i++;
                    time = 0;
                    break;
                }
            }
            
            time++;

            if(++pos>=debaters.length){
                pos = 0;
            }

            i--;
        }

        //now generate round 1 judges
        pos = 0;
        time = 0;
        for(int i = 0; i < round1.length; i++){
            // System.out.println(i);
            // System.out.println(round1[i][0].team+"~"+round1[i][0].number+"|");
            // System.out.println(round1[i][1].team+"~"+round1[i][1].number);
            if((round1[i][0].team == pos || round1[i][1].team == pos) && time<rounds){
                i--;
                pos = ++pos%debaters.length;
                time++;
                continue;
            }

            //if loop without match has been made, see if you can swap around a couple judges
            if(time >= rounds){
                boolean matchmade = false;
                for(int j = 0; j<i; j++){
                    if(round1[j][2].team!=round1[i][0].team&&round1[j][2].team!=round1[i][1].team){
                        for(int k = 0; k < judges.length; k++){
                            for(int l = 0; l<judges[k].length; l++){
                                if((!judges[k][l].used)&&judges[k][l].team!=round1[j][0].team&&judges[k][l].team!=round1[j][1].team){
                                    judges[k][l].used = true;
                                    judges[k][l].roundUsed = 1;
                                    round1[i][2] = round1[j][2];
                                    round1[j][2] = judges[k][l];
                                    time = 0;
                                    //i++;
                                    pos = ++pos%debaters.length;
                                    matchmade = true;
                                    break;
                                }
                            }
                            if(matchmade){
                                break;
                            }
                        }
                    }
                    if(matchmade){
                        break;
                    }
                }
                if(matchmade){
                    continue;
                }
            }

            for(int j = 0; j < judges[pos].length; j++){
                if(!judges[pos][j].used){
                    judges[pos][j].used = true;
                    judges[pos][j].roundUsed = 1;
                    if(round1[i][0].team == judges[pos][j].team&&round1[i][0].team!=round1[i][1].team){
                        round1[i][0].judgeSupport = true;
                    }
                    if(round1[i][1].team == judges[pos][j].team&&round1[i][0].team!=round1[i][1].team){
                        round1[i][1].judgeSupport = true;
                    }
                    round1[i][2] = judges[pos][j];
                    time = 0;
                    i++;
                    break;
                }
            }

            time++;

            pos = ++pos%debaters.length;
            
            i--;
        }

        return round1;
    }

    //helper function to check if debaters of type are used
    public static boolean allUsed(debater[] members){
        for(debater d : members){
            if(d.used == false){
                return false;
            }
        }
        return true;
    }

    //checks to see if a previous match was made with these members
    public static boolean prevMatched(debater[][] prev, debater aff, debater neg, debater judge){
        for(debater[] match : prev){
            if(match[0].equals(aff)||match[1].equals(aff)){
                if(match[0].equals(neg)||match[1].equals(neg)){
                    return true;
                }
            }
            if(match[2].equals(judge)){
                if(match[0].equals(neg)||match[1].equals(neg)||(match[0].equals(aff)||match[1].equals(aff))){
                    return true;
                }
            }
        }
        return false;
    }

    //cases to check here
        //none of matches have the same participants //hopefully done
        //proper support given to players of previous rounds with unfair judges
        //reenabling already used debators/judge //hopefully done
        //deprioritizing teams that did not bring enough judges //hopefully done
    public static debater[][] round2(debater[][] round1, int rounds, debater[][] debaters, debater[][] judges, String teamsDisavantaged){
        debater[][] round2 = new debater[rounds][3];

        int pos = 0;
        int time = 0;

        //generates affirmitive for round 1, adding teams in order, only checking to see debater hasn't been used
        for(int i = 0; i < round2.length; i++){

            if(allUsed(debaters[pos])){
                if(!(teamsDisavantaged.contains(","+pos+","))){
                    for(debater d : debaters[pos]){
                        if(d.roundUsed!=2){
                            d.used = false;
                        }
                    }
                }else{
                    if(time>=rounds){
                        for(debater d : debaters[pos]){
                            if(d.roundUsed!=2){
                                d.used = false;
                            }
                        }
                    }
                }
            }

            for(int j = 0; j < debaters[pos].length; j++){
                if(!debaters[pos][j].used&&debaters[pos][j].debatorType!=1){
                    debaters[pos][j].used = true;
                    debaters[pos][j].roundUsed = 2;
                    debaters[pos][j].debatorType = 1;
                    round2[i][0] = debaters[pos][j];
                    time = 0;
                    i++;
                    break;
                }
            }


            if(++pos>=debaters.length){
                pos = 0;
            }

            time++;

            i--;
        }

        //generates negative debators
        time = 0;;
        for(int i = 0; i < round2.length; i++){

            //resets debators when needed
            if(allUsed(debaters[pos])){
                if(!(teamsDisavantaged.contains(","+pos+","))){
                    for(debater d : debaters[pos]){
                        if(d.roundUsed!=2){
                            d.used = false;
                        }
                    }
                }else{
                    if(time>=rounds){
                        for(debater d : debaters[pos]){
                            if(d.roundUsed!=2){
                                d.used = false;
                            }
                        }
                    }
                }
            }

            //before trying to change current affirmative debator, check if you can swap negative ones
            //if loop without match has been made, see if you can swap around a couple debators
            if(time%rounds==0&&time>0){
                boolean matchmade = false;
                for(int j = 0; j<i; j++){
                    if(round2[j][1].team!=round2[i][0].team){
                        for(int k = 0; k<debaters.length; k++){
                            for(int l = 0; l < debaters[k].length; l++){
                                if((!debaters[k][l].used)&&debaters[k][l].debatorType!=2
                                    &&round2[j][0].team!=debaters[k][l].team
                                    &&(!prevMatched(round1, debaters[k][l], round2[j][0], null))
                                    &&(!prevMatched(round1, round2[i][0], round2[j][1], null))){
                                    debaters[k][l].used = true;
                                    debaters[k][l].roundUsed = 2;
                                    debaters[k][l].debatorType = 2;
                                    round2[i][1] = round2[j][1];
                                    round2[j][1] = debaters[k][l];
                                    time = 0;
                                    //i++;
                                    pos = ++pos%debaters.length;
                                    matchmade = true;
                                    break;
                                }
                            }
                            if(matchmade){
                                break;
                            }
                        }
                    }
                    if(matchmade){
                        break;
                    }
                }
                if(matchmade){
                    continue;
                }
            }

            //if gone through a full pass, try seeing if changing the affirmative team works
            if(time%rounds==0&&time>0){
                int temp = pos;
                pos = (round2[i][0].team+1)%debaters.length;
                while(pos!=round2[i][0].team){
                    for(int j = 0; j<debaters[pos].length; j++){
                        if(!debaters[pos][j].used){
                            debaters[pos][j].used = true;
                            debaters[pos][j].roundUsed = 2;
                            debaters[pos][j].debatorType = 2;
                            round2[i][0] = debaters[pos][j];
                            debaters[round2[i][0].team][round2[i][0].number].used = false;                            
                            debaters[round2[i][0].team][round2[i][0].number].debatorType = 2;                            
                            debaters[round2[i][0].team][round2[i][0].number].roundUsed = 0;
                            pos = round2[i][0].team-1;
                            break;
                        }
                    }
                    pos = ++pos%debaters.length;
                }
                pos = temp;
            }

            //only match with own team if you absolutely have to
            if(round2[i][0].team == pos && time<rounds*rounds){
                i--;
                pos = ++pos%debaters.length;
                time++;
                continue;
            }

            for(int j = 0; j < debaters[pos].length; j++){
                if((!debaters[pos][j].used)&&debaters[pos][j].debatorType!=2&&(!prevMatched(round1, debaters[pos][j], round2[i][0], null))){
                    debaters[pos][j].used = true;
                    round2[i][1] = debaters[pos][j];
                    i++;
                    time = 0;
                    break;
                }
            }
            
            time++;

            if(++pos>=debaters.length){
                pos = 0;
            }
            i--;
        }

        //now generate round 1 judges
        pos = 0;
        time = 0;
        for(int i = 0; i < round2.length; i++){

            if(allUsed(judges[pos])){
                for(debater d : judges[pos]){
                    if(d.roundUsed!=2){
                        d.used = false;
                    }
                }
            }

            if(round2[i][0].judgeSupport){
                int temp = round2[i][0].team;
                if(allUsed(judges[temp])){
                    for(debater d : debaters[temp]){
                        if(d.roundUsed!=2){
                            d.used = false;
                        }
                    }
                }
                boolean matched = false;
                for(int j = 0; j < judges[temp].length; j++){
                    if((!judges[temp][j].used)&&(!prevMatched(round1, round2[i][0], round2[i][1], judges[temp][j]))){
                        judges[temp][j].used = true;
                        judges[temp][j].roundUsed = 2;
                        // if(round2[i][0].team == judges[temp][j].team){
                        //     round2[i][0].judgeSupport = true;
                        // }
                        // if(round2[i][1].team == judges[temp][j].team){
                        //     round2[i][1].judgeSupport = true;
                        // }
                        round2[i][2] = judges[temp][j];
                        matched = true;
                        break;
                    }
                }
                if(matched){
                    //i++;
                    time = 0;
                    continue;
                }else{
                    for(int j = 0; j<i; j++){
                        if(round2[j][2].team == temp&&(!prevMatched(round1, round2[i][0], round2[i][1], round2[j][2]))){
                            int count = (temp+1)%debaters.length;
                            while(count!=temp){
                                for(int k = 0; k < judges[count].length; k++){
                                    if(judges[count][k].used==false&&(!prevMatched(round1, round2[j][0], round2[j][1], judges[count][k]))){
                                        count = temp-1;
                                        judges[count][k].used = true;
                                        judges[count][k].roundUsed=2;
                                        matched = true;
                                        time = 0;
                                        judges[i][2] = judges[j][2];
                                        judges[j][2] = judges[count][k];
                                        break;
                                    }
                                }
                                count=++count%debaters.length;
                            }
                            if(matched){
                                break;
                            }
                        }
                    }
                    if(matched){
                        //i++;
                        continue;
                    }
                }
            }else if(round2[i][1].judgeSupport){
                int temp = round2[i][1].team;
                if(allUsed(judges[temp])){
                    for(debater d : debaters[temp]){
                        if(d.roundUsed!=2){
                            d.used = false;
                        }
                    }
                }
                boolean matched = false;
                for(int j = 0; j < judges[temp].length; j++){
                    if((!judges[temp][j].used)&&(!prevMatched(round1, round2[i][0], round2[i][1], judges[temp][j]))){
                        judges[temp][j].used = true;
                        judges[temp][j].roundUsed = 2;
                        // if(round2[i][0].team == judges[pos][j].team){
                        //     round2[i][0].judgeSupport = true;
                        // }
                        // if(round2[i][1].team == judges[pos][j].team){
                        //     round2[i][1].judgeSupport = true;
                        // }
                        round2[i][2] = judges[temp][j];
                        matched = true;
                        break;
                    }
                }
                if(matched){
                    //i++;
                    time = 0;
                    continue;
                }else{
                    for(int j = 0; j<i; j++){
                        if(round2[j][2].team == temp&&(!prevMatched(round1, round2[i][0], round2[i][1], round2[j][2]))){
                            int count = (temp+1)%debaters.length;
                            while(count!=temp){
                                for(int k = 0; k < judges[count].length; k++){
                                    if(judges[count][k].used==false&&(!prevMatched(round1, round2[j][0], round2[j][1], judges[count][k]))){
                                        count = temp-1;
                                        judges[count][k].used = true;
                                        judges[count][k].roundUsed=2;
                                        matched = true;
                                        time = 0;
                                        judges[i][2] = judges[j][2];
                                        judges[j][2] = judges[count][k];
                                        break;
                                    }
                                }
                                count=++count%debaters.length;
                            }
                            if(matched){
                                break;
                            }
                        }
                    }
                    if(matched){
                        //i++;
                        continue;
                    }
                }
            }

            if((round2[i][0].team == pos || round2[i][1].team == pos) && time<rounds){
                i--;
                pos = ++pos%debaters.length;
                time++;
                continue;
            }

            //if loop without match has been made, see if you can swap around a couple judges
            if(time >= rounds){
                boolean matchmade = false;
                for(int j = 0; j<i; j++){
                    if(round2[j][2].team!=round2[i][0].team&&round2[j][2].team!=round2[i][1].team){
                        for(int k = 0; k < judges.length; k++){
                            for(int l = 0; l<judges[k].length; l++){
                                if((!judges[k][l].used)
                                    &&judges[k][l].team!=round2[j][0].team
                                    &&judges[k][l].team!=round2[j][1].team
                                    &&(!prevMatched(round1, round2[i][0], round2[i][1], round2[j][2]))
                                    &&(!prevMatched(round1, round2[j][0], round2[j][1], judges[k][l]))){
                                    judges[k][l].used = true;
                                    judges[k][l].roundUsed = 2;
                                    round2[i][2] = round2[j][2];
                                    round2[j][2] = judges[k][l];
                                    time = 0;
                                    //i++;
                                    pos = ++pos%debaters.length;
                                    matchmade = true;
                                    break;
                                }
                            }
                            if(matchmade){
                                break;
                            }
                        }
                    }
                    if(matchmade){
                        break;
                    }
                }
                if(matchmade){
                    continue;
                }
            }

            for(int j = 0; j < judges[pos].length; j++){
                if(!judges[pos][j].used&&(!prevMatched(round1, round2[i][0], round2[i][1], judges[pos][j]))){
                    judges[pos][j].used = true;
                    judges[pos][j].roundUsed = 2;
                    if(round2[i][0].team == judges[pos][j].team){
                        round2[i][0].judgeSupport = true;
                    }
                    if(round2[i][1].team == judges[pos][j].team){
                        round2[i][1].judgeSupport = true;
                    }
                    round2[i][2] = judges[pos][j];
                    time = 0;
                    i++;
                    break;
                }
            }

            time++;

            pos = ++pos%debaters.length;

            i--;
        }

        return round2;
    }

    
}

class debater{
    boolean isJudge;
    int team;
    int number;
    boolean used;
    boolean disadvantage;
    int debatorType;
    boolean judgeSupport;
    int roundUsed;
    
    public debater(){
        this.isJudge = false;
        this.team = 0;
        this.number = 0;
        this.used = false;
        this.debatorType = 0;
        this.roundUsed = 0;
        this.disadvantage = false;
        this.judgeSupport = false;
    }

    public debater(boolean isJudge, int team, int number){
        this.isJudge = isJudge;
        this.team = team;
        this.number = number;
        this.used = false;
        this.debatorType = 0;
        this.roundUsed = 0;
        this.disadvantage = false;
        this.judgeSupport = false;
    }
}