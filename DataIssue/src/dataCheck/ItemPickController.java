package dataCheck;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jcraft.jsch.JSchException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ItemPickController {
	
    ObservableList<String> EnvironmentValues = FXCollections.observableArrayList("QA2", "QA3","STG2","STG3");
	ObservableList<String> BannerValues = FXCollections.observableArrayList("LT", "BAY", "SAKS", "OFF5");

    	@FXML
    	private MenuItem ItemUpdater;

	    @FXML
	    private TextField Quantity;

	    @FXML
	    private ChoiceBox<String> Banner;
	    
	    @FXML
	    private ChoiceBox<String> EnvironmentVal;

	    @FXML
	    private ImageView LogoView;

	    @FXML
	    private Button Check;

	    @FXML
	    private TextField Status;

	    @FXML
	    private TextField ItemID;
	    
	    @FXML
	    private ImageView imageView;
	    
	    @FXML
	    private Label StatusLabel;
	    
	    
	    @FXML
	    void ItemUpdaterCall(ActionEvent event) {

	    }

	    

	    @FXML
	    private Button Reset;

	    @FXML
	    void ResetButton(ActionEvent event) {

	    	StatusLabel.setText("");
	    	ItemID.clear();
	    	Quantity.clear();
	    	Banner.getSelectionModel().clearSelection();
	    	EnvironmentVal.getSelectionModel().clearSelection();
	    	
	    	
	    }

	    
	    
	    @FXML
		public void initialize() {
	    	
			Banner.setItems(BannerValues);
			EnvironmentVal.setItems(EnvironmentValues);
			
			String Path= ClassLoader.getSystemClassLoader().getResource(".").getPath() + "HBCLogo.png";
			//System.out.println(Path);
			File file = new File(Path);
	        Image image = new Image(file.toURI().toString());
	        LogoView.setImage(image);
			

		}
	    
	    

	    @FXML
	    void setQuantity(ActionEvent event) throws ClassNotFoundException, SQLException, IOException, JSchException {

	    String ItemIDValue = ItemID.getText();
	    String QuantityValue=Quantity.getText();
	    String EnvironmentValue = EnvironmentVal.getSelectionModel().getSelectedItem();
	    String BannerValue= Banner.getSelectionModel().getSelectedItem();
		DataValidator DBV = new DataValidator();
		DBV.openConnection(EnvironmentValue);
	    String UPC = DBV.getDBResultsUPC(ItemIDValue,EnvironmentValue);
	    DBV.closeConnection();
	    
	    MongoWebDB MWD = new MongoWebDB();
	    MWD.updateMongo(UPC,QuantityValue,BannerValue,EnvironmentValue);
	    
	    //imageView.setImage(new Image(this.getClass().getResourceAsStream("Done.png")));
	    //Status.setText("          Completed");
	    
	    StatusLabel.setText("Completed");
	    	
	    }

	}


	