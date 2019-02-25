package dataCheck;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DataCheckController {

	ObservableList<String> EnvironmentValues = FXCollections.observableArrayList("QA2", "QA3", "STG2", "STG3");
	ObservableList<String> BannerValues = FXCollections.observableArrayList("LT", "BAY", "SAKS", "OFF5");

	@FXML
	private TextField ItemID;

	@FXML
	private TextField OMSAdjust;

	@FXML
	private TextField WebAdjust;

	@FXML
	private TextField ShipNodeForInventory;

	@FXML
	private ChoiceBox<String> Environment;

	@FXML
	private ChoiceBox<String> Banner;

	@FXML
	private ImageView LogoView;

	@FXML
	private Label OMSDONE;

	@FXML
	private Label WEBDONE;

	@FXML
	void openFAQURL(MouseEvent event) throws IOException, URISyntaxException {

		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop()
					.browse(new URI("https://hbcdigital.atlassian.net/wiki/spaces/SQA/pages/428146703/ATLAS"));
		}

	}

	@FXML
	void ResetAction(ActionEvent event) {

		ObservableList<TableData> resultData = FXCollections.observableArrayList();
		Banner.getSelectionModel().clearSelection();
		Environment.getSelectionModel().clearSelection();
		ShipNodeForInventory.clear();
		WebAdjust.clear();
		ItemID.clear();
		OMSAdjust.clear();
		OMSDONE.setText("");
		WEBDONE.setText("");
		resultData.add(new TableData("", "", "", "", "", "", "", "", "", "", "", ""));
		table.setItems(resultData);
	}

	@FXML
	public void initialize() throws IOException {
		Environment.setItems(EnvironmentValues);
		Banner.setItems(BannerValues);
		OMSInventoryValue.setCellValueFactory(new PropertyValueFactory<TableData, String>("OMSData"));
		DCSTORE.setCellValueFactory(new PropertyValueFactory<TableData, String>("DCStore"));
		BOPIS.setCellValueFactory(new PropertyValueFactory<TableData, String>("BOPIS"));
		WEB.setCellValueFactory(new PropertyValueFactory<TableData, String>("WEBDATA"));
		OnHand.setCellValueFactory(new PropertyValueFactory<TableData, String>("OnHandDATA"));

		DCAvailable.setCellValueFactory(new PropertyValueFactory<TableData, String>("DCAvailable"));
		StoreAvailable.setCellValueFactory(new PropertyValueFactory<TableData, String>("StoreAvailable"));
		VendorAvailable.setCellValueFactory(new PropertyValueFactory<TableData, String>("VendorAvailable"));

		AttributeSize.setCellValueFactory(new PropertyValueFactory<TableData, String>("AttributeSize"));
		AttributeColor.setCellValueFactory(new PropertyValueFactory<TableData, String>("AttributeColor"));
		AttributeModel.setCellValueFactory(new PropertyValueFactory<TableData, String>("AttributeModel"));
		AttributeStatus.setCellValueFactory(new PropertyValueFactory<TableData, String>("AttributeStatus"));

		String Path = ClassLoader.getSystemClassLoader().getResource(".").getPath() + "HBCLogo.png";
		File file = new File(Path);
		Image image = new Image(file.toURI().toString());
		LogoView.setImage(image);

	}

	public ObservableList<TableData> getTableData(String OMSdata, String BOPIS, String DCStore, String WebData,
			String OnHandDATA, String DCAvailable, String StoreAvailable, String VendorAvailable, String AttributeSize,
			String AttributeColor, String AttributeModel, String AttributeStatus) {
		ObservableList<TableData> tableData = FXCollections.observableArrayList();
		tableData.add(new TableData(OMSdata, BOPIS, DCStore, WebData, OnHandDATA, DCAvailable, StoreAvailable,
				VendorAvailable, AttributeSize, AttributeColor, AttributeModel, AttributeStatus));
		return tableData;

	}

	@FXML
	private Button checkInventory;

	@FXML
	private Button adjustInventory;

	@FXML
	private Button Reset;

	@FXML
	private MenuItem ItemPick;

	@FXML
	private MenuItem ItemInfo;

	@FXML
	private MenuItem orderDetails;

	@FXML
	private MenuItem setCredentials;

	@FXML
	void ItemPickView(ActionEvent event) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ItemView.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();
		Stage stage = new Stage();
		stage.initOwner(stage.getOwner());
		stage.setScene(new Scene(root1));
		stage.setTitle("Atlas V3.0");
		stage.show();

	}

	@FXML
	void ItemInfoView(ActionEvent event) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ItemInfo.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();
		Stage stage = new Stage();
		stage.initOwner(stage.getOwner());
		stage.setScene(new Scene(root1));
		stage.setTitle("Atlas V3.0");
		stage.show();

	}

	@FXML
	void CredentialsView(ActionEvent event) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Credentials.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();
		Stage stage = new Stage();
		stage.initOwner(stage.getOwner());
		stage.setScene(new Scene(root1));
		stage.setTitle("Atlas V3.0");
		stage.show();

	}

	@FXML
	void getOrderDetailsView(ActionEvent event) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OrderDetails.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();
		Stage stage = new Stage();
		stage.initOwner(stage.getOwner());
		stage.setScene(new Scene(root1));
		stage.setTitle("Atlas V3.0");
		stage.show();

	}

	@FXML
	private Button adjustWeb;

	/*
	 * Description: This method has been written to update the OMS Inventory
	 * Author:Kartikay Dhar Date: 08/08/2018
	 * 
	 * 
	 * 
	 * 
	 */

	@FXML
	void adjustInventory(ActionEvent event)
			throws SQLException, ClassNotFoundException, IOException, KeyManagementException, NoSuchAlgorithmException {

		String ItemIDValue = ItemID.getText();
		String EnvironmentValue = Environment.getSelectionModel().getSelectedItem();
		String OMSAdjustValue = OMSAdjust.getText();
		String ShipNodeForInventoryValue = ShipNodeForInventory.getText();
		String Bannervalue = Banner.getSelectionModel().getSelectedItem();

		System.out.println(ItemIDValue);
		System.out.println(EnvironmentValue);

		DataValidator DBV = new DataValidator();

		DBV.openConnection(EnvironmentValue);

		DBV.getDBResults(EnvironmentValue, ItemIDValue, OMSAdjustValue, ShipNodeForInventoryValue);
		DBV.closeConnection();

		InventoryWebServices IWS = new InventoryWebServices();
		IWS.UpdateInventory(ItemIDValue, Bannervalue, ShipNodeForInventoryValue, OMSAdjustValue, EnvironmentValue);
		IWS.UpdateMonitor(ItemIDValue, Bannervalue, EnvironmentValue);

		WEBDONE.setText("");
		OMSDONE.setText("DONE!");
		DBV.closeConnection();

	}

	/*
	 * Description: This method has been written to update the Web Inventory
	 * Author:Kartikay Dhar Date: 08/08/2018
	 * 
	 * 
	 * 
	 * 
	 */
	// TODO
	@FXML
	void adjustWeb(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {

		String ItemIDValue = ItemID.getText();
		String EnvironmentValue = Environment.getSelectionModel().getSelectedItem();
		String WebAdjustValue = WebAdjust.getText();
		String BannerValue = Banner.getSelectionModel().getSelectedItem();

		DataValidator DBV = new DataValidator();
		DBV.openConnection(EnvironmentValue);
		ResultSet rsWeb = DBV.getDBResultsWebValue(EnvironmentValue, ItemIDValue);

		ResultSetMetaData rsmdWebValue = rsWeb.getMetaData();
		int columnsNumberWebValue = rsmdWebValue.getColumnCount();
		List<String> WebValue = new ArrayList<String>();

		while (rsWeb.next()) {
			for (int i = 1; i <= columnsNumberWebValue; i++) {
				WebValue.add(rsWeb.getString(i));

			}

		}

		String UPCValue = WebValue.get(0);
		DBV.closeConnection();
		DBV.getDBResultsWeb(EnvironmentValue, BannerValue, ItemIDValue, WebAdjustValue, UPCValue);
		OMSDONE.setText("");
		WEBDONE.setText("DONE!");
	}

	@FXML
	private TableView<TableData> table;

	@FXML
	private TableColumn<TableData, String> OMSInventoryValue;

	@FXML
	private TableColumn<TableData, String> DCSTORE;

	@FXML
	private TableColumn<TableData, String> BOPIS;

	@FXML
	private TableColumn<TableData, String> DCAvailable;

	@FXML
	private TableColumn<TableData, String> StoreAvailable;

	@FXML
	private TableColumn<TableData, String> VendorAvailable;

	@FXML
	private TableColumn<TableData, String> WEB;

	@FXML
	private TableColumn<TableData, String> OnHand;

	@FXML
	private TableColumn<TableData, String> AttributeSize;

	@FXML
	private TableColumn<TableData, String> AttributeColor;

	@FXML
	private TableColumn<TableData, String> AttributeModel;

	@FXML
	private TableColumn<TableData, String> AttributeStatus;

	@FXML
	private Hyperlink FAQ;

	/*
	 * Description: This method has been written to Check and Display the OMS and
	 * Web Inventory. Author: Kartikay Dhar Date: 08/08/2018
	 * 
	 * 
	 * 
	 * 
	 */

	@FXML
	void checkInventory(ActionEvent event) throws ClassNotFoundException, SQLException, IOException, JSchException {

		try {
			String ItemIDValue = ItemID.getText();
			String EnvironmentValue = Environment.getSelectionModel().getSelectedItem();
			String BannerValue = Banner.getSelectionModel().getSelectedItem();
			System.out.println(ItemIDValue);
			System.out.println(EnvironmentValue);
			OMSDONE.setText("");
			WEBDONE.setText("");

			if (BannerValue == "OFF") {
				BannerValue = "OFF5";

			}

			DataValidator DBV = new DataValidator();

			DBV.openConnection(EnvironmentValue);

			ResultSet rs = DBV.getDBResultsOMS(EnvironmentValue, ItemIDValue, BannerValue);

			ResultSet rsBOPIS = DBV.getDBResultsBOPIS(EnvironmentValue, ItemIDValue, BannerValue);

			ResultSet rsWeb = DBV.getDBResultsWebValue(EnvironmentValue, ItemIDValue);

			ResultSet rsDirty = DBV.getDBResultsDirtyNode(EnvironmentValue, ItemIDValue, BannerValue);

			ResultSetMetaData rsmdWebValue = rsWeb.getMetaData();
			int columnsNumberWebValue = rsmdWebValue.getColumnCount();
			List<String> WebValue = new ArrayList<String>();

			while (rsWeb.next()) {
				for (int i = 1; i <= columnsNumberWebValue; i++) {
					WebValue.add(rsWeb.getString(i));

				}

			}

			ResultSetMetaData rsmdBOPIS = rsBOPIS.getMetaData();
			int columnsNumberBOPIS = rsmdBOPIS.getColumnCount();
			List<String> resultsBOPIS = new ArrayList<String>();

			while (rsBOPIS.next()) {
				for (int i = 1; i <= columnsNumberBOPIS; i++) {
					resultsBOPIS.add(rsBOPIS.getString(i));

				}

			}

			/*
			 * 
			 * Description: Method to make a call to web DB and retrieve the Onhand and
			 * sellable quantity Author: Kartikay Dhar Date: 08/08/2018
			 * 
			 * 
			 * 
			 * 
			 * 
			 */
			String ItemKey = WebValue.get(0);
			ResultSet rsWebDB;
			List<String> resultsBanner = new ArrayList<String>();

			switch (BannerValue) {

			case "LT":

				DBV.openConnectionWebDB(EnvironmentValue, BannerValue);
				rsWebDB = DBV.getDBResultsLT(ItemKey);

				ResultSetMetaData rsmdLT = rsWebDB.getMetaData();
				int columnsNumberLT = rsmdLT.getColumnCount();

				while (rsWebDB.next()) {
					for (int i = 1; i <= columnsNumberLT; i++) {
						resultsBanner.add(rsWebDB.getString(i));

					}

				}

				DBV.closeConnectionWeb();
				break; // optional

			case "BAY":

				if (EnvironmentValue.equals("QA3")) {
					resultsBanner.add("Pending");
					resultsBanner.add("Pending");
				}

				else {
					try {
						DBV.openConnectionWebDB(EnvironmentValue, BannerValue);
					} catch (Exception e1) {

						System.out.println(e1.toString());
						resultsBanner.add("Okta");
						resultsBanner.add("Connect");
						break;

					}
					rsWebDB = DBV.getDBResultsBAY(ItemIDValue);

					rsWebDB.beforeFirst();

					while (rsWebDB.next()) {

						for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
							resultsBanner.add(rsWebDB.getString(i));

						}

					}

					DBV.closeConnectionWeb();
				}

				break;

			case "SAKS":

				DBV.openConnectionWebDB(EnvironmentValue, BannerValue);
				rsWebDB = DBV.getDBResultsSAKS(ItemIDValue);

				ResultSetMetaData rsmdSAKS = rsWebDB.getMetaData();
				int columnsNumberSAKS = rsmdSAKS.getColumnCount();

				while (rsWebDB.next()) {
					for (int i = 1; i <= columnsNumberSAKS; i++) {
						resultsBanner.add(rsWebDB.getString(i));

					}

				}

				DBV.closeConnectionWeb();
				break; // optional

			case "OFF5":

				try {
					DBV.openConnectionWebDB(EnvironmentValue, BannerValue);
				} catch (Exception e1) {

					System.out.println(e1.toString());
					resultsBanner.add("AnyConnect");
					resultsBanner.add("Connect");
					break;

				}

				rsWebDB = DBV.getDBResultsOFF5(ItemIDValue);

				rsWebDB.beforeFirst();

				while (rsWebDB.next()) {

					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						resultsBanner.add(rsWebDB.getString(i));

					}

				}

				DBV.closeConnectionWeb();
				break;

			default:
				resultsBanner.add("pending");
				resultsBanner.add("pending");

			}

			/*
			 * 
			 * Description: Method to Update Atlas's Table. Author: Kartikay Dhar Date:
			 * 08/08/2018
			 * 
			 * 
			 * 
			 * 
			 * 
			 */

			List<String> results = new ArrayList<String>();
			String OMSInv = null;
			String DCStore = null;
			String BOPIS = null;
			String WH_Quantity = null;
			String ON_Hand_Quantity = null;
			String DCAvailable = null;
			String StoreAvailable = null;
			String VendorAvailable = null;
			String Status = null;
			String Dirty = null;
			List<String> resultsDirty = new ArrayList<String>();

			ObservableList<TableData> resultData = FXCollections.observableArrayList();

			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					results.add(rs.getString(i));

				}
				OMSInv = results.get(0);
				DCStore = results.get(1).trim();
				BOPIS = resultsBOPIS.get(0);
				DCAvailable = resultsBOPIS.get(1);
				StoreAvailable = resultsBOPIS.get(2);
				VendorAvailable = resultsBOPIS.get(3);
				Status = resultsBOPIS.get(4);

				while (rsDirty.next()) {

					resultsDirty.add(rsDirty.getString(1).trim());

				}

				for (String node : resultsDirty)

				{

					if (DCStore.equals(node)) {

						Dirty = "Y";

					} else {
						Dirty = "N";
					}

				}

				String ItemIDResult = null;
				String ModelResult = null;
				String UPCResult = null;
				String Color_Code = null;
				String Size_Code = null;
				HashMap<String, String> resultsColorSizeModel = new HashMap<String, String>();

				results = DBV.getDBItemInfoItemID(EnvironmentValue, ItemIDValue);
				ItemIDResult = results.get(1);
				ModelResult = results.get(2);
				UPCResult = results.get(0);

				resultsColorSizeModel = DBV.getDBItemInfoColorSizeModel(EnvironmentValue, ItemIDResult);
				Color_Code = resultsColorSizeModel.get("Color");
				Size_Code = resultsColorSizeModel.get("Size");

				WH_Quantity = resultsBanner.get(1);
				ON_Hand_Quantity = resultsBanner.get(0);

				resultData.add(new TableData(OMSInv, DCStore, BOPIS, WH_Quantity, ON_Hand_Quantity, Dirty, StoreAvailable,
						VendorAvailable, Size_Code, Color_Code, ModelResult, Status));

				results.clear();

			}
			table.getSelectionModel().setCellSelectionEnabled(true);
			table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			table.setItems(resultData);
		} catch (Exception e) {
			
			ObservableList<TableData> resultData = FXCollections.observableArrayList();
			resultData.add(new TableData("", "", "", "", "", "", "", "", "", "", "", ""));
			table.setItems(resultData);
			resultData.clear();

		}

	}

	@FXML
	void CopyMethod(MouseEvent event) {
		String NodeValue = table.getSelectionModel().getSelectedItem().getDCStore();

		String Value = NodeValue.trim();
		TableUtils.CopyToClipBoard(Value);

	}
}
