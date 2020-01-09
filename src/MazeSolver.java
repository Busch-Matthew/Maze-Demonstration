import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Queue;
import java.util.ArrayDeque;

public class MazeSolver extends JFrame implements MouseListener, ActionListener, ItemListener{
	//creating a constant for the size of the maze
	//other than the template mazes, nothing is hard-coded and 
	//can all scale with the maze if the size were increased
	private final int ROWS = 20;
	private final int COLS = 20;

	private JPanel mainScreen = new JPanel(new BorderLayout());
	
	//user-IO of the maze (pixels they can click on)
	private JPanel maze = new JPanel(new GridLayout(ROWS,COLS,0,0));
	private JPanel[][] pixelArray = new JPanel[ROWS][COLS];
	//variables to use when solving the maze
	//not all are used at once, but all are used at some point in different methods
	private int mazeMap[][] = new int[ROWS][COLS];
	private boolean[][] visited = new boolean[ROWS][COLS];
	private boolean[][] solution = new boolean[ROWS][COLS];
	static private int recursiveCounter = 0;
	
	//This block creates the user-control panel, allowing them to interface with the program
	//NOT INCLUDING setting blocks on the maze
	private JPanel controlPanel = new JPanel(new GridLayout(3,1,1,1));
		//top third of the control panel
		private JPanel rightTop = new JPanel();
			JLabel titleOne = new JLabel();
			JLabel titleTwo = new JLabel();
			JLabel titleThree = new JLabel();
			//middle third of the control panel
		private JPanel rightMiddle = new JPanel();
			JPanel comboLabelPanel = new JPanel();
				JLabel timeRun = new JLabel();
				JLabel numOfSteps = new JLabel();
				JLabel comboLabel = new JLabel();
			JPanel comboBoxPanel = new JPanel();
				String[] searchAlgorithms = {"Choose a Search Method", "Random", "DFS", "BFS"};
				JComboBox algorithmBox = new JComboBox(searchAlgorithms);
				String selectedAlgorithm;
			JPanel buttonsPanel = new JPanel(new GridLayout(2,2,1,1));
				private JButton solve = new JButton("Solve");
				private JButton clear = new JButton("Clear");
				private JButton randomGenerator = new JButton("Random");
				private JButton export = new JButton("Export");
			//bottom third of the control panel
		private JPanel rightBottom = new JPanel();
			JLabel rbtextOne = new JLabel();
			JTextArea instructions = new JTextArea();
			
	
	
	//definning the start of the maze
	int startingRow = 1;
	int startingCol = 0;
	//defining the end of the maze
	int endingRow = ROWS - 2;
	int endingCol = COLS - 1;
	
