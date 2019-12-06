package Agent;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.List;

public class Gui extends Application {
    private Pane root;
    private Scene scene;
    private TextField userEnteredAmount = new TextField("0.00");
    private ListView<String> itemList = new ListView<>();
    private ListView <String> auctionHouseList = new ListView<>();
    private ListView currentBidsList = new ListView<>();
    private Button refreshBalance, submitBid, refreshBids, selectItem, refreshHousesList, selectHouse;
    private Text balance, availableFunds, selectedItem, selectedHouse, name;
    private TextArea userMessages = new TextArea("");
    private static Agent agent;
    private boolean isClosable = false;
    public static void main (String [] args){
        String status [] = new String[1];
        if(args.length>2) {
            {
                String startingFunds = args[0];
                String bankAddress = args[args.length - 1];
                String myAddress = args[args.length-2];
                String name = "";
                for (int i = 1; i < args.length - 2; i++) {
                    name += args[i] + " ";
                }
                try {
                    agent = new Agent(name, startingFunds, myAddress, bankAddress);
                } catch (Exception e) {
                    status [0] = "Failed to connect to bank.";
                }
                try{
                    agent.registerWithRMI();
                }
                catch(RemoteException e){
                    status[0] = "RMI Registration Failure";
                }
            }

        }
        launch(status);

    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest((WindowEvent windowEvent) -> {
            windowEvent.consume();
            if(agent.canExit()) System.exit(0);
            else {
                userMessages.appendText("Could not exit because of unresolved bids.");
            }

        });
        int numArgs = getParameters().getRaw().size();
        makeLayout();
        setWindow();
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Auction App");
        if (numArgs == 0){
            bindVariables(agent);
        }
        else{
            userMessages.appendText(getParameters().getRaw().get(0));
        }
        primaryStage.show();
    }
    public void bindVariables(Agent agent){
        name.textProperty().set(agent.getName().get());
        balance.textProperty().bind(agent.getCurrentBalanceProperty());
        availableFunds.textProperty().bind(agent.getAvailableFundsProperty());
        selectedItem.textProperty().bindBidirectional(agent.getSelectedItemProperty());
        userEnteredAmount.textProperty().bindBidirectional(agent.getCurrentBidAmount());
        selectedHouse.textProperty().bindBidirectional(agent.getSelectedHouseProperty());
        auctionHouseList.setItems(agent.getHousesAddressList());
        itemList.setItems(agent.getItemStringList());
        currentBidsList.setItems(agent.getBidList());
        userMessages.textProperty().bind(agent.getMessagesProperty());
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
                makeHousesColumn(),
                makeItemsColumn(),
                makeBidAndBalanceColumn(),
                userMessages

        );
    }
    private HBox getLabeledNodeBox(String label, Node node){
        HBox container = new HBox();
        Text text = new Text(label);
        container.getChildren().addAll(text,node);
        return container;
    }

    private VBox makeBidAndBalanceColumn() {
        VBox column = new VBox();
        refreshBalance = new Button("Refresh");
        submitBid = new Button("Bid!");
        refreshBids = new Button("Refresh");
        submitBid.setOnAction(event -> handleSubmitBid());
        refreshBids.setOnAction(e->handleRefreshBids());
        refreshBalance.setOnAction(e->handleRefreshBalances());
        balance = new Text("0.00");
        availableFunds = new Text("0.00");
        selectedItem = new Text("");
        name = new Text("");
        column.getChildren().addAll(
                getLabeledNodeBox("Name: ", name),
                getLabeledNodeBox("Account Balance: $",balance),
                getLabeledNodeBox("Available Funds: $", availableFunds),
                refreshBalance,
                new Separator(Orientation.HORIZONTAL),
                getLabeledNodeBox("Bid Amount: $", userEnteredAmount),
                getLabeledNodeBox("Selected Item: ", selectedItem),
                submitBid,
                new Separator(Orientation.HORIZONTAL),
                new Text("Current Bids"),
                currentBidsList,
                refreshBids
        );
        return column;
    }

    private void handleSubmitBid() {
        try {
            agent.submitBid();
        }
        catch(RemoteException e){

        }
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

    private VBox makeHousesColumn(){
        VBox column = new VBox();
        HBox buttons = new HBox();
        refreshHousesList = new Button("Refresh");
        refreshHousesList.setOnAction(e->handleRefreshHouseList());
        selectHouse = new Button("Connect");
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

    private void handleSelectItem() {
        selectedItem.setText(itemList.getSelectionModel().getSelectedItem());
    }

    private void handleSelectHouse() {
        agent.connectToHouse(auctionHouseList.getSelectionModel().getSelectedItem());
        handleRefreshItems();
    }

    private void handleRefreshHouseList() {
        agent.refreshAvailableHouses();
    }

    private void handleRefreshBids() {
        agent.refreshBidList();
    }

    private void handleRefreshItems(){
        try {
            agent.refreshItemList();
        }
        catch(RemoteException e){

        }
    }
    private void handleRefreshBalances() {
        agent.refreshBalances();
    }
}
