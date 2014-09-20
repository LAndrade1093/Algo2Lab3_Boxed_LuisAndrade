import java.security.InvalidParameterException;


public class Graph 
{
	private int[][] matrix; //for tracking edges
	private int[] marks; //for coloring vertices
	private int numOfEdges;
	
	public Graph(int v)
	{
		matrix = new int[v][v];
		marks = new int [v];
		numOfEdges = 0;
	}
	
	//Returns number of vertices (whether connected or not) in the graph
	public int vcount()
	{
		return marks.length;
	}
	
	//Returns the number of edges in the graph
	public int ecount()
	{
		return numOfEdges;
	}
	
	//Returns the first vertex (in natural order) connected to vertex v.  
	//If there are none, then vcount() is returned
	public int first(int v)
	{
		for(int i = 0; i < vcount(); i++)
		{
			if(matrix[v][i] != 0)
			{
				return i;
			}
		}
		return vcount();
	}
	
	//Returns the vertex (in natural order) connected to vertex v after vertex w.  
	//If there are no more edges after w, vcount() is returned
	public int next(int v, int w)
	{
		int indexOfNeighbor = vcount();
		boolean isFound = false;
		for(int i = w + 1; i < vcount() && !isFound; i++)
		{
			if(matrix[v][i] != 0)
			{
				indexOfNeighbor = i;
				isFound = true;
			}
		}
		return indexOfNeighbor;
	}
	
	//Adds an edge between vertex v and vertex w
	public void addEdge(int v, int neighbor, int weight)
	{
		if(weight != 0 && matrix[v][neighbor] == 0)
		{
			matrix[v][neighbor] = weight;
			numOfEdges++;
		}
		else if(weight == 0)
		{
			throw new InvalidParameterException("Can nnot add an edge with a weight of 0");
		}
	}
	
	//Removes edge between vertex v and vertex w
	public void removeEdge(int v, int w)
	{
		if(matrix[v][w] != 0)
		{
			matrix[v][w] = 0;
			numOfEdges--;
		}
	}

	//Returns whether there is a connection between vertex v and vertex w
	public boolean isEdge(int v, int w)
	{
		return matrix[v][w] != 0;
	}
	
	//Returns how many edges depart from vertex v
	public int degree(int v)
	{
		int number = 0;
		
		for(int i = 0; i < vcount(); i++)
		{
			if(matrix[v][i] != 0)
			{
				number++;
			}
		}
		
		return number;
	}
	
	//Returns any graph coloring for this vertex
	public int getMark(int v)
	{
		return marks[v];
	}
	
	//Colors vertex v color m
	public void setMark(int v, int m)
	{
		marks[v] = m;
	}
}
