// ******************************************************************************************************

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// created concrete window that displays the user's picked results for comparison with simulated results.      Z.L.
public final class UserBracketViewer extends Stage {


    public UserBracketViewer(GridPane gp, String name) {

        Pane p = new Pane();
        gp.setDisable(true);
        p.getChildren().add(gp);
        
        String title = "March Madness Bracket Simulator - " + name + "'s results";

        setTitle(title);
        setScene(new Scene(p));
        setMaximized(true);
        show();

    }

}

// ******************************************************************************************************
