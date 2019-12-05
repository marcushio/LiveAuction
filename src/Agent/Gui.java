package Agent;

import javafx.application.Application;
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
    private static TextArea userMessages = new TextArea("");
    Agent agent;

    public static void main (String [] args){
            launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        makeLayout();
        setWindow();
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Auction App");
        primaryStage.show();
        try {
            handleParams();
        }
        catch(Exception e){
            
        }
        bindVariables(agent);
    }
    private void bindVariables(Agent agent){
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
        agent.connect(auctionHouseList.getSelectionModel().getSelectedItem());
        handleRefreshItems();
    }

    private void handleRefreshHouseList() {
        agent.refreshAvailableHouses();
    }

    private void handleRefreshBids() {
        agent.refreshBidList();
    }

    private void handleParams() throws Exception {
        List<String> params = getParameters().getRaw();
        if(params.size()>2)
        {
            String startingFunds = params.get(0);
            String bankAddress = params.get(params.size()-1);
            String name = "";
            for(int i = 1; i < params.size()-1; i++){
                name += params.get(i) + " ";
            }
            agent = new Agent(name,startingFunds,bankAddress);
            agent.registerWithRMI();
        }
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
