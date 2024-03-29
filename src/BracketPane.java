import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.layout.Region;

/**
 * Created by Richard and Ricardo on 5/3/17.
 */
public class BracketPane extends StackPane {

        /**
         * Reference to the graphical representation of the nodes within the bracket.
         */
        private static ArrayList<BracketNode> nodes;
        /**
         * Used to initiate the paint of the bracket nodes
         */
        private static boolean isTop = true;
        /**
         * Maps the text "buttons" to it's respective grid-pane
         */
        private HashMap<StackPane, Pane> panes;
	/**
         * VBox for the Rounds
         * @author John E Youte
         */
        private VBox vBox=new VBox(); 
        /**
         * HBox for the Rounds
         * @author John E Youte
         */
        private HBox panegrid=new HBox();  
        /**
         * Reference to the current bracket.
         */
        private Bracket currentBracket;
        /**
         * Reference to active subtree within current bracket.
         */
        private int displayedSubtree;
        /**
         * Keeps track of whether or not bracket has been finalized.
         */
        private boolean finalized;
        /**
         * Important logical simplification for allowing for code that is easier
         * to maintain.
         */
        private HashMap<BracketNode, Integer> bracketMap = new HashMap<>();
        /**
         * Reverse of the above;
         */
        private HashMap<Integer, BracketNode> nodeMap = new HashMap<>();

        /**
         * The pane of the champion tree
         */
        private GridPane center;

        /** 
         * The pane that displays the full bracket
         */
        private GridPane fullPane;

        /**
         * Clears the entries of a team future wins
         *
         * @param treeNum the tree number of the selected bracket
         */
        private void clearAbove(int treeNum) {
                int nextTreeNum = (treeNum - 1) / 2;
                if (!nodeMap.get(nextTreeNum).getName().isEmpty()) {
                        nodeMap.get(nextTreeNum).setName("");
                        clearAbove(nextTreeNum);
                }
        }
        
        /**
         * Clears the selected tree of the bracket
         * @author Shane Callahan
         * 
         */
        public void clear(){
                //whenever displayedSubTree = 7, it is the full bracket. so clear the full bracket but the special case of making root = 0
            if(displayedSubtree == 7){
                clearSubtree(0);
            }
            else
                clearSubtree(displayedSubtree);
                //ends
        }

