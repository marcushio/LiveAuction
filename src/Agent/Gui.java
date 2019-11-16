package Agent;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Gui extends Application {
    SplitPane root;
    Scene scene;
    TextArea userEnteredAmount = new TextArea("0.00");
    Button refreshBalance, submitBid, refreshBids, selectItem, refreshHousesList, selectHouse;
    Text balance, availableFunds, selectedItem, selectedHouse;
    public static void main (String [] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage){
        makeLayout();
        setWindow();
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Auction App");
        primaryStage.show();
    }

    private void setWindow(){
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screenBounds.getWidth(),screenBounds.getHeight());
    }
    private void makeLayout(){
        root = new SplitPane();
    }
    private HBox makeBidAmountNode(){
        HBox container = new HBox();
        Text label = new Text("Amount: $");
        container.getChildren().addAll(label,userEnteredAmount);
        return container;
    }
    private VBox makeBidAndBalanceColumn(){
        VBox column = new VBox();

        return column;
    }
    private VBox makeItemsColumn(){
        VBox column = new VBox();

        return column;
    }

    private VBox makeHousesColumn(){
        VBox column = new VBox();

        return column;
    }
}
