package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * @author Daniel Kolen
 * 
 * <P>This class represents a general "directed graph", which could 
 * be used for any purpose.  The graph is viewed as a collection 
 * of vertices, which are sometimes connected by weighted, directed
 * edges.</P> 
 * 
 * <P>This graph will never store duplicate vertices.</P>
 * 
 * <P>The weights will always be non-negative integers.</P>
 * 
 * <P>The WeightedGraph will be capable of performing three algorithms:
 * Depth-First-Search, Breadth-First-Search, and Djikatra's.</P>
 * 
 * <P>The Weighted Graph will maintain a collection of 
 * "GraphAlgorithmObservers", which will be notified during the
 * performance of the graph algorithms to update the observers
 * on how the algorithms are progressing.</P>
 */
public class WeightedGraph<V> {

	/* STUDENTS:  You decide what data structure(s) to use to
	 * implement this class.
	 * 
	 * You may use any data structures you like, and any Java 
	 * collections that we learned about this semester.  Remember 
	 * that you are implementing a weighted, directed graph.
	 */
	
	private HashMap<V, HashMap<V, Integer>> map;	//Used a HashMap with V keys that point to HashMaps as values
													//the value HashMaps have V keys (adjacent vertex) that points
													//to an Integer as the value (weight of the edge)
	
	/* Collection of observers.  Be sure to initialize this list
	 * in the constructor.  The method "addObserver" will be
	 * called to populate this collection.  Your graph algorithms 
	 * (DFS, BFS, and Dijkstra) will notify these observers to let 
	 * them know how the algorithms are progressing. 
	 */
	private Collection<GraphAlgorithmObserver<V>> observerList;
	

	/** Initialize the data structures to "empty", including
	 * the collection of GraphAlgorithmObservers (observerList).
	 */
	public WeightedGraph() {
		map = new HashMap<>();
		observerList = new ArrayList<>();		//uses an ArrayList for the observerList collection
	}

	/** Add a GraphAlgorithmObserver to the collection maintained
	 * by this graph (observerList).
	 * 
	 * @param observer
	 */
	public void addObserver(GraphAlgorithmObserver<V> observer) {
		observerList.add(observer);
	}

	/** Add a vertex to the graph.  If the vertex is already in the
	 * graph, throw an IllegalArgumentException.
	 * 
	 * @param vertex vertex to be added to the graph
	 * @throws IllegalArgumentException if the vertex is already in
	 * the graph
	 */
	public void addVertex(V vertex) {
		if(map.containsKey(vertex)) {
			throw new IllegalArgumentException();	//if vertex is in graph, throw exception
		}
		map.put(vertex, new HashMap<>());	//add vertex with no adjacent vertices
	}
	
	/** Searches for a given vertex.
	 * 
	 * @param vertex the vertex we are looking for
	 * @return true if the vertex is in the graph, false otherwise.
	 */
	public boolean containsVertex(V vertex) {
		return map.containsKey(vertex);
	}

	/** 
	 * <P>Add an edge from one vertex of the graph to another, with
	 * the weight specified.</P>
	 * 
	 * <P>The two vertices must already be present in the graph.</P>
	 * 
	 * <P>This method throws an IllegalArgumentExeption in three
	 * cases:</P>
	 * <P>1. The "from" vertex is not already in the graph.</P>
	 * <P>2. The "to" vertex is not already in the graph.</P>
	 * <P>3. The weight is less than 0.</P>
	 * 
	 * @param from the vertex the edge leads from
	 * @param to the vertex the edge leads to
	 * @param weight the (non-negative) weight of this edge
	 * @throws IllegalArgumentException when either vertex
	 * is not in the graph, or the weight is negative.
	 */
	public void addEdge(V from, V to, Integer weight) {
		if(!map.containsKey(from) || !map.containsKey(to) || weight < 0) {
			throw new IllegalArgumentException(); //if from or to is not in graph, or weight < 0 throw exception
		} 
		map.get(from).put(to, weight);		//adds a hashMap value that stores the adjacent vertex and weight
	}

	/** 
	 * <P>Returns weight of the edge connecting one vertex
	 * to another.  Returns null if the edge does not
	 * exist.</P>
	 * 
	 * <P>Throws an IllegalArgumentException if either
	 * of the vertices specified are not in the graph.</P>
	 * 
	 * @param from vertex where edge begins
	 * @param to vertex where edge terminates
	 * @return weight of the edge, or null if there is
	 * no edge connecting these vertices
	 * @throws IllegalArgumentException if either of
	 * the vertices specified are not in the graph.
	 */
	public Integer getWeight(V from, V to) {
		if(!map.containsKey(from) || !map.containsKey(to)) {
			throw new IllegalArgumentException();	//if either of the vertices are not in graph, throw exception
		}
		if(map.get(from).containsKey(to)) {
			return map.get(from).get(to);
		}
		return null;
	}