	//constructor
	MazeSolver(){
		super("Maze Solver");
		add(mainScreen);
		//adding EVERYTHING to the controlpanel and setting the size of it
		mainScreen.add(controlPanel, BorderLayout.EAST);
			controlPanel.setPreferredSize(new Dimension(300 ,800));
			controlPanel.setLayout(new GridLayout(3,1,1,1));
			controlPanel.setBackground(Color.WHITE);
			controlPanel.add(rightTop);
				rightTop.setBackground(Color.WHITE);
				rightTop.setLayout(new FlowLayout());
				rightTop.add(titleOne);
					titleOne.setText("Welcome");
					titleOne.setFont(new Font("Arial", Font.ITALIC, 45));
				rightTop.add(titleTwo);
					titleTwo.setText("to the");
					titleTwo.setFont(new Font("Arial", Font.ITALIC, 45));
				rightTop.add(titleThree);
					titleThree.setText("Maze Solver!");
					titleThree.setFont(new Font("Arial", Font.ITALIC, 45));
			controlPanel.add(rightMiddle);
				rightMiddle.setBackground(Color.WHITE);
				rightMiddle.setLayout(new BorderLayout());
				rightMiddle.add(comboLabelPanel, BorderLayout.NORTH);
					comboLabelPanel.setLayout(new GridLayout(3,1,0,0));
					comboLabelPanel.setBackground(Color.WHITE);
					//comboLabelPanel.setPreferredSize(new Dimension(300,100));
					comboLabelPanel.add(timeRun);
						timeRun.setText("Calculated In: ");
						timeRun.setFont(new Font("Arial", Font.BOLD, 25));
					comboLabelPanel.add(numOfSteps);
						numOfSteps.setText("Steps to Goal: ");
						numOfSteps.setFont(new Font("Arial", Font.BOLD, 25));
					comboLabelPanel.add(comboLabel);
						comboLabel.setText("Path-finding method:");
						comboLabel.setFont(new Font("Arial", Font.BOLD, 25));
				rightMiddle.add(comboBoxPanel, BorderLayout.CENTER);
					comboBoxPanel.setLayout(new FlowLayout());
					comboBoxPanel.setBackground(Color.WHITE);
					comboBoxPanel.add(algorithmBox);
						algorithmBox.addItemListener(this);
				rightMiddle.add(buttonsPanel,BorderLayout.SOUTH);
					buttonsPanel.setBackground(Color.WHITE);
					buttonsPanel.add(randomGenerator);
						randomGenerator.addActionListener(this);
					buttonsPanel.add(clear);
						clear.addActionListener(this);
					buttonsPanel.add(solve);
						solve.addActionListener(this);
					buttonsPanel.add(export);
						export.addActionListener(this);
			controlPanel.add(rightBottom);
			rightBottom.setBackground(Color.WHITE);
			rightBottom.add(rbtextOne);
				rbtextOne.setText("Instructions: ");
				rbtextOne.setFont(new Font("Arial", Font.BOLD, 18));
			rightBottom.add(instructions);
				instructions.setText("1: Either create your own maze\n by clicking in "
						+ "the field on the left or\n click 'Random' to generate a random maze\n" + 
						"2: Choose a path-finding method \n from the drop down menu\n" + 
						"3:Click 'Solve'\n____________________________\n"+
						"If you would like to export the maze,\n   press 'Export'\n" +
						"Cicking the 'Clear' button will erase all contents\n   of the maze");
								
		
		//adding on the MAZE I/O system for the user to click
		//sets walls + starting and end points to specific colors
		//   -all other spaces are white for "Open path"
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++) {
				pixelArray[i][j] = new JPanel();
				pixelArray[i][j].setBackground(Color.WHITE);
				if(i == 0 || j == 0 || i == ROWS-1 || j == COLS-1) {
					pixelArray[i][j].setBackground(Color.BLACK);
				}
				pixelArray[i][j].addMouseListener(this);
				visited[i][j] = false;
				solution[i][j] = false;
				maze.add(pixelArray[i][j]);
			}
		}
		
		pixelArray[1][0].setBackground(Color.YELLOW);
		pixelArray[ROWS-2][COLS-1].setBackground(Color.GREEN);
		
		mainScreen.add(maze, BorderLayout.CENTER);
		
			maze.setPreferredSize(new Dimension(800,800));
			maze.setBackground(Color.WHITE);
			
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100,800);
		
	}
	//this function current state of the USER I/O as a 2D array of ints
	//that represent what the tile is
	public int[][] getMap() {
		int map[][] = new int[ROWS][COLS];
		Color pixelColor;
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++) {
				pixelColor = pixelArray[i][j].getBackground();
				if(pixelColor == Color.yellow.brighter()) {
					map[i][j] = 0;
				}
				else if(pixelColor == Color.WHITE) {
					map[i][j] = 1;
				}
				else if (pixelColor == Color.BLACK || pixelColor == Color.GRAY) {
					map[i][j] = 2;
				}
				else if(pixelColor == Color.GREEN) {
					map[i][j] = 3;
				}
			}
		}
		return map;
		
	}
	//this function exports the current maze to the console as a 2D array of ints
	public void logMaze() {
		mazeMap = getMap();
		String maze;
		for(int i = 0; i< ROWS; i++) {
			maze = "{";
			for(int j = 0; j < COLS; j++) {
				maze = maze + mazeMap[i][j];
				if( j < COLS-1) {
					maze = maze + ",";
				}
			}
			System.out.println(maze + "}");
		}
	}
	//this function generates a random number and pulls the corresponding maze from the RandomMazes class
	//it DOES NOT use an algorithm to generate mazes
	public void generateRandomMaze() {
			clearMaze();
			int randomNumber = (int)(Math.random()*3) + 1;
			int [][] map;
			if (randomNumber == 1) {
				map = RandomMazes.randomMazeOne();
			}
			else if(randomNumber == 2) {
				map = RandomMazes.randomMazeTwo();
			}
			else {
				map = RandomMazes.randomMazeThree();
			}
			
			for(int i = 0; i < ROWS; i ++) {
				for(int j = 0; j < COLS ; j++) {
					if (map[i][j] == 0) {
						pixelArray[i][j].setBackground(Color.yellow.brighter());
					}
					else if(map[i][j] == 2) {
						pixelArray[i][j].setBackground(Color.BLACK);
					}
					else if(map[i][j] == 3) {
						pixelArray[i][j].setBackground(Color.GREEN);
					}
					else {
						pixelArray[i][j].setBackground(Color.WHITE);
					}
					
				}
			}
			
	}
	//this function resets the current solution to the maze but DOES NOT clear the walls
	public void resetMaze() {
		for(int i = 0; i < ROWS; i ++) {
			for(int j = 0; j <COLS; j++) {
				visited[i][j] = false;
				solution[i][j] = false;
				
				if((pixelArray[i][j].getBackground() == Color.YELLOW) || (pixelArray[i][j].getBackground() == Color.RED)) {
					pixelArray[i][j].setBackground(Color.WHITE);
				}
			}
		}
		pixelArray[1][0].setBackground(Color.YELLOW);
		pixelArray[ROWS-2][COLS-1].setBackground(Color.GREEN);
	}
	//this function reset the maze to a BLANK state
	public void clearMaze() {
		for(int i = 0; i < ROWS; i++){
			for(int j = 0; j < COLS; j++) {
				pixelArray[i][j].setBackground(Color.WHITE);
				if(i == 0 || j == 0 || i == ROWS-1 || j == COLS-1) {
					pixelArray[i][j].setBackground(Color.BLACK);
				}
				
				visited[i][j] = false;
				solution[i][j] = false;
				
			}
		}
		pixelArray[1][0].setBackground(Color.YELLOW.brighter());
		pixelArray[ROWS-2][COLS-1].setBackground(Color.GREEN);
	}
	//this function uses a basic Depth-First Search to Solve the maze
	//returns nothing
	public void DFS() {
		recursiveCounter = 0;
		resetMaze();
		boolean hasSolution = recursiveHelper(startingRow, startingCol);
		if(hasSolution) {
			
			numOfSteps.setText("Steps to Goal: " + recursiveCounter);
		}
		else {
			numOfSteps.setText("NO PATH TO GOAL");
		}
		for(int i = 0; i < ROWS; i ++) {
			for(int j = 0; j < COLS; j++) { 
				if(solution[i][j] == true){
					pixelArray[i][j].setBackground(Color.YELLOW);
				}
			}
		}
	}
	//this function is a recursive Helper for DFS
	private boolean recursiveHelper(int x, int y) {
		if (x == endingRow && y == endingCol) return true; // If you reached the end
	    if (mazeMap[x][y] == 2 || visited[x][y]) return false;  
	    // If you are on a wall or already were here
	    visited[x][y] = true;
	  
	    if (x != COLS - 1) // Checks if not on right edge
	        if (recursiveHelper(x+1, y)) { // Recalls method one to the right
	            solution[x][y] = true;
	            pixelArray[x][y].setBackground(Color.YELLOW);
	            recursiveCounter++;
	            return true;
	        }
	    if (y != ROWS - 1) // Checks if not on bottom edge
	        if (recursiveHelper(x, y+1)) { // Recalls method one down
	            solution[x][y] = true;
	            pixelArray[x][y].setBackground(Color.YELLOW);
	            recursiveCounter++;
	            return true;
	        }
	    if (x != 0) // Checks if not on left edge
	        if (recursiveHelper(x-1, y)) { // Recalls method one to the left
	            solution[x][y] = true; // Sets that path value to true;
	            pixelArray[x][y].setBackground(Color.YELLOW);
	            recursiveCounter++;
	            return true;
	        }
	    if (y != 0)  // Checks if not on top edge
	        if (recursiveHelper(x, y-1)) { // Recalls method one up
	            solution[x][y] = true;
	            pixelArray[x][y].setBackground(Color.YELLOW);
	            recursiveCounter++;
	            return true;
	        }
	    pixelArray[x][y].setBackground(Color.RED);
	    return false;
	}
	//this funciton uses a basic Breadth first search to solve the maze
	public boolean BFS() {
		mazeMap = getMap();
		
		int currentRow = startingRow;
		int currentCol = startingCol;
		int currentDist = 0;
		boolean hasSolution = false;
		
		Queue<Node> queue = new ArrayDeque<>();
		
		visited[startingRow][startingCol] = true;
		
		queue.add(new Node(currentRow, currentCol, currentDist));
		
		while(!queue.isEmpty()) {
			
			Node point = queue.poll();
			
			currentRow = point.x;
			currentCol = point.y;
			currentDist = point.dist;
			
			if((currentRow == endingRow) && (currentCol == endingCol)){
				numOfSteps.setText("Steps to Goal: " + point.dist);
				
				do {
					pixelArray[point.x][point.y].setBackground(Color.YELLOW);
					point = point.prev;
				} while(point.prev !=null);
				
				hasSolution = true;
				
				break;
			}
			
			if (currentCol != COLS - 1) // Checks if not on right edge
		        if ((mazeMap[currentRow][currentCol+1]) != 2 && (visited[currentRow][currentCol+1] != true)){
		            queue.add(new Node(currentRow, currentCol +1, currentDist+1, point));
		            visited[currentRow][currentCol+1] = true;
		            pixelArray[currentRow][currentCol+1].setBackground(Color.RED);		            
		        }
		    if (currentRow != ROWS - 1) // Checks if not on bottom edge
		    	if ((mazeMap[currentRow+1][currentCol]) != 2 && (visited[currentRow+1][currentCol] != true)){
			        queue.add(new Node(currentRow+1, currentCol, currentDist+1, point));
			        visited[currentRow+1][currentCol] = true;
			        pixelArray[currentRow+1][currentCol].setBackground(Color.RED);			            
			    }
		    if (currentCol != 0) // Checks if not on left edge
		    	if ((mazeMap[currentRow][currentCol-1]) != 2 && (visited[currentRow][currentCol-1] != true)){
			        queue.add(new Node(currentRow, currentCol-1, currentDist+1, point));
			        visited[currentRow][currentCol-1] = true;
			        	pixelArray[currentRow][currentCol-1].setBackground(Color.RED);			            
			    }
		    if (currentRow != 0)  // Checks if not on top edge
		    	if ((mazeMap[currentRow-1][currentCol]) != 2 && (visited[currentRow-1][currentCol] != true)){
		            queue.add(new Node(currentRow-1, currentCol, currentDist+1, point));
		            visited[currentRow-1][currentCol] = true;
		            pixelArray[currentRow-1][currentCol].setBackground(Color.RED);
		    }
		    
		}
		if(hasSolution == false) {
			numOfSteps.setText("NO PATH TO GOAL");
		}
		return hasSolution;
	}
	//checks ig the path exists without giving user feedback (used for random)
	public boolean doesPathExist(int x, int y) {
		if (x == endingRow && y == endingCol) return true; 
	    if (mazeMap[x][y] == 2 || visited[x][y]) return false;  
	    
	    visited[x][y] = true;
	  
	    if (x != COLS - 1) 
	        if (doesPathExist(x+1, y)) { 
	            solution[x][y] = true;
	            return true;
	        }
	    if (y != ROWS - 1) 
	        if (doesPathExist(x, y+1)) { 
	            solution[x][y] = true;
	            return true;
	        }
	    if (x != 0) 
	        if (doesPathExist(x-1, y)) {
	            solution[x][y] = true; 
	            return true;
	        }
	    if (y != 0)  
	        if (doesPathExist(x, y-1)) { 
	            solution[x][y] = true;
	            return true;
	        }
	    return false;
	}
	//randomly walks about the maze until an exit is found IF thepath Exists
	public void Random() {
			
		if(doesPathExist(startingRow, startingCol)) {
			
			for(int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					solution[i][j] = false;
					visited[i][j] = false;
				}
			}
			
			int currentRow = startingRow;
			int currentCol = startingCol;
			int roll;
			int counter=0;
			
			while(currentRow != endingRow || currentCol != endingCol) {

				roll = (int)(Math.random() * 4) + 1;
					if(roll == 1) {
						if((currentRow != ROWS-1) && (mazeMap[currentRow+1][currentCol]!= 2)) {
						//	pixelArray[currentRow][currentCol].setBackground(Color.WHITE);
							currentRow ++;
							pixelArray[currentRow][currentCol].setBackground(Color.YELLOW);							
						}
					}
					else if(roll == 2) {
						if((currentRow != 0) && (mazeMap[currentRow-1][currentCol]!= 2)) {
						//	pixelArray[currentRow][currentCol].setBackground(Color.WHITE);
							currentRow --;
							pixelArray[currentRow][currentCol].setBackground(Color.YELLOW);			
						}
						
					}
					else if(roll == 3) {
						if((currentCol != COLS-1) && (mazeMap[currentRow][currentCol+1]!= 2)) {
						//	pixelArray[currentRow][currentCol].setBackground(Color.WHITE);
							currentCol ++;
							pixelArray[currentRow][currentCol].setBackground(Color.YELLOW);	
						}
						
					}
					else if(roll == 4) {
						if((currentCol != 0) && (mazeMap[currentRow][currentCol-1]!= 2)) {
						//	pixelArray[currentRow][currentCol].setBackground(Color.WHITE);
							currentCol --;
							pixelArray[currentRow][currentCol].setBackground(Color.YELLOW);
						}

					}
				counter++;
				
			}
			numOfSteps.setText("Steps to Goal: " + counter);
			
		}
		else {
			numOfSteps.setText("NO PATH TO GOAL");
		}
			
	}
	//Triggers on mouse click
	//use the source to determine where the user wants to put a wall
	@Override
	public void mousePressed(MouseEvent e) {
		JPanel source = (JPanel)e.getSource();
		Color sourceBackground = source.getBackground();
		if(sourceBackground == Color.WHITE) {
			source.setBackground(Color.GRAY);
		}
		else if(sourceBackground == Color.GRAY){
			source.setBackground(Color.WHITE);
		}
	}
	//does nothing until commented
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	//performs an action based on the button
	//if solving, also displays the amount of time spent solving
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source  = e.getSource();
		if(source == solve) {
			long startTime = System.nanoTime();
			resetMaze();
			mazeMap = getMap();
			if(selectedAlgorithm == "Random") {
				Random();
			}
			else if (selectedAlgorithm == "DFS") {
				DFS();
			}
			else if(selectedAlgorithm == "BFS") {
				BFS();
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000 ;
			System.out.println(duration);
			timeRun.setText("Performed In: " + duration + "ms");
		}
		else if(source == randomGenerator) {
			generateRandomMaze();
		}
		else if(source == clear) {
			clearMaze();
		}
		else if(source == export) {
			logMaze();
		}
		
	}
	//determines which solve will be used when solve is pressed
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if(algorithmBox.getSelectedIndex() == 1) {
			selectedAlgorithm = "Random";
		}
		else if(algorithmBox.getSelectedIndex() == 2) {
			selectedAlgorithm = "DFS";
		}
		else if(algorithmBox.getSelectedIndex() == 3) {
			selectedAlgorithm = "BFS";
		}
		else {
			selectedAlgorithm = "None";
		}
		

		
	}

}//end of class
