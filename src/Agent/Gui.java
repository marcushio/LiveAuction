package Agent;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    TextArea itemList = new TextArea("");
    TextArea auctionHouseList = new TextArea("");
    TextArea currentBidsList = new TextArea("");
    Button refreshBalance, submitBid, refreshBids, selectItem, refreshHousesList, selectHouse;
    Text balance, availableFunds, selectedItem, selectedHouse;
    public static void main (String [] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage){
        Agent agent = new Agent();
        makeLayout();
        setWindow();
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Auction App");
        bindVariables(agent);
        primaryStage.show();
    }
    private void bindVariables(Agent agent){
        balance.textProperty().bind(agent.getCurrentBalanceProperty());
        availableFunds.textProperty().bind(agent.getAvailableFundsProperty());
        selectedItem.textProperty().bindBidirectional(agent.getSelectedItemProperty());
        selectedHouse.textProperty().bindBidirectional(agent.getSelectedHouseProperty());
    }
    private void setWindow(){
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        scene = new Scene(root, screenBounds.getWidth(),screenBounds.getHeight());
    }
    private void makeLayout(){
        root = new Pane();
        HBox columnContainer = new HBox();
        root.getChildren().add(columnContainer);
        columnContainer.getChildren().addAll(
                makeBidAndBalanceColumn(),
                makeItemsColumn(),
                makeHousesColumn()
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
        userEnteredAmount.setPrefRowCount(0);
        userEnteredAmount.setPrefColumnCount(9);
        column.getChildren().addAll(
                getLabeledNodeBox("Account Balance: $",balance),
                getLabeledNodeBox("Available Funds: $", availableFunds),
                refreshBalance,
                new Separator(Orientation.HORIZONTAL),
                getLabeledNodeBox("Bid Amount: $", userEnteredAmount),
                getLabeledNodeBox("Item: ", selectedItem),
                submitBid,
                new Separator(Orientation.HORIZONTAL),
                new Text("Current Bids"),
                currentBidsList,
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
        selectedHouse = new Text("");
        selectItem = new Button("Select");
        selectItem.setOnAction(e->handleSelectItem());
        ScrollPane scrollItems = new ScrollPane(itemList);
        column.getChildren().addAll(
                getLabeledNodeBox("Selected House: ", selectedHouse),
                new Separator(Orientation.HORIZONTAL),
                new Text("Items"),
                scrollItems,
                selectItem
        );
        return column;
    }

    private void handleSelectItem() {
    }

    private VBox makeHousesColumn(){
        VBox column = new VBox();
        HBox buttons = new HBox();
        refreshHousesList = new Button("Refresh");
        refreshHousesList.setOnAction(e->handleRefreshHouseList());
        selectHouse = new Button("Select");
        selectHouse.setOnAction(e->handleSelectHouse());
        buttons.getChildren().addAll(refreshHousesList, selectHouse);
        column.getChildren().addAll(
               new Text("Auction Houses"),
                auctionHouseList,
                new Separator(Orientation.HORIZONTAL),
                buttons
        );

        return column;
    }

    private void handleSelectHouse() {
    }

    private void handleRefreshHouseList() {
    }
}
