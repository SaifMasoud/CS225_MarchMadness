// ******************************************************************************************************

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// created concrete window that displays the user's picked results for comparison with simulated results.      Z.L.
public final class UserBracketViewer extends Stage {

    private static final String TITLE = "March Madness Bracket Simulator - user's results";

    public UserBracketViewer(GridPane gp) {

        Pane p = new Pane();
        gp.setDisable(true);
        p.getChildren().add(gp);

        setTitle(TITLE);
        setScene(new Scene(p));
        setMaximized(true);
        show();

    }

}

// ******************************************************************************************************
