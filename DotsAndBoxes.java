import java.util.ArrayList;
import java.util.List;


public class DotsAndBoxes 
{
	private Graph gameBoard;
	private int[] playerScores;
	private int width;
	private int height;
	private int VISITED_VERTEX = 10;;
	private int UNVISITED_VERTEX = 0;
	
	/*
	 * Helps represent the board as a Cartesian graph, where a box in 
	 * "Dots and Boxes", as well as a coin in "Strings-and-Coin", are represented by x and y values
	 * 
	 * Class stores x and y values of a box in the graph (game board)
	 */
	private class Coordinate
	{
		public int xPos;
		public int yPos;
		
		public Coordinate(int x, int y)
		{
			xPos = x;
			yPos = y;
		}
	}
	
	public DotsAndBoxes(int rows, int columns)
	{
		playerScores = new int[2];
		playerScores[0] = 0;
		playerScores[1] = 0;
		rows++;
		columns++;
		height = rows;
		width = columns;
		int totalBoxes = rows * columns;
		gameBoard = new Graph(totalBoxes);
		createBoard(rows, columns);
	}
	
	//Draws a line from (x1, y1) to (x2, y2) ((0,0) is in the upper-left corner), 
	//returning how many points were earned, if any
	public int drawLine(int player, int x1, int y1, int x2, int y2)
	{
		if(x1 == x2 && y1 == y2)
		{
			throw new IllegalArgumentException("Cannot draw a line between the same point");
		}
		
		if(!withinBoardGameRange(x1, y1, x2, y2))
		{
			throw new IllegalArgumentException("One or more points are not within the " + (width - 1) + " by " + (height - 1) + " game grid.");
		}
		
		Coordinate firstInput = new Coordinate(x1, y1);
		Coordinate secondInput = new Coordinate(x2, y2);
		Coordinate diff = calculateDifference(firstInput, secondInput);
		
		Coordinate offsetToBoxA = null;
		Coordinate offsetToBoxB = null;
		
		if(diff.yPos == 1)
		{
			offsetToBoxA = new Coordinate(0, 1);
			offsetToBoxB = new Coordinate(1, 1);
		}
		else if(diff.yPos == -1)
		{
			offsetToBoxA = new Coordinate(0, 0);
			offsetToBoxB = new Coordinate(1, 0);
		}
		else if(diff.xPos == 1)
		{
			offsetToBoxA = new Coordinate(1, 0);
			offsetToBoxB = new Coordinate(1, 1);
		}
		else if(diff.xPos == -1)
		{
			offsetToBoxA = new Coordinate(0, 0);
			offsetToBoxB = new Coordinate(0, 1);
		}
		
		int xA = x1 + offsetToBoxA.xPos;
		int yA = y1 + offsetToBoxA.yPos;
		int vertex = xA + yA * width;
		
		int xB = x1 + offsetToBoxB.xPos;
		int yB = y1 + offsetToBoxB.yPos;
		int neighbor = xB + yB * width;
		
		deleteConnection(vertex, neighbor);
		
		int points = 0;
		if(gameBoard.first(vertex) == gameBoard.vcount() && validNodeIndex(vertex))
		{
			points++;
		}
		if(gameBoard.first(neighbor) == gameBoard.vcount() && validNodeIndex(neighbor))
		{
			points++;
		}
		
		playerScores[player-1] += points;
		
		return points;
	}
	
	private Coordinate calculateDifference(Coordinate a, Coordinate b)
	{
		int x = b.xPos - a.xPos;
		int y = b.yPos - a.yPos;
		return new Coordinate(x, y);
	}
	
	
	
	// returns the score for a player
	public int score(int player)
	{
		return playerScores[player-1];
	}
	
	
	
	// returns whether or not there are any lines to be drawn
	public boolean areMovesLeft()
	{
		boolean movesLeft = false;
		for(int i = 1; i < height - 1 && !movesLeft; i++)
		{
			for(int j = 1; j < width - 1 && !movesLeft; j++)
			{
				int vertex = j + i * width;
				for(int neighbor = gameBoard.first(vertex); neighbor < gameBoard.vcount() && !movesLeft; neighbor = gameBoard.next(vertex, neighbor))
				{
					if(neighbor != gameBoard.vcount())
					{
						movesLeft = true;
					}
				}
			}
		}
		return movesLeft;
	}
	
	
	
