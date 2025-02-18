import edu.princeton.cs.algs4.In;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;

public class BaseballElimination {
    private final int numberOfTeams;
    private final String[] teams;
    private final int[] wins, losses, left;
    private final int[][] games;

    public BaseballElimination(String filename){
        In f = new In(filename);
        this.numberOfTeams = f.readInt();
        this.teams = new String[this.numberOfTeams];

        this.losses = new int[this.numberOfTeams];
        this.wins = new int[this.numberOfTeams];
        this.left = new int[this.numberOfTeams];
        this.games = new int[this.numberOfTeams][this.numberOfTeams];

        for (int i = 0; i < numberOfTeams; i++) {
            this.teams[i] = f.readString();
            this.wins[i] = f.readInt();
            this.losses[i] = f.readInt();
            this.left[i] = f.readInt();

            for (int j = 0; j < numberOfTeams; j++) {
                this.games[i][j] = f.readInt();
            }
        }
    }                    // create a baseball division from given filename in format specified below
    public int numberOfTeams() {
        return numberOfTeams;
    }                        // number of teams
    public Iterable<String> teams(){
        return Arrays.asList(this.teams);
    }                                // all teams

    public int wins(String team){
        for (int i = 0; i < this.teams.length; i++) {
            if (this.teams[i].equals(team)) {
                return this.wins[i];
            }
        }
        throw new IllegalArgumentException("Team not found: " + team);
    }                      // number of wins for given team

    public int losses(String team) {
        if (team == null) throw new IllegalArgumentException("null team");
        for (int i = 0; i < this.teams.length; i++) {
            if (this.teams[i].equals(team)) {
                return this.losses[i];
            }
        }
        throw new IllegalArgumentException("Team not found: " + team);
    }                    // number of losses for given team

    public int remaining(String team) {
        for (int i = 0; i < this.teams.length; i++) {
            if (this.teams[i].equals(team)) {
                return this.left[i];
            }
        }
        throw new IllegalArgumentException("Team not found: " + team);
    }                 // number of remaining games for given team

    public int against(String team1, String team2) {
        int index1 = -1;
        int index2 = -1;
        for (int i = 0; i < this.teams.length; i++) {
            if (this.teams[i].equals(team1)) {
                index1 = i;
            }
        }
        for (int j = 0; j < this.teams.length; j++) {
            if (this.teams[j].equals(team2)) {
                index2 = j;
            }
        }
        if (index1 == -1 || index2 == -1) {
            throw new IllegalArgumentException("One team not found");
        }

        return games[index1][index2];

    }    // number of remaining games between team1 and team2

    public boolean isEliminated(String team) {
        if (team == null) throw new IllegalArgumentException("null team");
        int index = getTeamIndex(team);
        int maxPossibleWins = wins[index] + left[index];

        for (int i = 0; i < numberOfTeams; i++) {
            if (wins[i] > maxPossibleWins) {
                return true;
            }
        }
        FordFulkerson maxFlow = constructGraph(index);
    	if (maxFlow == null) return true;
    	return NonTrivialElimination(maxFlow, index);
    }              // is given team eliminated?

    private boolean NonTrivialElimination(FordFulkerson maxFlow, int x) {
        int totalCapacity = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (i == x || j == x) continue;
                totalCapacity += games[i][j];
            }
        }
        return maxFlow.value() < totalCapacity;
    }

    private FordFulkerson constructGraph(int x) {
        int numGames = ((numberOfTeams - 1) * (numberOfTeams - 2)) / 2;
        int numTeamVertices = numberOfTeams - 1;  // 除 x 队外的球队数
        int numVertices = numGames + numTeamVertices + 2;
        int s = numGames + numTeamVertices;  // 源点编号
        int t = s + 1;                       // 汇点编号

        FlowNetwork network = new FlowNetwork(numVertices);
        int gameIndex = 0;
        int maxPossibleWins = wins[x] + left[x];

        // 为比赛节点和球队节点之间建立边
        for (int i = 0; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (i == x || j == x) continue;  // 跳过 x 队
                int gameNode = gameIndex++;
                network.addEdge(new FlowEdge(s, gameNode, games[i][j]));
                int teamVertexI = numGames + (i < x ? i : i - 1);
                int teamVertexJ = numGames + (j < x ? j : j - 1);
                network.addEdge(new FlowEdge(gameNode, teamVertexI, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(gameNode, teamVertexJ, Double.POSITIVE_INFINITY));
            }
        }

        // 为球队节点和汇点之间建立边
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == x) continue;
            int capacity = maxPossibleWins - wins[i];
            if (capacity < 0) return null;  // 若为负，说明 x 队已被简单淘汰
            int teamVertex = numGames + (i < x ? i : i - 1);
            network.addEdge(new FlowEdge(teamVertex, t, capacity));
        }

        return new FordFulkerson(network, s, t);
	}

    public Iterable<String> certificateOfElimination(String team) {
        if (team == null) throw new IllegalArgumentException("null team");
        int x = getTeamIndex(team);
        if (!isEliminated(team)) return null;
        
        FordFulkerson maxFlow = constructGraph(x);
        if (maxFlow == null) {
            // 简单淘汰的情况
            List<String> basicEliminators = new ArrayList<>();
            int maxPossibleWins = wins[x] + left[x];
            for (int i = 0; i < numberOfTeams; i++) {
                if (wins[i] > maxPossibleWins) { 
                    basicEliminators.add(teams[i]);
                }
            }
            return basicEliminators.isEmpty() ? null : basicEliminators;
        }
        
        int numGames = ((numberOfTeams - 1) * (numberOfTeams - 2)) / 2;
        List<String> eliminatingTeams = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == x) continue;
            int teamVertex = numGames + (i < x ? i : i - 1);
            if (maxFlow.inCut(teamVertex)) {
                eliminatingTeams.add(teams[i]);
            }
        }
        return eliminatingTeams.isEmpty() ? null : eliminatingTeams;
    }
      // subset R of teams that eliminates given team; null if not eliminated

    private int getTeamIndex(String team) {
        for (int i = 0; i < this.teams.length; i++) {
            if (this.teams[i].equals(team)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Team not found: " + team);
    }
}