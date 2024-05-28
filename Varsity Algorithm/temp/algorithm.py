class algorithm():
        def JVMatches(players, judges):
                numMatches = 0
                if(sum(players)>sum(judges)):
                        numMatches = sum(judges)
                else:
                        numMatches = sum(players)
                
                copyP = players#should be a copy, might need to fix later for python
                copyJ = judges#same problem as above
                round1 = roundJV(copyP, copyJ, numMatches, [], players, judges)
                round2 = roundJV(copyP, copyJ, numMatches, round1, players, judges)
                matches = [0,0]
                matches[0] = round1
                matches[1] = round2
                return matches
        
        def roundJV(players, judges, matches, previousRound, origP, origJ):
                pos = len(players)-1
                excludeP = players#same copy issue
                excludeJ = judges#isue as above
                excludedP = False
                excludedJ = False
                match = matches#one of the changes cause arrays ain't in python

                for i in range(len(matches)):
                        if(players[pos]>0):
                                if(excludedP):
                                        if(players[pos]!= excludeP[pos]):
                                                match[i] = (pos) + players[pos]
                                                players[pos] -= 1
                                else:
                                        match[i] = pos + players[pos]
                                        players[pos]-=1
                        pos -= 1
                        if(pos < 0):
                                if(sum(players) == 0):
                                        players = origP
                                        excludedP = True
                                pos = len(players)-1
                
                secondTime = False

                for i in range(len(matches)):
                        if(players[pos]>0):
                                if(excludedP and players[pos] == excludeP[pos]):
                                        pos = pos
                                else:
                                        if(match[i][0] == pos):#could be something super rong here
                                                if(secondTime):
                                                        match[i] += pos + players[pos]
                                                        players[pos] -=1
                                                        secondTime = False
                                                else:
                                                        secondTime = True
                                        else:
                                                match[i] += pos + players[pos]
                                                players[pos] -= 1
                                                secondTime = False
                        pos-=1
                        if(pos < 0):
                                if(sum(players) == 0):
                                        players = origP
                                        excludedP = True
                                pos = len(players)-1 
                secondTime = false;

                for i in range(len(matches)):
                        if(judges[pos]>0):
                                if(excludedJ and judges[pos] == excludeJ[pos] or cannotJudge(pos + i, match[i], previousRound)):
                                        secondTime = True
                                else:
                                        if(match[i][0] == pos):#another potential place of error
                                                if(secondTime):
                                                        match[i] += pos + judges[pos]
                                                        judges[pos] -= 1
                                                        secondTime = False;
                                                else:
                                                        secondTime = True;
                                        else:
                                                match[i] += pos + judges[pos]
                                                judges[pos] -=1
                                                secondTime = False;
                        pos-=1
                        if(pos < 0):
                                if(sum(judges) == 0):
                                        judges = origJ
                                        excludedJ = True
                                pos = players.length-1 
                
                return match
        
        def cannotJudge(judge, match, prev):
                for i in range(len(prev)):
                        if(judge in prev[i]):
                                if(match[:2] in prev[i] or match[:3] in prev[i] or match[-2:] in prev[i] or match[-3:] in prev[i]):
                                        return False
                return True
        
        def sum(array):
                sum = 0
                for i in range (len(array)):
                        sum+=array[i]
                return sum