	/** 
	 * <P>This method will perform a Breadth-First-Search on the graph.
	 * The search will begin at the "start" vertex and conclude once
	 * the "end" vertex has been reached.</P>
	 * 
	 * <P>Before the search begins, this method will go through the
	 * collection of Observers, calling notifyBFSHasBegun on each
	 * one.</P>
	 * 
	 * <P>Just after a particular vertex is visited, this method will
	 * go through the collection of observers calling notifyVisit
	 * on each one (passing in the vertex being visited as the
	 * argument.)</P>
	 * 
	 * <P>After the "end" vertex has been visited, this method will
	 * go through the collection of observers calling 
	 * notifySearchIsOver on each one, after which the method 
	 * should terminate immediately, without processing further 
	 * vertices.</P> 
	 * 
	 * @param start vertex where search begins
	 * @param end the algorithm terminates just after this vertex
	 * is visited
	 */
	public void DoBFS(V start, V end) {
		for(GraphAlgorithmObserver<V> observer : observerList) {
			observer.notifyBFSHasBegun();		//notifies observers BFS began
		}
		Set<V> visited = new HashSet<V>();		//set that holds visited vertices
		Queue<V> queue = new LinkedList<V>();	//queue used to perform BFS
		queue.add(start);
		while(!queue.isEmpty()) {	
			V vertex = queue.remove();
			if(!visited.contains(vertex)) {		//if the vertex removed from queue is not visited...
				for(GraphAlgorithmObserver<V> observer : observerList) {
					observer.notifyVisit(vertex);	//notify observers vertex has been visited
				}
				if(vertex.equals(end)) {
					for(GraphAlgorithmObserver<V> observer : observerList) {
						observer.notifySearchIsOver();	//if this vertex is the end, notify observers BFS is over
					}
					return;		//if this vertex is the end, stop the BFS from running
				}
				visited.add(vertex);	//add newly visited vertex to visited set
				Set<V> adjSet = map.get(vertex).keySet();
				for(V adj : adjSet) {
					if(!visited.contains(adj)) {
						queue.add(adj);	//for all of the adjacent vertices, if they haven't been visited add to queue
					}
				}
			}
		}
		
	}
	
	/** 
	 * <P>This method will perform a Depth-First-Search on the graph.
	 * The search will begin at the "start" vertex and conclude once
	 * the "end" vertex has been reached.</P>
	 * 
	 * <P>Before the search begins, this method will go through the
	 * collection of Observers, calling notifyDFSHasBegun on each
	 * one.</P>
	 * 
	 * <P>Just after a particular vertex is visited, this method will
	 * go through the collection of observers calling notifyVisit
	 * on each one (passing in the vertex being visited as the
	 * argument.)</P>
	 * 
	 * <P>After the "end" vertex has been visited, this method will
	 * go through the collection of observers calling 
	 * notifySearchIsOver on each one, after which the method 
	 * should terminate immediately, without visiting further 
	 * vertices.</P> 
	 * 
	 * @param start vertex where search begins
	 * @param end the algorithm terminates just after this vertex
	 * is visited
	 */
	public void DoDFS(V start, V end) {
		for(GraphAlgorithmObserver<V> observer : observerList) {
			observer.notifyDFSHasBegun();		//notifies observers DFS began
		}
		Set<V> visited = new HashSet<V>();		//set that holds visited vertices
		Stack<V> stack = new Stack<V>();		//stack used to perform DFS
		stack.push(start);
		while(!stack.empty()) {
			V vertex = stack.pop();
			if(!visited.contains(vertex)) {		//if the vertex popped from stack is not visited...
				for(GraphAlgorithmObserver<V> observer : observerList) {
					observer.notifyVisit(vertex);		//notify observers vertex has been visited
				}
				if(vertex.equals(end)) {
					for(GraphAlgorithmObserver<V> observer : observerList) {
						observer.notifySearchIsOver();	//if this vertex is the end, notify observers DFS is over
					}
					return;		//if this vertex is the end, stop the DFS from running
				}
				visited.add(vertex);	//add newly visited vertex to visited set
				Set<V> adjSet = map.get(vertex).keySet();
				for(V adj : adjSet) {
					if(!visited.contains(adj)) {
						stack.push(adj);//for all of the adjacent vertices, if they haven't been visited push on stack
					}
				}
			}
		}
	}
	
