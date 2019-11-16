package Agent;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Gui extends Application {
    //TODO lookup SplitPane and how to use it.
    Pane root;
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
        root = new Pane();

        root.getChildren().addAll(
                makeBidAndBalanceColumn()
        );
    }
    private HBox getLabeledNodeBox(String label, Node node){
        HBox container = new HBox();
        Text text = new Text(label);
        container.getChildren().addAll(text,node);
        return container;
    }

    private VBox makeBidAndBalanceColumn(){
        VBox column = new VBox();
        refreshBalance = new Button("Refresh");
        submitBid = new Button("Bid!");
        refreshBids = new Button("Refresh");
        refreshBalance.setOnAction(e->handleRefreshBalance());
        refreshBids.setOnAction(e->handleRefreshBids());
        balance = new Text("0.00");
        availableFunds = new Text("0.00");
        selectedItem = new Text("");
        column.getChildren().addAll(
                getLabeledNodeBox("Account Balance: ",balance),
                getLabeledNodeBox("Available Funds: ", availableFunds),
                refreshBalance,
                new Separator(Orientation.HORIZONTAL),
                getLabeledNodeBox("Bid Amount: $", userEnteredAmount),
                selectedItem,
                submitBid,
                new Separator(Orientation.HORIZONTAL),
                new Text("Current Bids"),
                refreshBids
        );
        return column;
    }

    private void handleRefreshBids() {
    }

    private void handleRefreshBalance() {
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
