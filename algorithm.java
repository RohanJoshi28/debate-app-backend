class algorithm{
    public static void main(String[] args) {
        
    }

    public static String[][] JVMatches (int[] players, int[] judges){
        int numMatches = 0;
        if(sum(players)>sum(judges)){
            numMatches = sum(judges);
        }else{
            numMatches = sum(players);
        }
        //the following does not perfectly take into account edge cases
        //the main edge case not accounted four is too many teams
        //in the case of too many teams the largest team will be penalized rather than the host
        //number of matches will be based on judges or players and distributed to teams equally
        int[] copyP = players.clone();
        int[] copyJ = judges.clone();
        String[] round1 = round1JV(copyP, copyJ, numMatches);
        String[] round2 = round2JV(copyP, copyJ, numMatches, round1, players, judges);
        String[][] matches = new String[2][];
        matches[0] = round1;
        matches[1] = round2;
        return matches;
    }

    public static String[] round1JV(int[] players, int[] judges, int matches){
        int pos = players.length-1;
        String[] match = new String[matches];
        for(int i = 0; i<matches; i++){
            if(players[pos]>0){
                match[i] = ((char) pos) + Integer.toString(players[pos]--);
            }
            pos--;
            if(pos < 0){
                pos = players.length-1;
            }
        }

        boolean secondTime = false;
        for(int i = 0; i<matches; i++){
            if(players[pos]>0){
                if(match[i].charAt(0) == (char) pos){
                    if(secondTime){
                        match[i] += ((char) pos) + players[pos]--;
                        secondTime = false;
                    }else{
                        secondTime = true;
                    }
                }else{
                    match[i] += ((char) pos) + players[pos]--;
                    secondTime = false;
                }
            }
            pos--;
            if(pos < 0){
                pos = players.length-1;
            }
        }

        secondTime = false;
        for(int i = 0; i<matches; i++){
            if(judges[pos]>0){
                if(match[i].charAt(0) == (char) pos){
                    if(secondTime){
                        match[i] += ((char) pos) + judges[pos]--;
                        secondTime = false;
                    }else{
                        secondTime = true;
                    }
                }else{
                    match[i] += ((char) pos) + judges[pos]--;
                    secondTime = false;
                }
            }
            pos--;
            if(pos < 0){
                pos = judges.length-1;
            }
        }
        return match;
    }

    public static String[] round2JV(int[] players, int[] judges, int matches, String[] previousRound, int[] origP, int[] origJ){
        int pos = players.length-1;
        int[] excludeP = players.clone();
        int[] excludeJ = judges.clone();
        boolean excludedP = false;
        boolean excludedJ = false;
        String[] match = new String[matches];
        boolean change = false;

        for(int i = 0; i<matches; i++){
            if(players[pos]>0){
                if(excludedP){
                    if(players[pos] != excludeP[pos]){
                        match[i] = ((char) pos) + Integer.toString(players[pos]--);
                    }
                }else{
                    match[i] = ((char) pos) + Integer.toString(players[pos]--);
                    change = false;
                }
            }
            pos--;
            if(pos < 0){
                if(change && !excludedP){
                    players = origP;
                    excludedP = true;
                    pos = players.length-1;
                    continue;
                }
                pos = players.length-1;
                change = true;
            }
        }

        boolean secondTime = false;

        for(int i = 0; i<matches; i++){
            if(players[pos]>0){
                if(excludedP && players[pos] == excludeP[pos]){
                    
                }else{
                    if(match[i].charAt(0) == (char) pos){
                        if(secondTime){
                            match[i] += ((char) pos) + players[pos]--;
                            secondTime = false;
                        }else{
                            secondTime = true;
                        }
                    }else{
                        match[i] += ((char) pos) + players[pos]--;
                        secondTime = false;
                    }
                    change = false;
                }
            }
            pos--;
            if(pos < 0){
                if(change && !excludedP){
                    players = origP;
                    excludedP = true;
                    pos = players.length-1;
                    continue;
                }
                pos = players.length-1; 
                change = true;  
            }
        }

        change = false;
        secondTime = false;

        for(int i = 0; i<matches; i++){
            if(judges[pos]>0){
                if(excludedJ && judges[pos] == excludeJ[pos] || cannotJudge(((char) pos)+Integer.toString(i), match[i], previousRound)){
                    secondTime = true;
                }else{
                    if(match[i].charAt(0) == (char) pos){
                        if(secondTime){
                            match[i] += ((char) pos) + judges[pos]--;
                            secondTime = false;
                        }else{
                            secondTime = true;
                        }
                    }else{
                        match[i] += ((char) pos) + judges[pos]--;
                        secondTime = false;
                    }
                }
            }
            pos--;
            if(pos < 0){
                if(change && !excludedJ){
                    judges = origJ;
                    excludedJ = true;
                    pos = players.length-1;
                    continue;
                }
                pos = players.length-1; 
                change = true;  
            }
        }
        return match;
    }

    public static boolean cannotJudge (String Judge, String match, String[] prev){
        for(int i = 0; i<prev.length; i++){
            // this does not work lol
            // very brute force way of doing it
            // has possibilty of match player number only, instead of player and team
            // main issue comes from having to split chars and ints(which can be interpreted as the same thing)
            if(prev[i].contains(Judge)){
                if(prev[i].contains(match.substring(0, 2)) 
                || prev[i].contains(match.substring(0, 3)) 
                || prev[i].contains(match.substring(2)) 
                || prev[i].contains(match.substring(3))){
                    return false;
                }

            }
        }
        return true;
    }

    public static int sum(int[] array){
        int sum = 0;
        for(int i = 0; i<array.length; i++){
            sum+=array[i];
        }
        return sum;
    }
}