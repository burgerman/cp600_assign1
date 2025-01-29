import java.util.*;

public class NetworkRapidChange {

    private static String END_LINE = "-1 -1 -1";

    private static void getLargestEdgeWeight(MSTEntity mst) {
        int largest=Integer.MIN_VALUE;
        for (int w : mst.weights) {
            largest = w>largest? w:largest;
        }
        mst.setLargestEdge(largest);
    }

    private static int findWeightiestEdge (MSTEntity mst) {
        int pos = 0;
        int weight = Integer.MIN_VALUE;
        for (int i = 0; i < mst.weights.size(); i++) {
            if(mst.weights.get(i)>weight) {
                weight = mst.weights.get(i);
                pos = i;
            }
        }
        return pos;
    }

    private static MSTEntity buildMST(int[][] graph, int V) {
        MSTEntity mst = new MSTEntity(V);
        int edges = 0;
        int u, v;
        String uv;
        boolean[] visited = new boolean[V];
        visited[0] = true;
        for (int i = 1; i < V; i++) {
            visited[i] = false;
        }
        System.out.println("MST Edges:");
        while (edges < V-1) {
            u =0;
            v=0;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < V; i++) {
                if(visited[i] == true) {
                    for(int j = 0; j < V; j++) {
                        if(i==j) continue;
                        if(graph[i][j]!=-1 && visited[j] == false) {
                            if(graph[i][j] < min) {
                                u = i;
                                v = j;
                                min = graph[u][v];
                            }
                        }
                    }
                }
            }
            visited[v] = true;
//            u1=u+1;
//            v1=v+1;
//            uv1 = u1+"-"+v1;
//            System.out.println("edge: "+uv1+"; weight: "+graph[u][v]);
            if(v<u) {
                int tmp = u;
                u = v;
                v = tmp;
            }
            uv = u+"-"+v;
            mst.tree.put(uv, graph[u][v]);
            mst.edges.add(uv);
            mst.weights.add(graph[u][v]);
            edges++;
        }
        getLargestEdgeWeight(mst);
        return mst;
    }


    private static void printMST(MSTEntity mst) {
        int V = mst.V;
        for (int i = 0; i < V-1; i++) {
            String[] edge = mst.edges.get(i).split("-");
            int u = Integer.parseInt(edge[0]);
            int v = Integer.parseInt(edge[1]);
            int u1=u+1;
            int v1=v+1;
            String uv1 = u1+"-"+v1;
            System.out.println("edge: "+uv1+"; weight: "+mst.weights.get(i));
        }
    }


    private static void dfs(int[][] graph, int n1, int n2, boolean[] visited, List<Integer> connectedNodes, MSTEntity mst) {
        visited[n1] = true;
        connectedNodes.add(n1);
        String edge;
        int u, v;
        for (int neighbor=0; neighbor< graph.length; neighbor++) {
            if(neighbor<n1) {
                u = neighbor;
                v = n1;
            } else {
                u=n1;
                v=neighbor;
            }
            edge = u+"-"+v;
            // assume n1 and n2 disconnected
            // find connected neighbor nodes in the same component of MST
            if(neighbor!=n1 && neighbor!=n2 && !visited[neighbor] && graph[n1][neighbor]!=-1 && mst.edges.contains(edge)) {
                dfs(graph, neighbor, n2, visited, connectedNodes, mst);
            }
        }
    }

    /**
     *
     * In the case where the updated weight of u-v as part of MST outweigh than before
     * We check if u-v should be removed from MST using DFS to search a possible replacement.
     * Check logic: assume if removing u-v from MST, what better choice can be used to connect two component of MST
     * Get two split components by using DFS to find one component of visited nodes without edge u-v
     * and filter out the rest of unvisited nodes as the other one
     * Next search a new edge with min weight that can connect these two split components
     * compare current weight of MST to the new weight of MST if adopting the found edge
     * if new weight smaller than before, it suggests a better choice exists, then replace u-v with found one
     * if not, keep the MST the same as before
     */
    private static MSTEntity checkAndAdjustMST(int[][] graph, MSTEntity mst, int u, int v, boolean inMST) {
        // edge to be replaced
        String edge = u+"-"+v;
        int currentWeight = mst.getMSTWeight();
        if(inMST) {
            int pos = mst.edges.indexOf(edge);
            if(pos==-1) return mst;
            boolean[] visited = new boolean[mst.V];
            List<Integer> connectedSet1 = new ArrayList<>();
            dfs(graph, u, v, visited, connectedSet1, mst);

            List<Integer> connectedSet2 = new ArrayList<>();
            for (int i = 0; i < visited.length; i++) {
                if(!visited[i]) connectedSet2.add(i);
            }
            String newMSTEdge = edge;
            int weight = Integer.MAX_VALUE;
            for (int i = 0; i < connectedSet1.size(); i++) {
                for(int j = 0; j < connectedSet2.size(); j++) {
                    int u1 = connectedSet1.get(i);
                    int v1 = connectedSet2.get(j);
                    if(v1<u1) {
                        int tmp = u1;
                        u1 = v1;
                        v1 = tmp;
                    }
                    String tmpEdge = u1+"-"+v1;
                    if(tmpEdge.equals(edge) || mst.edges.contains(tmpEdge) || graph[u1][v1]==-1) continue;
                    if(graph[u1][v1]<weight) {
                        weight = graph[u1][v1];
                        newMSTEdge = tmpEdge;
                    }
                }
            }
            int newWeight = weight;
            for (int i = 0; i < mst.weights.size(); i++) {
                if(i!=pos) {
                    newWeight+=mst.weights.get(i);
                }
            }
            if(newWeight<currentWeight) {
                mst.edges.set(pos, newMSTEdge);
                mst.weights.set(pos, weight);
                printMST(mst);
                getLargestEdgeWeight(mst);
            } else {
                System.out.println("MST not changed!");
            }
        } else {
            UnionFind unionFind = new UnionFind(mst.V);
            for (String e : mst.edges) {
                String[] uv = e.split("-");
                int u1 = Integer.parseInt(uv[0]);
                int v1 = Integer.parseInt(uv[1]);
                unionFind.union(u1, v1);
            }
            // check a cycle
            if(!unionFind.union(u,v)) {
                int weight = graph[u][v];
                if(weight<mst.largestEdge) {
                    int pos = findWeightiestEdge(mst);
                    mst.edges.set(pos, edge);
                    mst.weights.set(pos, weight);
                    getLargestEdgeWeight(mst);
                }
            }
        }
        return mst;
    }

    private static void testCycleCheck() {
        int V = 4;
        UnionFind unionFind = new UnionFind(V);
        List<String> edges = new ArrayList<>();
        edges.add("0-2");
        edges.add("0-3");
        edges.add("1-3");
        for (String e : edges) {
            String[] uv = e.split("-");
            int u1 = Integer.parseInt(uv[0]);
            int v1 = Integer.parseInt(uv[1]);
            unionFind.union(u1, v1);
        }
        int u = 2;
        int v = 3;
        if(!unionFind.union(u,v)) {
            System.out.println("Cycle found!");
        } else {
            System.out.println("No Cycle Found!");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int i =0;
        MSTEntity mst=null;
        int weight, currentWeight;
        String edge;
        int w;
        int V=0;
        int u, v;
        int[][] arr = new int[1][1];
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if(input.equals(END_LINE)) {
                break;
            } else {
                if(input.matches("^\\d+$")) {
                    V = Integer.parseInt(input);
                    arr = new int[V][V];
                } else if(input.length()>1) {
                    String[] matrix = input.split("\\s");
                    if(matrix.length==V) {
                        for (int j = 0; j < matrix.length; j++) {
                            arr[i][j] = Integer.parseInt(matrix[j]);
                        }
                        if(i==V-1) {
                            mst = buildMST(arr, V);
                            printMST(mst);
                            weight = mst.getMSTWeight();
                            System.out.println("The total weight of the MST: "+weight);
                        }
                        i++;
                    } else if(matrix.length==3 && mst!=null) {
                        // vertex in the matrix starts from 0
                        u = Integer.parseInt(matrix[0])-1;
                        v = Integer.parseInt(matrix[1])-1;
                        if(v<u) {
                            int tmp = u;
                            u = v;
                            v = tmp;
                        }
                        w = Integer.parseInt(matrix[2]);
                        edge = u+"-"+v;
                        arr[u][v] = w;
                        arr[v][u] = w;
                        if(mst.edges.contains(edge)) {
                            int pos = mst.edges.indexOf(edge);
                            // (u,v) edge in MST
                            if(w<mst.weights.get(pos)) {
                                mst.tree.put(edge, w);
                                mst.weights.set(pos, w);
                                printMST(mst);
                                getLargestEdgeWeight(mst);
                                weight = mst.getMSTWeight();
                                System.out.println("MST weight changes to "+weight);
                            } else if(w==mst.tree.get(edge)) {
                                System.out.println("MST weight does not change");
                            } else {
                                mst.weights.set(pos, w);
                                currentWeight = mst.getMSTWeight();
                                mst = checkAndAdjustMST(arr, mst, u, v, true);
                                weight = mst.getMSTWeight();
                                if(currentWeight==weight) {
                                    System.out.println("MST weight does not change");
                                } else{
                                    System.out.println("MST weight changes to "+weight);
                                }
                            }

                        } else {
                            // (u,v) edge not in MST
                            if (w>=mst.largestEdge) {
                                System.out.println("MST weight does not change");
                            } else if(w<mst.largestEdge && w!=-1) {
                                currentWeight = mst.getMSTWeight();
                                mst = checkAndAdjustMST(arr, mst, u, v, false);
                                weight = mst.getMSTWeight();
                                if(currentWeight==weight) System.out.println("MST weight does not change");
                                else {
                                    System.out.println("MST weight changes to "+weight);
                                    printMST(mst);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static class MSTEntity {
        int V;
        List<String> edges;
        List<Integer> weights;
        Map<String,Integer> tree;
        int largestEdge;
        public MSTEntity(int V) {
            this.V = V;
            this.tree = new HashMap<>(V-1, 1.0f);
            this.edges = new LinkedList<>();
            this.weights = new LinkedList<>();
        }

        public void setLargestEdge(int largestEdge) {
            this.largestEdge = largestEdge;
        }

        private int getMSTWeight() {
            int weight = 0;
            if(this.weights!=null) {
                for (int w : this.weights) {
                    weight+=w;
                }
            }
            return weight;
        }
    }

    static class UnionFind {
        int[] parent;
        int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int u) {
            if (parent[u] != u) {
                parent[u] = find(parent[u]);
            }
            return parent[u];
        }

        public boolean union(int u, int v) {
            int rootU = find(u);
            int rootV = find(v);
            // Found a cycle
            if (rootU == rootV) return false;

            int rankU = rank[rootU];
            int rankV = rank[rootV];
            if (rankU > rankV) {
                parent[rootV] = rootU;
            } else if (rankU < rankV) {
                parent[rootU] = rootV;
            } else {
                parent[rootV] = rootU;
                rank[rootU]++;
            }
            return true;
        }
    }
}