	// returns the number of double-crosses on the board
	public int countDoubleCrosses()
	{
		DfsGraphTraversal dfs = new DfsGraphTraversal();
		List<List<Integer>> connectedNodes = dfs.traverse(gameBoard);
		
		int count = 0;
		for(int i = 0; i < connectedNodes.size(); i++)
		{
			int graphSize = connectedNodes.get(i).size();
			if(graphSize == 2)
			{
				int v = connectedNodes.get(i).get(0);
				int w = connectedNodes.get(i).get(1);
				
				if(gameBoard.isEdge(v, w) && gameBoard.isEdge(w, v))
				{
					count++;
				}
			}
		}
		clearGraphMarks();
		return count;
	}
	
	
	
	// returns the number of cycles on the board
	public int countCycles()
	{
		int count = 0;
		
		for(int i = 1; i < height-1; i++)
		{
			for(int j = 1; j < width-1; j++)
			{
				int vertex = j + i * width;
				if(isCycle(-1, 0, vertex))
				{
					count++;
				}
			}
		}
		
		clearGraphMarks();
		return count;
	}
	
	public boolean isCycle(int startingVertex, int cycleLength, int currentVertex)
	{
		boolean validCycle = false;
		if(cycleLength > 2)
		{
			if(startingVertex == currentVertex) //Wrapped around back to the first node
			{
				return true;
			}
		}
		if(countConnections(currentVertex) != 2) //Less than two neighbors
		{
			validCycle = false;
		}
		else if(gameBoard.getMark(currentVertex) == VISITED_VERTEX) //Vertex is already added to the chain
		{
			validCycle = false;
		}
		else
		{
			if(startingVertex == -1)
			{
				startingVertex = currentVertex;
			}
			gameBoard.setMark(currentVertex, VISITED_VERTEX);
			cycleLength++;
			for(int neighbor = gameBoard.first(currentVertex); neighbor < gameBoard.vcount() && !validCycle; neighbor = gameBoard.next(currentVertex, neighbor))
			{
				validCycle = isCycle(startingVertex, cycleLength, neighbor);
			}
		}
		
		return validCycle;
	}
	
	
	
