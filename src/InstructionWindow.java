// ******************************************************************************************************

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

// created concrete window to display instructions regarding software usage to the user of the software.       Z.L.
public final class InstructionWindow extends Stage {

    public InstructionWindow() {
        this("Instructions");
    }

    public InstructionWindow(String title) {
        Text content = new Text(instructions());
        orientAndDisplayContent(content, title);
    }

    private String instructions() {

        return  "\t\t\t\t\t\t\t\t\t\t\t<<<(IN A NUTSHELL)>>>\n-----------------------------------\n~ logging in ~" +
                "\n-----------------------------------\n" +
                "\tstep1) Enter a “Username” and “Password.”" +
                               " the program will check if the entered credentials already exists.\n"  +
                "[If] the Username and Password is found, you will be navigated to your already"  +
                                                                     " filled/semi-filled bracket.\n"  +
                "[If] the Username and Password is not found the entered Username and Password will"   +
                " be saved\nand you will be prompted to start filling out your bracket.\n"        +
                "Once you log out you can now log in with your new credentials previously entered.\n"
                + "-----------------------------------\n~ creating a new bracket ~" +
                  "\n-----------------------------------\n"
                + "\tstep2) After creating a new account you will be prompted with the menu to the right,\n" +
                  "(East, West, Midwest, South and Full) each selection will prompt you to complete a portion" +
                  " of your bracket division by division.\n" +
                  "Right-click on a team to display some information about the team in a pop-up window.\n" +
                  "To Select a team to move onto the next round, simply click their name.\n" +
                "The team will automatically move to the next empty slot in the path.\n" +
                "Continue this pattern until all fields are complete.\n-----------------------------------\n"
                + "~ completed brackets ~\n-----------------------------------\n\tstep3) "
                + "Once all fields, divisions and the final four are filled out the bracket will be loaded,\n" +
                "and displayed on the screen after selecting the “Full” option.\n" +
                "Once you are satisfied with your choices you can select the “Finalize” button,\n" +
                "which will lock your bracket in.\n" +
                "You will still have the option to re-finalize your bracket if changes were preferred.\n" +
                "Select the “Simulate” button to simulate the tournament.\n" +
                "After you select this button you will be able to determine" +
                " which user has won the tournament and even\nsee the simulated bracket results as well as your" +
                " own.\n-----------------------------------\n~ rankings / user score ~\n"
                + "-----------------------------------\n\tstep4) After the simulation is complete\n" +
                "you will be navigated to the Ranking Chart.\n" +
                "This will display each individual total score accumulated by a given user.\n" +
                "The winner will be the first name in the list and the loser will be the last name in the list.\n";

    }

    private void orientAndDisplayContent(Text text, String title) {

        BackgroundFill bgf = new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY);
        Background bg = new Background(bgf);

        VBox layout = new VBox();
        layout.setBackground(bg);

        TextArea body = new TextArea();
        body.setMaxWidth(700.0);
        body.setMinHeight(550.0);
        body.setEditable(false);
        body.setText(text.getText());

        layout.getChildren().add(body);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 750.0, 600.0);

        setTitle(title);
        setScene(scene);
        initModality(Modality.APPLICATION_MODAL);

        show();

    }

}

// ******************************************************************************************************
