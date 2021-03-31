import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TestGui extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        //GridPane gp = new GridPane();
        AnchorPane ap = new AnchorPane();
        Button butt = new Button("test");
        //gp.add(butt, 0, 0);
        AnchorPane.setTopAnchor(butt, 100.0);
        ap.getChildren().addAll(butt);
        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
     launch(args);   
    }
}