	/** 
	 * <P>Perform Dijkstra's algorithm, beginning at the "start"
	 * vertex.</P>
	 * 
	 * <P>The algorithm DOES NOT terminate when the "end" vertex
	 * is reached.  It will continue until EVERY vertex in the
	 * graph has been added to the finished set.</P>
	 * 
	 * <P>Before the algorithm begins, this method goes through 
	 * the collection of Observers, calling notifyDijkstraHasBegun 
	 * on each Observer.</P>
	 * 
	 * <P>Each time a vertex is added to the "finished set", this 
	 * method goes through the collection of Observers, calling 
	 * notifyDijkstraVertexFinished on each one (passing the vertex
	 * that was just added to the finished set as the first argument,
	 * and the optimal "cost" of the path leading to that vertex as
	 * the second argument.)</P>
	 * 
	 * <P>After all of the vertices have been added to the finished
	 * set, the algorithm will calculate the "least cost" path
	 * of vertices leading from the starting vertex to the ending
	 * vertex.  Next, it will go through the collection 
	 * of observers, calling notifyDijkstraIsOver on each one, 
	 * passing in as the argument the "lowest cost" sequence of 
	 * vertices that leads from start to end (I.e. the first vertex
	 * in the list will be the "start" vertex, and the last vertex
	 * in the list will be the "end" vertex.)</P>
	 * 
	 * @param start vertex where algorithm will start
	 * @param end special vertex used as the end of the path 
	 * reported to observers via the notifyDijkstraIsOver method.
	 */
	public void DoDijsktra(V start, V end) {
		for(GraphAlgorithmObserver<V> observer : observerList) {
			observer.notifyDijkstraHasBegun();	//notifies observers Dijkstra began
		}
		Set<V> finished = new HashSet<V>();		//set that holds finished vertices
		HashMap<V,V> pred = new HashMap<>();	//maps vertices to their predecessors
		HashMap<V,Integer> cost = new HashMap<>();	//maps vertices to their costs
		for(V vertex : map.keySet()) {
			cost.put(vertex, Integer.MAX_VALUE);	//set all initial costs to infinity
		}
		cost.put(start, 0);						//start vertex always has cost of 0
		while(finished.size() != map.size()) {	//while not all vertices are in finished set...
			int smallestCost = Integer.MAX_VALUE;	//variable will hold smallest cost of the unfinished vertices
			V smallestCostVertex = null;	//variable will hold unfinished vertex with smallest cost
			for(V vertex : map.keySet()) {
				if(!finished.contains(vertex)) {
					if(cost.get(vertex) < smallestCost) {
						smallestCost = cost.get(vertex);	//loops through unfinished vertices to find vertex
						smallestCostVertex = vertex;		//with smallest cost
					}
				}
			}
			finished.add(smallestCostVertex);		//adds smallest cost vertex to finished set
			for(GraphAlgorithmObserver<V> observer : observerList) {	//notifies observers vertex has been finished
				observer.notifyDijkstraVertexFinished(smallestCostVertex, smallestCost);	
			}								
			for(V adj : map.get(smallestCostVertex).keySet()) {	//for each neighbor of vertex that is not finished
				if(!finished.contains(adj)) {
					int newCost = cost.get(smallestCostVertex) + map.get(smallestCostVertex).get(adj);
					if(newCost < cost.get(adj)) {
						cost.put(adj, newCost);	//if updated cost is smaller than original cost, update cost and pred
						pred.put(adj, smallestCostVertex);
					}
				}
			}
		}
		Stack<V> path = new Stack<>();	//after Dijkstra is finished, this variable holds the smallest cost path
		V currVertex = end;
		path.push(currVertex);
		while(!currVertex.equals(start)) {
			currVertex = pred.get(currVertex);
			path.push(currVertex);	//goes through predecessors starting with end until it reaches start
		}
		ArrayList<V> pathList = new ArrayList<>();
		while(!path.empty()) {
			pathList.add(path.pop());	//takes the path out of the stack and puts it into the list
		}
		for(GraphAlgorithmObserver<V> observer : observerList) {
			observer.notifyDijkstraIsOver(pathList);	//notifies observers Dijkstra is over
		}
	}
	
}
