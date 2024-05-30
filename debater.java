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