        /**
         * Handles clicked events for BracketNode objects
         */
        private EventHandler<MouseEvent> clicked = mouseEvent -> {
                //conditional added by matt 5/7 to differentiate between left and right mouse click
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        BracketNode n = (BracketNode) mouseEvent.getSource();
                        int treeNum = bracketMap.get(n);
                        int nextTreeNum = (treeNum - 1) / 2;
                        if (!nodeMap.get(nextTreeNum).getName().equals(n.getName())) {
                                currentBracket.removeAbove((nextTreeNum));
                                clearAbove(treeNum);
                                nodeMap.get((bracketMap.get(n) - 1) / 2).setName(n.getName());
                                currentBracket.moveTeamUp(treeNum);
                        }
                }
                //added by matt 5/7, shows the teams info if you right click
                else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        String text = "";
                        BracketNode n = (BracketNode) mouseEvent.getSource();
                        int treeNum = bracketMap.get(n);
                        String teamName = currentBracket.getBracket().get(treeNum);
                        try {
                                TournamentInfo info = new TournamentInfo();
                                Team t = info.getTeam(teamName);
                                //by Tyler - added the last two pieces of info to the pop up window
                                text += "Team: " + teamName + " | Ranking: " + t.getRanking() + "\nMascot: " + t.getNickname() + "\nInfo: " + t.getInfo() + "\nAverage Offensive PPG: " + t.getOffensePPG() + "\nAverage Defensive PPG: "+ t.getDefensePPG();
                        } catch (IOException e) {//if for some reason TournamentInfo isnt working, it will display info not found
                                text += "Info for " + teamName + "not found";
                        }
                        //create a popup with the team info
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.CLOSE);
                        alert.setTitle("March Madness Bracket Simulator");
                        alert.setHeaderText(null);
                        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        alert.showAndWait();
                }
        };
        /**
         * Handles mouseEntered events for BracketNode objects
         */
        private EventHandler<MouseEvent> enter = mouseEvent -> {
                BracketNode tmp = (BracketNode) mouseEvent.getSource();
                tmp.setStyle("-fx-background-color: green;");
                tmp.setEffect(new InnerShadow(10, Color.GREEN));
                
        };
        
         
        
        /**
         * Handles mouseExited events for BracketNode objects
         */
        private EventHandler<MouseEvent> exit = mouseEvent -> {
                BracketNode tmp = (BracketNode) mouseEvent.getSource();
                tmp.setStyle(null);
                tmp.setEffect(null);

        };
        /**
         * Returns the full bracket
         * @return fullPane
         */
        public GridPane getFullPane() {
                return fullPane;
                
        }

        


        /**
         * Initializes the properties needed to construct a bracket.
         * @param currentBracket the layout of the current bracket
         */
        public BracketPane(Bracket currentBracket) {
                displayedSubtree=0;
                this.currentBracket = currentBracket;

                bracketMap = new HashMap<>();
                nodeMap = new HashMap<>();
                panes = new HashMap<>();
                nodes = new ArrayList<>();
                ArrayList<Root> roots = new ArrayList<>();

                center = new GridPane();

                ArrayList<StackPane> buttons = new ArrayList<>();
                buttons.add(customButton("EAST"));
                buttons.add(customButton("WEST"));
                buttons.add(customButton("MIDWEST"));
                buttons.add(customButton("SOUTH"));
                buttons.add(customButton("FULL"));
                

                ArrayList<GridPane> gridPanes = new ArrayList<>();

                for (int m = 0; m < buttons.size() - 1; m++) {
                        roots.add(new Root(3 + m));
                        panes.put(buttons.get(m), roots.get(m));
                }
                Pane finalPane = createFinalFour();
                //buttons.add(customButton("FINAL"));
                //panes.put(buttons.get(5), finalPane);
                fullPane = new GridPane();
                //fullPane.setStyle("-fx-background-color: black");
                fullPane.setMaxHeight(1600);
                
                this.getChildren().add(roundLabels());
                this.getChildren().get(0).setVisible(false);
                
                /////////////////////////////////////////
             
                /////////////////////////////////////////
                
                
                GridPane gp1 = new GridPane();
                gp1.add(roots.get(0), 0, 0);
                gp1.add(roots.get(1), 0, 1);
                GridPane gp2 = new GridPane();
                gp2.add(roots.get(2), 0, 0);
                gp2.add(roots.get(3), 0, 1);
                gp2.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                //gp1.setStyle("-fx-background-color:  blue;");
                //gp2.setStyle("-fx-background-color: green;");
                ///////////////////////////////////////////
                 //fullPane.setStyle("-fx-background-color: yellow;");
                panegrid.getChildren().add(roundLabels());
                panegrid.getChildren().add(roundLabels2());
                panegrid.getChildren().add(roundLabels3());
                //panegrid.add(labelstry(), 2, 0);
 				////////////////////////////////////////////////////////////
                fullPane.add(gp1, 0, 0);
                fullPane.add(finalPane, 1, 0, 1, 2);
                fullPane.add(gp2, 2, 0 );
                //fullPane.add(labelstry(), 0, 0);
                
                
                fullPane.setAlignment(Pos.CENTER);
                vBox.getChildren().add(panegrid);
                vBox.getChildren().add(fullPane);
                panes.put(buttons.get((buttons.size() - 1)), vBox);
                finalPane.toBack();

                // Initializes the button grid
                GridPane buttonGrid = new GridPane();
                for (int i = 0; i < buttons.size(); i++)
                        buttonGrid.add(buttons.get(i), 0, i);
                buttonGrid.setAlignment(Pos.CENTER);

                // set default center to the button grid
                

                //Shane Callahan Start Editions
                Image imageTop;
                Image imageRight;
                ImageView imageViewTop = new ImageView();
                ImageView imageViewRight = new ImageView();
                try {
                        FileInputStream inputStreamTop = new FileInputStream("March_Madness_Logo_Test1.png"); 
                        FileInputStream inputstreamRight = new FileInputStream("Basketball_Logo_1.png"); 
                        imageTop = new Image(inputStreamTop);
                        imageRight = new Image(inputstreamRight);
                        imageViewTop = new ImageView(imageTop);
                        imageViewRight = new ImageView(imageRight);
                    } catch (Exception e) {
                        imageTop = new Image("https://github.com/madelesi/CS225_MarchMadness/blob/main/March_Madness_Logo_Test1.png?raw=true");
                        imageRight = new Image("https://github.com/madelesi/CS225_MarchMadness/blob/main/Basketball_Logo_1.png?raw=true");
                    }
                    GridPane topCenteredPane = new GridPane();
                    topCenteredPane.setAlignment(Pos.TOP_CENTER);
                    topCenteredPane.add(imageViewTop, 0, 0);
                    this.getChildren().add(topCenteredPane);

                    GridPane rightCenteredPane = new GridPane();
                    rightCenteredPane.setAlignment(Pos.CENTER_RIGHT);
                    rightCenteredPane.add(imageViewRight, 0, 0);
                    this.getChildren().add(rightCenteredPane);
                    this.getChildren().add(buttonGrid);
                
                    //end editions

                for (StackPane t : buttons) {
                        t.setOnMouseEntered(mouseEvent -> {
                                t.setStyle("-fx-background-color: lightblue;");
                                t.setEffect(new InnerShadow(10, Color.LIGHTCYAN));
                        });
                        t.setOnMouseExited(mouseEvent -> {
                                t.setStyle("-fx-background-color: orange;");
                                t.setEffect(null);
                        });
                        t.setOnMouseClicked(mouseEvent -> {
                                //add(null,0,1);
                                /**
                                 * @update Grant & Tyler 
                                 * 			panes are added as ScrollPanes to retain center alignment when moving through full-view and region-view
                                 */
                                center.add(new ScrollPane(panes.get(t)), 0, 0);
                                center.setAlignment(Pos.CENTER);
                                this.getChildren().addAll(center);
                                this.getChildren().get(1).setVisible(false);
                                this.getChildren().get(2).setVisible(false);

                                //Grant 5/7 this is for clearing the tree it kind of works 
                                displayedSubtree=buttons.indexOf(t)==7?0:buttons.indexOf(t)+3;
                        });
                }

        }

        /**
         * Helpful method to retrieve our magical numbers
         *
         * @param root the root node (3,4,5,6)
         * @param pos  the position in the tree (8 (16) , 4 (8) , 2 (4) , 1 (2))
         * @return The list representing the valid values.
         */
        public ArrayList<Integer> helper(int root, int pos) {
                ArrayList<Integer> positions = new ArrayList<>();
                int base = 0;
                int tmp = (root * 2) + 1;
                if (pos == 8) base = 3;
                else if (pos == 4) base = 2;
                else if (pos == 2) base = 1;
                for (int i = 0; i < base; i++) tmp = (tmp * 2) + 1;
                for (int j = 0; j < pos * 2; j++) positions.add(tmp + j);
                return positions; //                while ((tmp = ((location * 2) + 1)) <= 127) ;
        }

        /**
         * Sets the current bracket to,
         *
         * @param target The bracket to replace currentBracket
         */
        public void setBracket(Bracket target) {
                currentBracket = target;
        }

        /**
         * Clears the sub tree from,
         *
         * @param position The position to clear after
         */
        public void clearSubtree(int position) {
                currentBracket.resetSubtree(position);
        }

        /**
         * Resets the bracket-display
         */
        public void resetBracket() {
                currentBracket.resetSubtree(0);
        }

        /**
         * Requests a message from current bracket to tell if the bracket
         * has been completed and highlights empty nodes on the bracket.
         * @author Michael Skuncik
         *
         * @return True if completed, false otherwise.
         */
        public boolean isComplete() {

                if (!currentBracket.isComplete()) {

                        for (BracketNode n : nodes) {
                                if (n.getName().compareTo("") == 0) {
                                        n.setRect(Color.LIGHTPINK);
                                }
                        }
                        clearHighlights();
                        return false;
                }
                        return true;
        }
        /**
         * Method that will clear the highlighted empty cells after 5 seconds
         * @author Michael Skuncik and Zachary Lavoie
         *
         **/
        private void clearHighlights(){

                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                        @Override
                        public void run() {
                                for (BracketNode n : nodes) {
                                               n.setRect(Color.TRANSPARENT);
                               }
                        }
                };
                final int delay = 5000;
                timer.schedule(task, delay);
        }

        /**
         * @return true if the current-bracket is complete and the value
         * of finalized is also true.
         */
        public boolean isFinalized() {
                return currentBracket.isComplete() && finalized;
        }

        /**
         * @param isFinalized The value to set finalized to.
         */
        public void setFinalized(boolean isFinalized) {
                finalized = isFinalized && currentBracket.isComplete();
        }

        /**
         * Returns a custom "Button" with specified
         *
         * @param name The name of the button
         * @return pane The stack-pane "button"
         */
        private StackPane customButton(String name) {
                StackPane pane = new StackPane();
                Rectangle r = new Rectangle(100, 50, Color.TRANSPARENT);
                Text t = new Text(name);
                t.setTextAlignment(TextAlignment.CENTER);
                pane.getChildren().addAll(r, t);
                pane.setStyle("-fx-background-color: orange;");
                return pane;
        }

        /**
         * Draws the final four bracket node.
         * @return finalPane
         */
        public Pane createFinalFour() {
                Pane finalPane = new Pane();
                //finalPane.setMinSize(2000, 2000);
                BracketNode nodeFinal0 = new BracketNode("", 162, 400, 70, 0);
                BracketNode nodeFinal1 = new BracketNode("", 75, 500, 70, 0);
                BracketNode nodeFinal2 = new BracketNode("", 250, 500, 70, 0);
                nodeFinal0.setName(currentBracket.getBracket().get(0));
                nodeFinal1.setName(currentBracket.getBracket().get(1));
                nodeFinal2.setName(currentBracket.getBracket().get(2));
                finalPane.getChildren().add(nodeFinal0);
                finalPane.getChildren().add(nodeFinal1);
                finalPane.getChildren().add(nodeFinal2);
                //finalPane.getChildren().add(Labels());
                bracketMap.put(nodeFinal1, 1);
                bracketMap.put(nodeFinal2, 2);
                bracketMap.put(nodeFinal0, 0);
                nodeMap.put(1, nodeFinal1);
                nodeMap.put(2, nodeFinal2);
                nodeMap.put(0, nodeFinal0);

                nodeFinal0.setOnMouseClicked(clicked);
                nodeFinal0.setOnMouseDragEntered(enter);
                nodeFinal0.setOnMouseDragExited(exit);

                nodeFinal1.setOnMouseClicked(clicked);
                nodeFinal1.setOnMouseDragEntered(enter);
                nodeFinal1.setOnMouseDragExited(exit);

                nodeFinal2.setOnMouseClicked(clicked);
                nodeFinal2.setOnMouseDragEntered(enter);
                nodeFinal2.setOnMouseDragExited(exit);
                nodeFinal0.setStyle("-fx-border-color: black");
                nodeFinal1.setStyle("-fx-border-color: black");
                nodeFinal2.setStyle("-fx-border-color: black");
                finalPane.setMinWidth(600.0);
               

                return finalPane;
        }

        /**
         * Creates the graphical representation of a subtree.
         * Note, this is a vague model. TODO: MAKE MODULAR
         */
        private class Root extends Pane {

                /**
                 * Location of a bracket
                 */
                private int location;

                /**
                 * Constructor to draw the bracket
                 * @param location the location of the bracket in the window
                 */
                public Root(int location) {
                        this.location = location;
                        createVertices(420, 200, 100, 20, 0, 0);
                        createVertices(320, 119, 100, 200, 1, 0);
                        createVertices(220, 60, 100, 100, 2, 200);
                        createVertices(120, 35, 100, 50, 4, 100);
                        createVertices(20, 25, 100, 25, 8, 50);
                        //labels();
                        for (BracketNode n : nodes) {
                                n.setOnMouseClicked(clicked);
                                n.setOnMouseEntered(enter);
                                n.setOnMouseExited(exit);
                        }
                }
               
                /**
                 * The secret sauce... well not really,
                 * Creates 3 lines in appropriate location unless it is the last line.
                 * Adds these lines and "BracketNodes" to the Pane of this inner class
                 * @param iX integer of x
                 * @param iY integer of y
                 * @param iXO integer of x again
                 * @param iYO integer of y again
                 * @param num number 
                 * @param increment increment number
                 */
                private void createVertices(int iX, int iY, int iXO, int iYO, int num, int increment) {
                        int y = iY;
                        if (num == 0 && increment == 0) {
                                BracketNode last = new BracketNode("", iX, y - 20, iXO, 20);
                                nodes.add(last);
                                getChildren().addAll(new Line(iX, iY, iX + iXO, iY), last);
                                last.setName(currentBracket.getBracket().get(location));
                                bracketMap.put(last, location);
                                nodeMap.put(location, last);
                        } else {
                                ArrayList<BracketNode> aNodeList = new ArrayList<>();
                                for (int i = 0; i < num; i++) {
                                        Point2D tl = new Point2D(iX, y);
                                        Point2D tr = new Point2D(iX + iXO, y);
                                        Point2D bl = new Point2D(iX, y + iYO);
                                        Point2D br = new Point2D(iX + iXO, y + iYO);
                                        BracketNode nTop = new BracketNode("", iX, y - 20, iXO, 20);
                                        aNodeList.add(nTop);
                                        nodes.add(nTop);
                                        BracketNode nBottom = new BracketNode("", iX, y + (iYO - 20), iXO, 20);
                                        aNodeList.add(nBottom);
                                        nodes.add(nBottom);
                                        Line top = new Line(tl.getX(), tl.getY(), tr.getX(), tr.getY());
                                        Line bottom = new Line(bl.getX(), bl.getY(), br.getX(), br.getY());
                                        Line right = new Line(tr.getX(), tr.getY(), br.getX(), br.getY());
                                        getChildren().addAll(top, bottom, right, nTop, nBottom);
                                        isTop = !isTop;
                                        y += increment;
                                }
                                ArrayList<Integer> tmpHelp = helper(location, num);
                                for (int j = 0; j < aNodeList.size(); j++) {
                                        //System.out.println(currentBracket.getBracket().get(tmpHelp.get(j)));
                                        aNodeList.get(j).setName(currentBracket.getBracket().get(tmpHelp.get(j)));
                                        bracketMap.put(aNodeList.get(j), tmpHelp.get(j));
                                        nodeMap.put(tmpHelp.get(j), aNodeList.get(j));
                                        //System.out.println(bracketMap.get(aNodeList.get(j)));
                                }
                        }

                }
        }

        /**
         * The BracketNode model for the Graphical display of the "Bracket"
         */
        private class BracketNode extends Pane {
                private String teamName;
                private Rectangle rect;
                private Label name;

                /**
                 * Creates a BracketNode with,
                 *
                 * @param teamName The name if any
                 * @param x        The starting x location
                 * @param y        The starting y location
                 * @param rX       The width of the rectangle to fill pane
                 * @param rY       The height of the rectangle
                 */
                public BracketNode(String teamName, int x, int y, int rX, int rY) {
                        this.setLayoutX(x);
                        this.setLayoutY(y);
                        this.setMaxSize(rX, rY);
                        this.teamName = teamName;
                        rect = new Rectangle(rX, rY);
                        name = new Label(teamName);
                        rect.setFill(Color.TRANSPARENT);
						
                        // ******************************************************************************************************

                       /*                                        (Z.L.) ~ removed lines of code within BracketNode constructor.
                                                                          The following two (2) statements or complete commands
                                                                            have been removed to make the text alignment nicer.
                                                          **********************************************************************
                                                               statement 1 : setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                                                               statement 2 : name.setTranslateX(5);
                       */

                       // ******************************************************************************************************

                        getChildren().addAll( rect,name);
                }
                
                @SuppressWarnings("unused")
				public BracketNode() {
                   
            }

                /**
                 * @return teamName The teams name.
                 */
                public String getName() {
                        return teamName;
                }

                /**
                 * @param teamName The name to assign to the node.
                 */
                public void setName(String teamName) {
                        this.teamName = teamName;
                        name.setText(teamName);
                }

                /**
                 * Gets the rectangle
                 * @return rect
                 */                
		public Rectangle getRect() {
			return rect;
		}

                /**
                 * Sets the rectangles color
                 * @param j the color to set the rectangle
                 */
		public void setRect(Color j) {   //john////////////
			this.rect.setFill(j);
		}
                   
        }
        
		 /**
                  * Creates the left sides round label
                  * @return pane
                  * @author John E Youte
                  */
		public HBox roundLabels() {
			HBox pane=new HBox();
			 pane.setStyle("-fx-background-color: yellow;");
                         pane.setSpacing(20);
			Insets sert=new Insets(0 , 0, 0,45);
			String[] label= {"Round 1","Round 2","Sweet 16","Elite 8","Final Four"};
			for (int i = 0; i < label.length; i++) {
				Label lab=new Label(label[i]);
				lab.setTextFill(Color.WHITE);
				lab.setStyle("-fx-background-color: red;");
				pane.setMargin(lab , sert);
        		pane.getChildren().addAll(lab);
        		
			}
			
        	return pane;
        }
		
		
		/**
                 * Creates the center round label of Championship
                 * @author John E Youte
                 * @return pane
                 */
		public HBox roundLabels2() {
			HBox pane=new HBox();
			pane.setPrefWidth(600);
			pane.setAlignment(Pos.CENTER);
			//pane.setSpacing(40);
			pane.setStyle("-fx-background-color: green;");
			Insets sert=new Insets(0 , 0, 0,45);
			Label lab=new Label("Championship 🏆");
				lab.setTextFill(Color.WHITE);
				//lab.setStyle("-fx-background-color: red;");
				pane.setMargin(lab , sert);
        		pane.getChildren().addAll(lab);
			 return pane;
        }
		
		/**
                 * Creates the right round labels
                 * @author John E Youte
                 * @return pane
                 */
		public HBox roundLabels3() {
			HBox pane=new HBox();
                        pane.setSpacing(20);
			pane.setAlignment(Pos.BASELINE_RIGHT);
			 pane.setStyle("-fx-background-color: yellow;");
			Insets sert=new Insets(0 , 0, 0,45);
			String[] label= {"Final Four","Elite 8","Sweet 16","Round 2","Round 1"};
			for (int i = 0; i < label.length; i++) {
				Label lab=new Label(label[i]);
				lab.setTextFill(Color.WHITE);
				lab.setStyle("-fx-background-color: red;");
				pane.setMargin(lab , sert);
        		pane.getChildren().addAll(lab);
        	 }
        	return pane;
        }
		
		
		
		
		
}