	// returns the number of open chains on the board
	public int countOpenChains()
	{
		int chainCount = 0;
		
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				int vertex = j + i * width;
				List<List<Integer>> chains = getAllChains(vertex);
				for(int chainIndex = 0; chainIndex < chains.size(); chainIndex++)
				{
					if(chains.get(chainIndex).size() >= 3)
					{
						chainCount++;
					}
				}
			}
		}
		clearGraphMarks();
		return chainCount;
	}
	
	private List<List<Integer>> getAllChains(int currentVertex)
	{
		List<List<Integer>> chains = new ArrayList<List<Integer>>();
		
		if(countConnections(currentVertex) > 2 || isEdgeNode(currentVertex))
		{
			for(int neighbor = gameBoard.first(currentVertex); neighbor < gameBoard.vcount(); neighbor = gameBoard.next(currentVertex, neighbor))
			{
				if(countConnections(neighbor) == 2)
				{
					chains.add(getCurrentChain(neighbor));
				}
			}
		}
		
		return chains;
	}
	
	private List<Integer> getCurrentChain(int currentVertex)
	{
		List<Integer> chain = new ArrayList<Integer>();
		
		if(countConnections(currentVertex) != 2) //Less than two neighbors
		{
			return chain;
		}
		else if(gameBoard.getMark(currentVertex) == VISITED_VERTEX) //Vertex is already added to the chain
		{
			return chain;
		}
		else
		{
			chain.add(currentVertex);
			gameBoard.setMark(currentVertex, VISITED_VERTEX);
			for(int neighbor = gameBoard.first(currentVertex); neighbor < gameBoard.vcount(); neighbor = gameBoard.next(currentVertex, neighbor))
			{
				chain.addAll(getCurrentChain(neighbor));
			}
		}
		
		return chain;
	}
	
	
	
	
	
	private void createBoard(int numOfRows, int numOfColumns)
	{
		for(int row = 1; row < numOfRows - 1; row++)
		{
			for(int col = 1; col < numOfColumns - 1; col++)
			{
				int vertexIndex = col + row * numOfColumns;
				
				int rightNeighbor = (col+1) + row * numOfColumns;
				createConnection(vertexIndex, rightNeighbor);
				
				int leftNeighbor = (col-1) + row * numOfColumns;
				createConnection(vertexIndex, leftNeighbor);
				
				int topNeighbor = col + (row-1) * numOfColumns;
				createConnection(vertexIndex, topNeighbor);
				
				int bottomNeighbor = col + (row+1) * numOfColumns;
				createConnection(vertexIndex, bottomNeighbor);
			}
		}
	}
	
	private void createConnection(int vertexIndex, int neighborIndex)
	{
		gameBoard.addEdge(vertexIndex, neighborIndex, 100);
		gameBoard.addEdge(neighborIndex, vertexIndex, 100);
	}
	
	private void deleteConnection(int vertexIndex, int neighborIndex)
	{
		gameBoard.removeEdge(vertexIndex, neighborIndex);
		gameBoard.removeEdge(neighborIndex, vertexIndex);
	}
	
	private int countConnections(int vertex)
	{
		int count = 0;
		for(int neighbor = gameBoard.first(vertex); neighbor < gameBoard.vcount(); neighbor = gameBoard.next(vertex, neighbor))
		{
			count++;
		}
		return count;
	}
	
	private boolean withinBoardGameRange(int x1, int y1, int x2, int y2)
	{
		return (x1 >= 0 && x1 <= width - 1 && y1 >= 0 || y1 <= height - 1 && x2 >= 0 && x2 <= width - 1 && y2 >= 0 || y2 <= height - 1);
	}
	
	private boolean validNodeIndex(int index)
	{
		int x = index % width;
		int y = index / width;
		boolean isValid = (x >= 1 && x <= width - 2 && y >= 1 && y <= height - 2);
		return isValid;
	}
	
	private boolean isEdgeNode(int index)
	{
		int x = index % width;
		int y = index / width;
		boolean isValid = false;
		if( (x == 0 || x == width - 1) && (y > 0 && y < height - 2) )
		{
			isValid = true;
		}
		else if( (y == 0 || y == height - 1) && (x > 0 && x < width - 2) )
		{
			isValid = true;
		}
		else
		{
			isValid = false;
		}
		
		return isValid;
	}
	
	private void clearGraphMarks()
	{
		for(int i = 0; i < gameBoard.vcount(); i++)
		{
			gameBoard.setMark(i, UNVISITED_VERTEX);
		}
	}
	
	public String toString()
	{
		String board = "";
		for(int row = 0; row < height; row++)
		{
			String connectionsRow = "";
			for(int col = 0; col < width; col++)
			{
				if(row == 0)
				{
					if(col == 0)
					{
								 board += "#   ";
						connectionsRow += "  . ";
					}
					else if(col == width-1)
					{
								 board += "#";
						connectionsRow += " ";
					}
					else
					{
						int v = col + row * width;
						int b = col + (row+1) * width;
						
						if(gameBoard.isEdge(v, b) && gameBoard.isEdge(b, v))
						{
									 board += "#   ";
							connectionsRow += "| . ";
						}
						else
						{
									 board += "#   ";
							connectionsRow += "- . ";
						}
					}
				}
				else if(row == height - 1)
				{
					if(col >= 0)
					{
						board += "#   ";
					}
					else if(col == width-1)
					{
						board += "#";
					}
				}
				else
				{
					if(col == 0)
					{
						int v = col + row * width;
						int w = (col+1) + row * width;
						
						if(gameBoard.isEdge(v, w) && gameBoard.isEdge(w, v))
						{
									 board += "# - ";
							connectionsRow += "  . ";
						}
						else
						{
									 board += "# | ";
							connectionsRow += "  . ";
						}
					}
					else if(col == width-1)
					{
						 		  board += "#";
						 connectionsRow += " ";
					}
					else
					{
						int v = col + row * width;
						int w = (col+1) + row * width;
						int t = col + (row+1) * width;
						
						if(gameBoard.isEdge(v, w) && gameBoard.isEdge(w, v) && gameBoard.isEdge(v, t) && gameBoard.isEdge(t, v))
						{
									 board += "# - ";
							connectionsRow += "| . ";
						}
						else if(gameBoard.isEdge(v, w) && gameBoard.isEdge(w, v))
						{
									 board += "# - ";
							connectionsRow += "- . ";
						}
						else if(gameBoard.isEdge(v, t) && gameBoard.isEdge(t, v))
						{
									 board += "# | ";
							connectionsRow += "| . ";
						}
						else
						{
									 board += "# | ";
							connectionsRow += "- . ";
						}
					}
				}
			}
			board += "\n" + connectionsRow + "\n";
		}
		return board;
	}
	
}
