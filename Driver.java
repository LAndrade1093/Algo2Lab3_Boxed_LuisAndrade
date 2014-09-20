import java.util.List;


public class Driver 
{

	public static int eWeight = 1;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//0 through 4 are in one tree
		//5 to 6 are in another, disconnected tree
		Graph g = new Graph(7);
		
		g.addEdge(0, 1, eWeight);
		g.addEdge(0, 2, eWeight);
		
		g.addEdge(1, 0, eWeight);
		g.addEdge(1, 3, eWeight);
		g.addEdge(1, 4, eWeight);
		
		g.addEdge(3, 2, eWeight);
		
		g.addEdge(5, 6, eWeight);//The separate, disconnected tree
		
		//Test for who is connected to what (directed graph)
		for(int i = 0; i < g.vcount(); i++)
		{
			System.out.print("Connected from " + i + ": ");
			if(g.first(i) == g.vcount())
			{
				System.out.print("None");
			}
			else
			{
				for(int j = g.first(i); j < g.vcount(); j = g.next(i, j))
				{
					System.out.print(j + ", ");
				}
			}
			System.out.println();
		}
		
		/*
		 * Depth-first search test_________________________________________
		 */
		System.out.println("\n__________Depth-First Search__________");
		DfsGraphTraversal dfs = new DfsGraphTraversal();
		List<List<Integer>> connectedComponents = dfs.traverse(g);
		
		for(int i = 0; i < connectedComponents.size(); i++)
		{
			System.out.println("Tree " + (i + 1) + ": ");
			List<Integer> component = connectedComponents.get(i);
			for(int j = 0; j < component.size(); j++)
			{
				System.out.println(component.get(j));
			}
			System.out.println();
		}
		
		//Clears the marks in a graph
		for(int i = 0; i < g.vcount(); i++)
		{
			g.setMark(i, 0);
		}
		
		/*
		 * Breadth-first search test_________________________________________
		 */
		System.out.println("\n__________Breadth-First Search__________");
		BfsGraphTraversal bfs = new BfsGraphTraversal();
		List<List<Integer>> bfsComponents = bfs.traverse(g);

		for(int i = 0; i < bfsComponents.size(); i++)
		{
			System.out.println("Tree " + (i + 1) + ": ");
			List<Integer> someComponent = bfsComponents.get(i);
			for(int j = 0; j < someComponent.size(); j++)
			{
				System.out.println(someComponent.get(j));
			}
			System.out.println();
		}
		
		for(int i = 0; i < g.vcount(); i++)
		{
			g.setMark(i, 0);
		}
	}
}





