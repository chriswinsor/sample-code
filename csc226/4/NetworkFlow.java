/* NetworkFlow.java
   CSC 226 - Fall 2014
   Assignment 4 - Max. Flow Template
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java NetworkFlow
	
   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
	java NetworkFlow file.txt
   where file.txt is replaced by the name of the text file.
   
   The input consists of a series of directed graphs in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry A[i][j] of the adjacency matrix gives the capacity of the edge from 
   vertex i to vertex j (if A[i][j] is 0, then the edge does not exist).
   For network flow computation, the 'source' vertex will always be vertex 0 
   and the 'sink' vertex will always be vertex 1.
	
   An input file can contain an unlimited number of graphs; each will be 
   processed separately.


   B. Bird - 07/05/2014
*/

import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.util.LinkedList;
import java.io.File;

//Do not change the name of the NetworkFlow class
public class NetworkFlow{

	/* MaxFlow(G)
	   Given an adjacency matrix describing the structure of a graph and the 
	   capacities of its edges, return a matrix containing a maximum flow from
	   vertex 0 to vertex 1 of G.
	   In the returned matrix, the value of entry i,j should be the total flow
	   across the edge (i,j).
	*/
	static int[][] MaxFlow(int[][] G){
		int numVerts = G.length;
		int[][] Gf = copy(G);//residual network
		int[][] flow = new int[numVerts][numVerts];//holds the flow
		int[] path = augmentPath(Gf);//holds the current augmented path
		
		//adds the current augmented path while there is still a path
		while(path[1] !=-1){
			int min = minCapacity(Gf,path);//holds the capacity of the minimum capacity edge
			Gf = update(Gf,path,min);//updates the residual network
			flow = addFlow(flow,path,min);//adds to the flow network
			path = augmentPath(Gf);//finds the next augmented path
		}
		
		//adjusts flow to remove the flow that was added at one point and removed at a later point
		for (int i = 0; i < numVerts; i++){
			for (int j = 0; j < numVerts; j++){
				if(flow[i][j] > G[i][j]){
					int temp = flow[i][j]-G[i][j];
					flow[i][j] -= temp;
					flow[j][i] -= temp;
				}
			}
		}
		
		return flow;
		
	}
	
	//update takes in the residual network and the augmented path and returns an updated residual network
	public static int[][] update(int[][] G, int[] path, int min){
		int n = G.length;
		
		for (int i = 0; i < n; i++){
			if(path[i] != -1){
				G[path[i]][i] -= min;//reduces the capacity of the edge in the path
				G[i][path[i]] += min;//adds to the capacity of the opposite edge
			}
		}
		return G;
	}
	
	//adds min to G along path
	public static int[][] addFlow(int[][] G, int[] path, int min){
		int n = path.length;
		
		//for (int i = 0; i < n; i++){
		int j = 1;
		while(j != 0){
			G[path[j]][j] += min;
			j = path[j];
		}
		return G;
	}
	
	//finds the augmented path in G and returns the path
	public static int[] augmentPath(int[][] G){
		int n = G.length;
		int[] path = new int[n];//holds the augmented path
		int[] parent = new int[n];//holds the BFS tree
		boolean[] seen = new boolean[n];//holds whether vertex "i" has been seen in the BFS
		LinkedList<Integer> Q = new LinkedList<Integer>();//The queue for BFS
		
		//initialize path and parent
		for (int i = 0; i < n; i++){
			path[i] = -1;
			parent[i] = -1;
		}
		
		//BFS
		seen[0] = true;
		parent[0] = 0;
		Q.addFirst(0);
		while(!Q.isEmpty()){
			int v = Q.removeFirst();
			if(v==1){
				break;
			}
			for(int i = 0; i < n; i++){
				if(G[v][i]!=0 && seen[i] == false){
					Q.addFirst(i);
					seen[i] = true;
					parent[i] = v;
				}
			}
		}
		
		if(parent[1] == -1)return parent;//stops now if no path from 0 to 1 exists 
		
		//makes path to be the BFS path from 0 to 1
		int j = 1;
		while(j != 0){
			path[j] = parent[j];
			j = parent[j];
		}
		
		return path;
	}
	
	//returns the minimum capacity of path
	public static int minCapacity(int[][] G, int[] path){
		int n = G.length;
		int min = Integer.MAX_VALUE;
		
		for(int i = 0; i < n; i++){
			if(path[i] != -1 && G[path[i]][i] < min){
				min = G[path[i]][i];
			}
		}
		return min;
	}
	
	public static int[][] copy(int[][] G){
		int n = G.length;
		int[][] temp = new int[n][n];
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				temp[i][j] = G[i][j];
			}
		}
		return temp;
	}
	
	public static boolean verifyFlow(int[][] G, int[][] flow){
		
		int n = G.length;
		
		//Test that the flow on each edge is less than its capacity.
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if (flow[i][j] < 0 || flow[i][j] > G[i][j]){
					System.err.printf("ERROR: Flow from vertex %d to %d is out of bounds.\n",i,j);
					System.out.println(flow[i][j] + "," + G[i][j]);
					System.out.println(flow[j][i] + "," + G[j][i]);
					return false;
				}
			}
		}
		
		//Test that flow is conserved.
		int sourceOutput = 0;
		int sinkInput = 0;
		for (int j = 0; j < n; j++)
			sourceOutput += flow[0][j];
		for (int i = 0; i < n; i++)
			sinkInput += flow[i][1];
		
		if (sourceOutput != sinkInput){
			System.err.printf("ERROR: Flow leaving vertex 0 (%d) does not match flow entering vertex 1 (%d).\n",sourceOutput,sinkInput);
			return false;
		}
		
		for (int i = 2; i < n; i++){
			int totalIn = 0, totalOut = 0;
			for (int j = 0; j < n; j++){
				totalIn += flow[j][i];
				totalOut += flow[i][j];
			}
			if (totalOut != totalIn){
				System.err.printf("ERROR: Flow is not conserved for vertex %d (input = %d, output = %d).\n",i,totalIn,totalOut);
				return false;
			}
		}
		return true;
	}
	
	public static int totalFlowValue(int[][] flow){
		int n = flow.length;
		int sourceOutput = 0;
		for (int j = 0; j < n; j++)
			sourceOutput += flow[0][j];
		return sourceOutput;
	}
	
	/* main()
	   Contains code to test the MaxFlow function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		int graphNum = 0;
		double totalTimeSeconds = 0;
		
		//Read graphs until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading graph %d\n",graphNum);
			int n = s.nextInt();
			int[][] G = new int[n][n];
			int valuesRead = 0;
			for (int i = 0; i < n && s.hasNextInt(); i++){
				for (int j = 0; j < n && s.hasNextInt(); j++){
					G[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < n*n){
				System.out.printf("Adjacency matrix for graph %d contains too few values.\n",graphNum);
				break;
			}
			long startTime = System.currentTimeMillis();
			
			int[][] G2 = new int[n][n];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					G2[i][j] = G[i][j];
			int[][] flow = MaxFlow(G2);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;
			
			if (flow == null || !verifyFlow(G,flow)){
				System.out.printf("Graph %d: Flow is invalid.\n",graphNum);
			}else{
				int value = totalFlowValue(flow);
				System.out.printf("Graph %d: Max Flow Value is %d\n",graphNum,value);
			}
				
		}
		graphNum--;
		System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
	}
}
