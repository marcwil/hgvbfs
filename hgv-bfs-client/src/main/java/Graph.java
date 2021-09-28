package main.java;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.ArrayList;

public class Graph {
    private int n, m;
    private int[] v;
    private HashMap<Integer, Integer> reverseIDs;
    private int[][] e;
    private ArrayList<Integer>[] adj;

    public Graph(String received) {
        System.out.println(received);
        JSONObject graph = new JSONObject(received);
        v = new int[graph.getJSONArray("nodes").length()];
        e = new int[graph.getJSONArray("edges").length()][2];
        Object[] nodes = graph.getJSONArray("nodes").toList().toArray();
        reverseIDs = new HashMap<>(v.length);
        for (int i = 0; i < nodes.length; i++) {
            v[i] = ((HashMap<String, Integer>)nodes[i]).get("id");
            reverseIDs.put(v[i], i);
        }
        Object[] edges = graph.getJSONArray("edges").toList().toArray();
        for (int i = 0; i < edges.length; i++) {
            e[i][0] = ((HashMap<String, Integer>)edges[i]).get("node1");
            e[i][1] = ((HashMap<String, Integer>)edges[i]).get("node2");
        }

        n = v.length;
        m = e.length;
        adj = new ArrayList[n];
        for (int i = 0; i<n; i++) {
            adj[i] = new ArrayList<>();
        }

        for (int i = 0; i < edges.length; i++) {
            int a = e[i][0];
            int b = e[i][1];
            int a_id = reverseIDs.get(a);
            int b_id = reverseIDs.get(b);
            adj[a_id].add(b_id);
            adj[b_id].add(a_id);
        }
    }

    public void bfs(int startNode, int targetNode, HgvClient hgv) {
        boolean[] visited_s = new boolean[n];
        boolean[] visited_t = new boolean[n];
        LinkedList<Integer> queue_s = new LinkedList<>();
        LinkedList<Integer> queue_t = new LinkedList<>();

        int oldStart = startNode;
        int oldTarget = targetNode;
        startNode = reverseIDs.get(startNode);
        targetNode = reverseIDs.get(targetNode);

        visited_s[startNode] = true;
        queue_s.addLast(startNode);
        visited_s[targetNode] = true;
        queue_t.addLast(targetNode);

        hgv.setColor(v[startNode], "#000000");
        hgv.setColor(v[targetNode], "#000000");

        int cost_s = adj[startNode].size();
        int cost_t = adj[targetNode].size();
        boolean done = false;

        while ((!queue_s.isEmpty() || !queue_t.isEmpty()) && !done) {
            if ( (!queue_s.isEmpty() && cost_s <= cost_t) || queue_t.isEmpty()) {
                System.out.println("layer from s");
                // explore layer from s
                int num = queue_s.size();
                int popped = 0;

                cost_s = 0;
                while (!queue_s.isEmpty() && popped < num) {
                    int vert = queue_s.pop();
                    popped += 1;

                    for (int neighbour: adj[vert]) {
                        if (!visited_s[neighbour]) {
                            visited_s[neighbour] = true;
                            queue_s.addLast(neighbour);
                            cost_s += adj[neighbour].size();
                            hgv.setColor(v[neighbour], "#0000FF");
                            if (visited_t[neighbour]) {
                                System.out.println("Found common vertex! " + v[neighbour]);
                                // found it
                                hgv.setColor(v[neighbour], "#00FF00");
                                done = true;
                            }
                        }
                    }
                }
            } else {
                System.out.println("layer from t");
                // explore layer from t
                int num = queue_t.size();
                int popped = 0;

                cost_t = 0;
                while (!queue_t.isEmpty() && popped < num) {
                    int vert = queue_t.pop();
                    popped += 1;

                    for (int neighbour: adj[vert]) {
                        if (!visited_t[neighbour]) {
                            visited_t[neighbour] = true;
                            queue_t.addLast(neighbour);
                            cost_t += adj[neighbour].size();
                            hgv.setColor(v[neighbour], "#FFFF00");
                            if (visited_s[neighbour]) {
                                System.out.println("Found common vertex! " + v[neighbour]);
                                // found it
                                hgv.setColor(v[neighbour], "#00FF00");
                                done = true;
                            }
                        }
                    }
                }

            }
            hgv.render();
            hgv.pause();
        }
    }

    private void visit(int i, HgvClient hgv) {
        hgv.setColor(i, "#0000FF");
    }

    private static int countEdgesFromId(JSONObject graph, int id) {
        int count = 0;
        for (Object edge : graph.getJSONArray("edges")) {
            JSONObject e  = (JSONObject) edge;
            if (e.getInt("node1") == id || e.getInt("node2") == id) {
                count ++;
            }
        }
        return count;
    }
}
