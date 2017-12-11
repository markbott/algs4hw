import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private final String[] teamNames;
    private final int[] w;
    private final int[] loss;
    private final int[] r;
    private final int[][] g;
    private int wMax = Integer.MIN_VALUE;
    private final int N;
    private final Map<String, Set<String>> eliminationInfo = new HashMap<>();
    private static final double FLOATING_POINT_EPSILON = 1E-11;

    public BaseballElimination(String filename) { // create a baseball division
                                                  // from given filename in
                                                  // format specified below
        if (filename == null) {
            throw new IllegalArgumentException("filename");
        }

        In infile = new In(filename);
        N = infile.readInt();

        teamNames = new String[N];
        w = new int[N];
        loss = new int[N];
        r = new int[N];
        g = new int[N][N];

        for (int i = 0; i < N; i++) {
            teamNames[i] = infile.readString();

            w[i] = infile.readInt();
            wMax = Math.max(wMax, w[i]);
            loss[i] = infile.readInt(); // losses
            r[i] = infile.readInt();

            for (int j = 0; j < N; j++) {
                if (i == j) {
                    infile.readString(); // read '-'
                } else {
                    g[i][j] = infile.readInt();
                }
            }
        }
    }

    private FlowNetwork flowNetwork(int teamId) {
        FlowNetwork flow = new FlowNetwork(2 /* s,t */ + N * N + N);

        final int SOURCE = 0;
        final int TEAMS = 1 + N * N;
        final int T = TEAMS + N;

        for (int i = 0; i < N; i++) {
            if (i == teamId) {
                continue;
            }

            for (int j = i + 1; j < N; j++) {
                if (i == j || j == teamId) {
                    continue;
                }

                if (g[i][j] != 0) {
                    int matchup = 1 + N * i + j;
                    // S -> game
                    FlowEdge edge = new FlowEdge(SOURCE, matchup, g[i][j]);
                    flow.addEdge(edge);

                    // game -> team
                    flow.addEdge(new FlowEdge(matchup, TEAMS + i, Double.POSITIVE_INFINITY));
                    flow.addEdge(new FlowEdge(matchup, TEAMS + j, Double.POSITIVE_INFINITY));
                }
            }

            // team -> t
            flow.addEdge(new FlowEdge(TEAMS + i, T, w[teamId] + r[teamId] - w[i]));
        }

        // StdOut.print(flow.toString());
        return flow;
    }

    public int numberOfTeams() { // number of teams
        return N;
    }

    public Iterable<String> teams() { // all teams
        return Arrays.asList(teamNames);
    }

    private int teamIdx(String team) {
        // linear search -- there should only be a handful of teams
        for (int i = 0; i < N; i++) {
            if (this.teamNames[i].equals(team)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid team " + team);
    }

    public int wins(String team) { // number of wins for given team
        return w[teamIdx(team)];
    }

    public int losses(String team) { // number of losses for given team
        return loss[teamIdx(team)];

    }

    public int remaining(String team) { // number of remaining games for given
                                        // team
        return r[teamIdx(team)];

    }

    public int against(String team1, String team2) { // number of remaining
                                                     // games between team1 and
                                                     // team2
        return g[teamIdx(team1)][teamIdx(team2)];
    }

    public boolean isEliminated(String team) { // is given team eliminated?
        return !eliminatingTeams(team).isEmpty();
    }

    // return the list of teams that can eliminate the passed-in parameter
    private Set<String> eliminatingTeams(String team) {
        int teamId = teamIdx(team);
        
        if (eliminationInfo.containsKey(team))
            return eliminationInfo.get(team);

        Set<String> eliminatingTeams = new HashSet<>(N);
        // not thread safe, but callers should be single threaded for this use
        eliminationInfo.put(team, eliminatingTeams);

        // trivial case
        if (w[teamId] + r[teamId] < wMax) {
            for (int i = 0; i < N; i++) {
                if (w[teamId] + r[teamId] < w[i]) {
                    eliminatingTeams.add(this.teamNames[i]);
                }
            }
            return eliminatingTeams;
        }

        FlowNetwork flow = flowNetwork(teamId);

        FordFulkerson ff = new FordFulkerson(flow, 0, flow.V() - 1);

        for (FlowEdge e : flow.adj(0)) {
            if (Math.abs(e.capacity() - e.flow()) > FLOATING_POINT_EPSILON) {
                // StdOut.println(e.toString());
                // e.to() == 1 + n*i + j
                int j = (e.to() - 1) % N;
                int i = (e.to() - 1) / N;

                if (i != teamId && (w[i] + r[i]) >= w[teamId])
                    eliminatingTeams.add(this.teamNames[i]);
                if (j != teamId && (w[j] + r[j]) >= w[teamId])
                    eliminatingTeams.add(this.teamNames[j]);
            }
        }

        return eliminatingTeams;
    }

    public Iterable<String> certificateOfElimination(String team) { // subset R
                                                                    // of teams
                                                                    // that
                                                                    // eliminates
                                                                    // given
                                                                    // team;
                                                                    // null if
                                                                    // not
                                                                    // eliminated
        Set<String> e = eliminatingTeams(team);
        return e.isEmpty() ? null : e;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
        
        StdOut.println("Princeton: " + division.certificateOfElimination("Princeton"));
    }
}
