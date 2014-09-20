import java.util.ArrayList;
import java.util.List;

public class DfsGraphTraversal 
{
	private int VISITED_VERTEX = 1;
	private int UNVISITED_VERTEX = 0;
	private int ALL_VISITED = -1;
	
	List<List<Integer>> traverse(Graph g)
	{
		int unvisitedVertex = 0;
		List<List<Integer>> connectedComponents = new ArrayList<List<Integer>>();
		
		while(unvisitedVertex != ALL_VISITED)
		{
			List<Integer> connectedGraph = new ArrayList<Integer>();
			connectedGraph = dfsTraversalHelper(g, unvisitedVertex);
			connectedComponents.add(connectedGraph);
			unvisitedVertex = findUnvisitedVertex(g);
		}
		
		return connectedComponents;
	}
	
	private List<Integer> dfsTraversalHelper(Graph g, int vertexIndex)
	{
		g.setMark(vertexIndex, VISITED_VERTEX);
		List<Integer> vertices = new ArrayList<Integer>();
		vertices.add(vertexIndex);
		
		for(int neighbor = g.first(vertexIndex); neighbor < g.vcount(); neighbor = g.next(vertexIndex, neighbor))
		{
			if(g.getMark(neighbor) == UNVISITED_VERTEX)
			{
				vertices.addAll(dfsTraversalHelper(g, neighbor));
			}
		}
		
		return vertices;
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
