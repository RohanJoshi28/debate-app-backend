package Matchmaking;

import java.util.List;

import Round.Round;
import People.PersonStore;
import People.Team;
import People.Judge;
import Round.Match;

public class RoundGenerator {
    private PersonStore people;
    private MatchEvaluator evaluator;
    private List<Team> availableTeams;
    private List<Judge> availableJudges;

    public RoundGenerator(PersonStore people) {
        this.people = people;
        evaluator = new MatchEvaluator();
    }

    public Round Generate(Round lastRound) {
        Round round = new Round();
        availableTeams = people.GetTeams();
        availableJudges = people.GetJudges();

        //Loop as long as there are enough people to create a match
        while (availableTeams.size() >= 2 && availableJudges.size() >= 1) {
            Team team = GetHighestRankingTeamAvailable();
            availableTeams.remove(team);

            //Search for best opponent
            MatchupResult result = GetBestMatchup(team, lastRound);

            //Do not match this team if there are no valid matchups
            if (result == null) {
                continue;
            }

            Team opponent = result.opponent;
            Judge judge = result.judge;


            //Set people as unavailable
            availableTeams.remove(opponent);
            availableJudges.remove(judge);

            //Create a match
            Match match;

            if (result.isAffirmative) {
                match = new Match(team, opponent, judge);
            } else {
                match = new Match(opponent, team, judge);
            }

            round.matches.add(match);
        }

        return round;
    }

    private Team GetHighestRankingTeamAvailable() {
        Team bestTeam = null;
        int bestRank = Integer.MAX_VALUE;

        for (Team team : availableTeams) {
            if (team.rank < bestRank) {
                bestRank = team.rank;
                bestTeam = team;
            }
        }

        return bestTeam;
    }

    private MatchupResult GetBestMatchup(Team team, Round lastRound) {
        int bestScore = Integer.MIN_VALUE;
        Team bestOpponent = null;
        Judge bestJudge = null;
        boolean isAffirmative = true;

        //Check every team and judge
        for (Team opponent : availableTeams) {
            for (Judge judge : availableJudges) {
                if (evaluator.IsIllegalMatchup(team, opponent,judge)) continue;
                
                //Best affirmative
                int score = evaluator.EvaluateMatchup(team, opponent, judge, lastRound);
                
                if (score > bestScore) {
                    bestJudge = judge;
                    bestScore = score;
                    bestOpponent = opponent;
                    isAffirmative = true;
                }
                
                //Best negative
                score = evaluator.EvaluateMatchup(opponent, team, judge, lastRound);
                
                if (score > bestScore) {
                    bestJudge = judge;
                    bestScore = score;
                    bestOpponent = opponent;
                    isAffirmative = false;
                }
            }
        }

        if (bestOpponent == null) {
            return null;
        }
        
        return new MatchupResult(bestOpponent, bestJudge, isAffirmative);
    }
        
        private class MatchupResult {
            public Team opponent;
            public Judge judge;
            public boolean isAffirmative;

        public MatchupResult(Team opponent, Judge judge, boolean isAffirmative) {
            this.opponent = opponent;
            this.judge = judge;
            this.isAffirmative = isAffirmative;
        }
    }
}
