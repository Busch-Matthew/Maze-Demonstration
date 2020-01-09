//This Node class is used solely for the BFS traversal of the graph
//This allows me to keep a "linked list" of previous nodes that can be traced 
//if a solution is found to generate the shortest path to the goal/exit
public class Node {
	
	int x;
	int y;
	int dist;
	Node prev;
	
	Node(int x, int y, int dist){
		this.x = x;
		this.y = y;
		this.dist = dist;
		this.prev = null;
	}
	Node(int x, int y, int dist, Node prev){
		this.x = x;
		this.y = y;
		this.dist = dist;
		this.prev = prev;
	}

}
