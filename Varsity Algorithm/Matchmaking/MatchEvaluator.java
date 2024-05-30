package Matchmaking;

import People.Judge;
import People.Team;
import Round.Round;

public class MatchEvaluator {
    //TODO: Rank judges and have higher judges preferrably judge in the afternoon
    //and preferrably judge higher ranked teams
    private static final int RANK_DIFFERENCE_PENALTY = 1;
    private static final int WIN_DIFFERENCE_PENALTY = 10;
    private static final int TEAM_ALREADY_GONE_PENALTY = 100;
    private static final int JUDGE_ALREADY_GONE_PENALTY = 1000;
    private static final int JUDGE_JUDGES_TEAM_TWICE_PENALTY = 10000;
    private static final int TEAM_SAME_SIDE_PENALTY = 100000;
    private static final int JUDGE_HOME_SCHOOL_PENALTY = 1000000;
    private static final int TEAMS_FROM_SAME_SCHOOL_PENALTY = 10000000;
    private static final int NO_JUDGE_SUPPORT_PENALTY = 100000000;
    private static final int TEAMS_MATCHED_TWICE_PENALTY = 1000000000;
    
    //Calculates a score that reflects how well of a matchup these teams and judge are
    public int EvaluateMatchup(Team affirmativeTeam, Team negativeTeam, Judge judge, Round lastRound) {
        //Initialize a score counter
        int score = 0;

        //Penalize matching teams from the same school
        if (affirmativeTeam.school == negativeTeam.school) {
            score -= TEAMS_FROM_SAME_SCHOOL_PENALTY;
        }

        //Penalize matching teams of different ranks
        int rankDifference = Math.abs(affirmativeTeam.rank - negativeTeam.rank);
        score -= rankDifference * RANK_DIFFERENCE_PENALTY;

        //Penalize matching teams with different numbers of wins
        int winDifference = Math.abs(affirmativeTeam.wins - negativeTeam.wins);
        score -= winDifference * WIN_DIFFERENCE_PENALTY;

        //Penalize matching judges with teams from their own school
        if (affirmativeTeam.school == judge.school) {
            score -= JUDGE_HOME_SCHOOL_PENALTY;
        }

        if (negativeTeam.school == judge.school) {
            score -= JUDGE_HOME_SCHOOL_PENALTY;
        }

        //Don't consider factors relating to a previous round if there isn't one
        if (lastRound == null) {
            return score;
        }

        //Penalize putting a team against a judge that isn't from their school
        //if their last opponent was judged by a judge from the opponent's school
        Team lastOpponent = lastRound.GetOpponent(affirmativeTeam);

        if (lastOpponent != null) {
            Judge lastJudge = lastRound.GetJudge(affirmativeTeam);

            if (lastJudge.school == lastOpponent.school && judge.school != affirmativeTeam.school) {
                score -= NO_JUDGE_SUPPORT_PENALTY;
            }
        }

        lastOpponent = lastRound.GetOpponent(negativeTeam);

        if (lastOpponent != null) {
            Judge lastJudge = lastRound.GetJudge(negativeTeam);

            if (lastJudge.school == lastOpponent.school && judge.school != negativeTeam.school) {
                score -= NO_JUDGE_SUPPORT_PENALTY;
            }
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

        //Penalize matching teams that have already debated
        //(so teams which haven't been matched can debate)
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
