import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class BfsGraphTraversal 
{
	private int VISITED_VERTEX = 1;
	private int UNVISITED_VERTEX = 0;
	private int ALL_VISITED = -1;
	
	List<List<Integer>> traverse(Graph g)
	{
		int startVertex = 0;
		List<List<Integer>> connectedComponents = new ArrayList<List<Integer>>();
		
		while(startVertex != ALL_VISITED)
		{
			PriorityQueue<Integer> queue = new PriorityQueue<Integer>(g.vcount());
			queue.add(startVertex);
			
			List<Integer> connectedGraph = new ArrayList<Integer>();
			connectedGraph.add(startVertex);
			
			g.setMark(startVertex, VISITED_VERTEX);
			
			while(queue.size() > 0)
			{
				int vertex = queue.poll();
				for(int neighbor = g.first(vertex); neighbor < g.vcount(); neighbor = g.next(vertex, neighbor))
				{
					if(g.getMark(neighbor) == UNVISITED_VERTEX)
					{
						g.setMark(neighbor, VISITED_VERTEX);
						queue.add(neighbor);
						connectedGraph.add(neighbor);
					}
				}
			}
			
			connectedComponents.add(connectedGraph);
			startVertex = findUnvisitedVertex(g);
		}
		
		return connectedComponents;
	}
	
	private int findUnvisitedVertex(Graph g)
	{
		boolean isFound = false;
		int currentCount = 0;
		int unvisitedIndex = ALL_VISITED;
		while(currentCount < g.vcount() && !isFound)
		{
			if(g.getMark(currentCount) == UNVISITED_VERTEX)
			{
				isFound = true;
				unvisitedIndex = currentCount;
			}
			currentCount++;
		}
		return unvisitedIndex;
	}
}
