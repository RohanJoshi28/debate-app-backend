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
    
    private static final int RANK_DIFFERENCE_PENALTY = 10;
    private static final int JUDGE_TEAM_TWICE_PENALTY = 100;
    private static final int MATCH_UNMATCHED_TEAM_BONUS = 1000;
    private static final int JUDGE_HOME_SCHOOL_PENALTY = 10000;
    
    //Calculates a score that reflects how well of a matchup these teams and judge are
    public int EvaluateMatchup(Team affirmativeTeam, Team negativeTeam, Judge judge, Round lastRound) {
        int score = 0;

        //Penalize matching teams of different ranks
        int rankDifference = Math.abs(affirmativeTeam.rank - negativeTeam.rank);
        score -= rankDifference * RANK_DIFFERENCE_PENALTY;

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

        //Promote matching teams that weren't matched last round
        if (lastRound.IsTeamUnmatched(affirmativeTeam)) {
            score += MATCH_UNMATCHED_TEAM_BONUS;
        }

        if (lastRound.IsTeamUnmatched(negativeTeam)) {
            score += MATCH_UNMATCHED_TEAM_BONUS;
        }

        //Penalize judging the same team twice
        if (lastRound.JudgeJudgesTeam(judge, affirmativeTeam)) {
            score -= JUDGE_TEAM_TWICE_PENALTY;
        }

        if (lastRound.JudgeJudgesTeam(judge, negativeTeam)) {
            score -= JUDGE_TEAM_TWICE_PENALTY;
        }

        return score;
    }
}
