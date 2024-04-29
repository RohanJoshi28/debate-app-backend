package Matchmaking;

import People.Judge;
import People.Team;
import Round.Round;

public class MatchEvaluator {
    //Determines if a matchup is absolutely not allowed
    public boolean IsIllegalMatchup(Team team1, Team team2, Judge judge) {
        //Teams from the same school can never debate each other
        return team1.school == team2.school;
    }

    private static final int RANK_DIFFERENCE_PENALTY = 1;
    private static final int WIN_DIFFERENCE_PENALTY = 10;
    private static final int TEAM_ALREADY_GONE_PENALTY = 100;
    private static final int JUDGE_ALREADY_GONE_PENALTY = 1000;
    private static final int JUDGE_JUDGES_TEAM_TWICE_PENALTY = 10000;
    private static final int TEAM_SAME_SIDE_PENALTY = 100000;
    private static final int JUDGE_HOME_SCHOOL_PENALTY = 1000000;
    private static final int TEAMS_MATCHED_TWICE_PENALTY = 10000000;
    
    //Calculates a score that reflects how well of a matchup these teams and judge are
    public int EvaluateMatchup(Team affirmativeTeam, Team negativeTeam, Judge judge, Round lastRound) {
        int score = 0;

        //Penalize matching teams of different ranks
        int rankDifference = Math.abs(affirmativeTeam.rank - negativeTeam.rank);
        score -= rankDifference * RANK_DIFFERENCE_PENALTY;

        //Penalize matching teams with different number of wins
        int winDifference = Math.abs(affirmativeTeam.wins - negativeTeam.wins);
        score -= winDifference * WIN_DIFFERENCE_PENALTY;

        //Penalize matching judges with teams from their own school
        if (affirmativeTeam.school == judge.school) {
            score -= JUDGE_HOME_SCHOOL_PENALTY;
        }

        if (negativeTeam.school == judge.school) {
            score -= JUDGE_HOME_SCHOOL_PENALTY;
        }

        //Don't consider the last round if there isn't one
        if (lastRound == null) {
            return score;
        }
        
        //Penalize matching teams against each other again
        if (lastRound.AreTeamsMatched(affirmativeTeam, negativeTeam)) {
            score -= TEAMS_MATCHED_TWICE_PENALTY;
        }

        //Penalize putting teams on the affirmative/negative side twice
        if (lastRound.IsTeamAffirmative(affirmativeTeam)) {
            score -= TEAM_SAME_SIDE_PENALTY;
        }

        if (lastRound.IsTeamNegative(negativeTeam)) {
            score -= TEAM_SAME_SIDE_PENALTY;
        }

        //Penalize matching teams again (so teams which haven't been matched can go)
        if (lastRound.IsTeamMatched(affirmativeTeam)) {
            score -= TEAM_ALREADY_GONE_PENALTY;
        }

        if (lastRound.IsTeamMatched(negativeTeam)) {
            score -= TEAM_ALREADY_GONE_PENALTY;
        }

        //Penalize judging the same team twice
        if (lastRound.JudgeJudgesTeam(judge, affirmativeTeam)) {
            score -= JUDGE_JUDGES_TEAM_TWICE_PENALTY;
        }

        if (lastRound.JudgeJudgesTeam(judge, negativeTeam)) {
            score -= JUDGE_JUDGES_TEAM_TWICE_PENALTY;
        }

        //Penalize matching judges again (so judges which haven't been matched can go)
        if (lastRound.IsJudgeMatched(judge)) {
            score -= JUDGE_ALREADY_GONE_PENALTY;
        }

        return score;
    }
}
