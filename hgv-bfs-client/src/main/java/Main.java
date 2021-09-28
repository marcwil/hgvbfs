package main.java;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.parseInt(args[0]);
        int graphId = Integer.parseInt(args[1]);
        int sourceID = Integer.parseInt(args[2]);
        int targetID = Integer.parseInt(args[3]);
        HgvClient hgv = new HgvClient(port);
        Graph g = hgv.loadGraph(graphId);
        g.bfs(sourceID, targetID, hgv);
        hgv.render();

        Object lock = new Object();
        synchronized (lock) {
            lock.wait(10000000);
        }
    }
}
