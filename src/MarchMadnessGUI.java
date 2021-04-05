import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *  MarchMadnessGUI
 * 
 * this class contains the buttons the user interacts
 * with and controls the actions of other objects 
 *
 * @author Grant Osborn
 */
public class MarchMadnessGUI extends Application {
    
    
    //all the gui elements
    private BorderPane root;
    private ToolBar toolBar;
    private ToolBar btoolBar;
    private Button simulate;
    private Button login;
    private Button scoreBoardButton;
    private Button viewBracketButton;
    private Button resetSimulationButt;
    private Button clearButton;
    private Button resetButton;
    private Button finalizeButton;
    private Button saveBtn = new Button("Save");
    
    //allows you to navigate back to division selection screen
    private Button back;
  
    
    private  Bracket startingBracket; 
    //reference to currently logged in bracket
    private Bracket selectedBracket;
    private Bracket simResultBracket;

    
    private ArrayList<Bracket> playerBrackets;
    private HashMap<String, Bracket> playerMap;

    
    private String name;
    private ScoreBoardTable scoreBoard;
    private TableView table;
    private BracketPane bracketPane;
    private GridPane loginP;
    private TournamentInfo teamInfo;
    private StackPane stackPane;
    
    
    @SuppressWarnings("static-access")
	@Override
    public void start(Stage primaryStage) {
        
        primaryStage.setOnCloseRequest(e->System.exit(0));
        
        //try to load all the files, if there is an error display it
        try{
            teamInfo=new TournamentInfo();
            startingBracket= new Bracket(TournamentInfo.loadStartingBracket());
            simResultBracket=new Bracket(TournamentInfo.loadStartingBracket());
        } catch (IOException ex) {
            showError(new Exception("Can't find "+ex.getMessage(),ex),true);
        }
       
        
        root = new BorderPane();//deserialize stored brackets
        playerBrackets = loadBrackets();
        
        playerMap = new HashMap<>();
        addAllToMap();
        
        


        //the main layout container
       
        
        
        scoreBoard= new ScoreBoardTable();
        table=scoreBoard.start();
        loginP=createLogin();
        CreateToolBars();
       
        
        //display login screen
        login();
        
        setActions();
        root.setTop(toolBar); 
        root.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        
        root.setBottom(btoolBar);
        Scene scene = new Scene(root);
       
        

        //logout button display ET
        createLogOut();

        primaryStage.setTitle("March Madness Bracket Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(true);//all of us getting this back

        // ******************************************************************************************************

        // added an instruction window to explain the use of the software "in a nutshell"                       Z.L.
        new InstructionWindow("How to: Software Utilization");

        // ******************************************************************************************************

    }
    //Creates a logout button for user to exit game at any time. ET
    private void createLogOut() {
        Button logOut = new Button("logout");
        btoolBar.getItems().add(logOut);
        logOut.setOnAction( e -> login());

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
	/*
	 * ///////////////////////////////////////////// //John is doing that
	 * 
	 * @SuppressWarnings({ "exports", "static-access" }) public void labels() {
	 * 
	 * Label label=new Label(); label.setText("boom"); label.setLayoutX(20);
	 * label.setLayoutY(20); //this.setMaxSize(900, 900); stackPane = new
	 * StackPane(); stackPane.getChildren().add(label);
	 * stackPane.setStyle("-fx-background-color: orange;");
	 * root.setAlignment(stackPane, Pos.TOP_CENTER); root.setTop(stackPane); }
	 * //////////////////////////////////////////////////
	 */    
    
    
    
    
    
    
    
    
    /**
     * simulates the tournament  
     * simulation happens only once and
     * after the simulation no more users can login
     */
    private void simulate(){

        // ******************************************************************************************************

        // allows the user to see their results so they may compare them with the simulated results.           Z.L.
        new UserBracketViewer(bracketPane.getFullPane(),name);

        // ******************************************************************************************************
  

        //cant login and restart prog after simulate
        login.setDisable(true);
        simulate.setDisable(true);
        
       scoreBoardButton.setDisable(false);
       viewBracketButton.setDisable(false);
       resetSimulationButt.setDisable(false);
       
       teamInfo.simulate(simResultBracket);
       for(Bracket b:playerBrackets){
           scoreBoard.addPlayer(b,b.scoreBracket(simResultBracket));
           
       }
        
        displayPane(table);
    }
    
    /**
     * Displays the login screen
     * 
     */
    private void login(){            
        login.setDisable(true);
        simulate.setDisable(true);
        scoreBoardButton.setDisable(true);
        viewBracketButton.setDisable(true);
        btoolBar.setDisable(true);
        resetSimulationButt.setDisable(true);
        displayPane(loginP);
    }
    
     /**
     * Displays the score board
     * 
     */
    private void scoreBoard(){
        displayPane(table);
    }
    
     /**
      * Displays Simulated Bracket
      * 
      */
    private void viewBracket(){
       selectedBracket = simResultBracket;
       bracketPane = new BracketPane(selectedBracket);
       GridPane full = new GridPane();
       full.add(bracketPane.getFullPane(),0,0);
       full.setDisable(true);
       ScrollPane scrollPane = new ScrollPane(full);
       //Shane Callahan, small fix, just added ScrollPane to GridPane to make it centered.
       GridPane centerPane = new GridPane();
       centerPane.add(scrollPane, 0, 0);
       centerPane.setAlignment(Pos.CENTER);
       displayPane(centerPane); 
    }
    
    /**
     * Method that is called when the Reset Simulation button is pressed
     * It will reset the scores of the simulation and bring the user back to login
     * @author Michael Skuncik
     */
    private void resetSimulation(){

        if(confirmSimReset()) {
            scoreBoard= new ScoreBoardTable();
            table=scoreBoard.start();
            login();
        }

    }
    
    /**
     * allows user to choose bracket
     * 
     */
   private void chooseBracket(){
        //login.setDisable(true);
        btoolBar.setDisable(false);
        bracketPane=new BracketPane(selectedBracket);
        displayPane(bracketPane);

    }
    /**
     * resets current selected sub tree
     * for final4 reset Ro2 and winner
     */
    private void clear(){
      
      
      bracketPane.clear();
      bracketPane=new BracketPane(selectedBracket);
      displayPane(bracketPane);
        
    }
    
    /**
     * resets entire bracket
     */
    private void reset(){
        if(confirmReset()){
            //horrible hack to reset
            selectedBracket=new Bracket(startingBracket);
            bracketPane=new BracketPane(selectedBracket);
            displayPane(bracketPane);
        }
    }
    
    private void finalizeBracket(){
       if(bracketPane.isComplete()){

            // ******************************************************************************************************

            // ensures only one reference, predecessor is subject to garbage collection.                         Z.L.
            scoreBoard = new ScoreBoardTable();
            // ensures only one reference, predecessor is subject to garbage collection.                         Z.L.
            table = scoreBoard.start();

            // allows a user to still make any more changes to finalize, even after clicking "finalize."         Z.L.
            ObservableList<Node> bottomItems = btoolBar.getItems();
            for(int i = 0; i < bottomItems.size(); i++) {
                switch(i) {
                    case 3: case 5:
                        continue;
                }
                bottomItems.get(i).setDisable(true);
            }

            // ******************************************************************************************************

           
           bracketPane.setDisable(true);
           simulate.setDisable(false);
           login.setDisable(false);
           resetSimulationButt.setDisable(true);
           //save the bracket along with account info
           seralizeBracket(selectedBracket);
            
       }else{
            infoAlert("You can only finalize a bracket once it has been completed.");
            //go back to bracket section selection screen
            // bracketPane=new BracketPane(selectedBracket);
            displayPane(bracketPane);
        
       }
       //bracketPane=new BracketPane(selectedBracket);
      
      
        
    }
    
    
    /**
     * displays element in the center of the screen
     * 
     * @param p must use a subclass of Pane for layout. 
     * to be properly center aligned in  the parent node
     */
    private void displayPane(Node p){
        root.setCenter(p);
        BorderPane.setAlignment(p,Pos.CENTER);
    }
    
    /**
     * Creates toolBar and buttons.
     * adds buttons to the toolbar and saves global references to them
     */
    private void CreateToolBars(){
        toolBar  = new ToolBar();
        btoolBar  = new ToolBar();
        login=new Button("Login");
        simulate=new Button("Simulate");
        scoreBoardButton=new Button("ScoreBoard");
        viewBracketButton= new Button("View Simulated Bracket");
        resetSimulationButt = new Button("Reset Simulation");
        clearButton=new Button("Clear");
        resetButton=new Button("Reset");
        finalizeButton=new Button("Finalize");
        toolBar.getItems().addAll(
                createSpacer(),
                login,
                simulate,
                scoreBoardButton,
                viewBracketButton,
                resetSimulationButt,
                createSpacer()
        );
        btoolBar.getItems().addAll(
                createSpacer(),
                clearButton,
                resetButton,
                finalizeButton,
                saveBtn,
                back=new Button("Choose Division"),
                createSpacer()
        );
    }
    
   /**
    * sets the actions for each button
    */
    private void setActions(){
        login.setOnAction(e->login());
        simulate.setOnAction(e->simulate());
        scoreBoardButton.setOnAction(e->scoreBoard());
        viewBracketButton.setOnAction(e->viewBracket());
        resetSimulationButt.setOnAction(e->resetSimulation());
        clearButton.setOnAction(e->clear());
        resetButton.setOnAction(e->reset());
        finalizeButton.setOnAction(e->finalizeBracket());
        back.setOnAction(e->{
            bracketPane=new BracketPane(selectedBracket);
            displayPane(bracketPane);
        });
        // Saif Masoud: save users progress.
        saveBtn.setOnAction(event -> {
            seralizeBracket(selectedBracket);
        });
    }
    
    /**
     * Creates a spacer for centering buttons in a ToolBar
     */
    private Pane createSpacer(){
        Pane spacer = new Pane();
        HBox.setHgrow(
                spacer,
                Priority.SOMETIMES
        );
        return spacer;
    }
    
    
    private GridPane createLogin(){
        
        
        /*
        LoginPane
        Sergio and Joao
         */

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(5, 5, 5, 5));

        Text welcomeMessage = new Text("March Madness Login");
        loginPane.add(welcomeMessage, 0, 0, 2, 1);

        Label userName = new Label("Username: ");       //Edited username ET
        loginPane.add(userName, 0, 1);

        TextField enterUser = new TextField();
        loginPane.add(enterUser, 1, 1);

        Label password = new Label("Password: ");
        loginPane.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        loginPane.add(passwordField, 1, 2);

        Button signButton = new Button("Sign in");
        loginPane.add(signButton, 1, 4);
        signButton.setDefaultButton(true);//added by matt 5/7, lets you use sign in button by pressing enter

        Label message = new Label();
        loginPane.add(message, 1, 5);

        // Saif Masoud: Add a drop-down for registered users
        ObservableList<String> options =
                FXCollections.observableArrayList(playerMap.keySet());
        ComboBox usersDropDown = new ComboBox(options);
        usersDropDown.setOnAction(event -> enterUser.setText((String) usersDropDown.getValue()));
        loginPane.add(usersDropDown, 2, 1);

        signButton.setOnAction(event -> {

            // the name user enter
            name = enterUser.getText();
            // the password user enter
            String playerPass = passwordField.getText();

        
          
            
            if (playerMap.get(name) != null) {
                //check password of user
                 
                Bracket tmpBracket = this.playerMap.get(name);
               
                String password1 = tmpBracket.getPassword();

                if (Objects.equals(password1, playerPass)) {
                    // load bracket
                    selectedBracket=playerMap.get(name);
                    chooseBracket();
                }else{
                   infoAlert("The password you have entered is incorrect!");
                }

            } else {
                //check for empty fields
                if(!name.equals("")&&!playerPass.equals("")){
                    //create new bracket
                    Bracket tmpPlayerBracket = new Bracket(startingBracket, name);
                    playerBrackets.add(tmpPlayerBracket);
                    tmpPlayerBracket.setPassword(playerPass);

                    playerMap.put(name, tmpPlayerBracket);
                    selectedBracket = tmpPlayerBracket;
                    //alert user that an account has been created
                    infoAlert("No user with the username \""  + name + "\" exists. A new account has been created.");
                    chooseBracket();
                }
            }
        });
        
        return loginPane;
    }
    
    /**
     * addAllToMap
     * adds all the brackets to the map for login
     */
    private void addAllToMap(){
        for(Bracket b:playerBrackets){
            playerMap.put(b.getPlayerName(), b);   
        }
    }
    
    /**
     * The Exception handler
     * Displays a error message to the user
     * and if the error is bad enough closes the program
     * msg message to be displayed to the user
     * @param fatal true if the program should exit. false otherwise 
     */
    private void showError(Exception e,boolean fatal){
        String msg=e.getMessage();
        if(fatal){
            msg=msg+" \n\nthe program will now close";
            //e.printStackTrace();
        }
        Alert alert = new Alert(AlertType.ERROR,msg);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(420);   
        alert.setTitle("Error");
        alert.setHeaderText("something went wrong");
        alert.showAndWait();
        if(fatal){ 
            System.exit(666);
        }   
    }
    
    /**
     * alerts user to the result of their actions in the login pane 
     * @param msg the message to be displayed to the user
     */
    private void infoAlert(String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    /**
     * Prompts the user to confirm that they want
     * to clear all predictions from their bracket
     * @return true if the yes button clicked, false otherwise
     */
    private boolean confirmReset(){
        Alert alert = new Alert(AlertType.CONFIRMATION, 
                "Are you sure you want to reset the ENTIRE bracket?", 
                ButtonType.YES,  ButtonType.CANCEL);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult()==ButtonType.YES;
    }
    
    /**
     * Creates a popup window asking the user to confirm that they want
     * to reset the simulation
     * @author Michael Skuncik
     * @return the user's selection in the popup window
     */
    private boolean confirmSimReset(){
        Alert alert = new Alert(AlertType.CONFIRMATION,
                "Are you sure you want to reset the simulation?",
                ButtonType.YES,  ButtonType.CANCEL);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult()==ButtonType.YES;
    }
    
    
    /**
     * Tayon Watson 5/5
     * seralizedBracket
     * @param B The bracket the is going to be seralized
     */
    private void seralizeBracket(Bracket B){
        FileOutputStream outStream = null;
        ObjectOutputStream out = null;
    try 
    {
      outStream = new FileOutputStream(B.getPlayerName()+".ser");
      out = new ObjectOutputStream(outStream);
      out.writeObject(B);
      out.close();
    } 
    catch(IOException e)
    {
      // Grant osborn 5/6 hopefully this never happens 
      showError(new Exception("Error saving bracket \n"+e.getMessage(),e),false);
    }
    }
    /**
     * Tayon Watson 5/5
     * deseralizedBracket
     * @param filename of the seralized bracket file
     * @return deserialized bracket 
     */
    private Bracket deseralizeBracket(String filename){
        Bracket bracket = null;
        FileInputStream inStream = null;
        ObjectInputStream in = null;
    try 
    {
        inStream = new FileInputStream(filename);
        in = new ObjectInputStream(inStream);
        bracket = (Bracket) in.readObject();
        in.close();
    }catch (IOException | ClassNotFoundException e) {
      // Grant osborn 5/6 hopefully this never happens either
      showError(new Exception("Error loading bracket \n"+e.getMessage(),e),false);
    } 
    return bracket;
    }
    
      /**
     * Tayon Watson 5/5
     * deseralizedBracket
     * filename of the seralized bracket file
     * @return deserialized bracket 
     */
    private ArrayList<Bracket> loadBrackets()
    {   
        ArrayList<Bracket> list=new ArrayList<Bracket>();
        File dir = new File(".");
        for (final File fileEntry : dir.listFiles()){
            String fileName = fileEntry.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);
       
            if (extension.equals("ser")){
                list.add(deseralizeBracket(fileName));
            }
        }
        return list;
    }
       
}
