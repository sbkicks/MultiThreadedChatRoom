import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField c1;
	Button serverChoice,clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	ClientInfo info;
	ListView<String> listItems, listItems2;
	Boolean bool = false;
	TextField message;
	Button connect;
	String name;
	VBox vb;
	TextArea recieved;
	TextArea sent;
	Button send;
	ArrayList<String> choices;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Project 4");
		
		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");
		
		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("This is the Server");
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						listItems.getItems().add(data.toString());
					});

				});
											
		});
		
		
		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");
		
		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("welcome"));
			primaryStage.setTitle("This is an absolute chonk client");
			clientConnection = new Client(data->{
				Platform.runLater(()->{
					synchronized(((ClientInfo)data).clients) {
						if (((ClientInfo)data).send) {
							ArrayList<String> clients = ((ClientInfo)data).clients;
							vb.getChildren().clear();
							for (int i = 0; i < clients.size(); i++) {
								Button bt = new Button(clients.get(i));
								bt.setPrefSize(100, 25);
								bt.setPrefWidth(100);
								bt.setOnAction(e1-> {
									if (choices.contains(bt.getText()))
										choices.remove(bt.getText());
									else
										choices.add(bt.getText());
								});
								vb.getChildren().add(bt);
							}
						} else {
							recieved.setText(recieved.getText()+((ClientInfo)data).msg);
						}
					}
				});
			});
			this.clientConnection.start();
		});
		
		connect = new Button("CONNECT");
		connect.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("client"));
			name = message.getText();
			choices = new ArrayList<String>();
			info = new ClientInfo(name, "", choices);
			clientConnection.send(info);
			bool = true;
		});
		
		this.buttonBox = new HBox(400, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		
		startScene = new Scene(startPane, 800,800);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		
		c1 = new TextField();
		b1 = new Button("Send");
		sent = new TextArea();
		recieved = new TextArea();
		send = new Button("send");
		
		b1.setOnAction(e->{
			if(bool == false) {
				ArrayList<String> clientChoice = new ArrayList<String>();
				info = new ClientInfo(name, "", clientChoice);
				clientConnection.send(info);
				bool = true;
			} else {
				ArrayList<String> clientChoice = new ArrayList<String>();
				clientChoice.add(c1.getText());
				info = new ClientInfo("", c1.getText(), clientChoice);
				clientConnection.send(info);
			}
			});
		
		send.setOnAction(e->{
			System.out.println("The choice list is:");
			for(int i=0; i< choices.size(); i++) {
				System.out.println(choices.get(i));
			}
			info = new ClientInfo("", sent.getText(), choices);
			clientConnection.send(info);
		});
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		sceneMap.put("welcome", createWelcomeGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		 
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}
	
	public Scene createServerGui() {
		BorderPane pane = new BorderPane();
		pane.setCenter(listItems);
		pane.setPadding(new Insets(50));
		return new Scene(pane, 500, 400);
	}
	
	public Scene createWelcomeGui() {
		BorderPane pane = new BorderPane();
		Text username = new Text("Enter Name:");
		message = new TextField();
		HBox middle = new HBox(50, username, message);
		VBox middleV = new VBox(50, middle, connect);
		pane.setCenter(middleV);
		pane.setPadding(new Insets(50));
		Scene s1 = new Scene(pane,500,400);
		return s1;
	}
	
	public Scene createClientGui() {
		BorderPane pane = new BorderPane();
		vb = new VBox();
		Text Contacts = new Text("Friends");
		VBox contactV = new VBox(25, Contacts, vb);
		Text Chat = new Text("Message");
		VBox chatV = new VBox(20, Chat, sent);
		Text Message = new Text("Recieved");
		VBox messageV = new VBox(20, Message, recieved);
		HBox middle = new HBox(20, chatV, messageV);
		
		VBox right = new VBox(send);
		
		pane.setLeft(contactV);
		pane.setCenter(middle);
		pane.setRight(right);
		pane.setPadding(new Insets(50));
		Scene s1 = new Scene(pane, 800, 800);
		s1.getStylesheets().add("style2.css");
		return s1;
		
	}

}
