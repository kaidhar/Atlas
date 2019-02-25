package dataCheck;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ItemInfoController {

	ObservableList<String> EnvironmentValues = FXCollections.observableArrayList("QA2", "QA3","STG2","STG3");
	ObservableList<String> BannerValues = FXCollections.observableArrayList("LT", "BAY", "SAKS", "OFF5");
	ObservableList<TableDataItemInfo> resultData = FXCollections.observableArrayList();

	ObservableList<String> TypeValues = FXCollections.observableArrayList("ItemID/SKU", "Model/WebID", "UPC");

	@FXML
	private Button Search;

	@FXML
	private TextField ItemID;

	@FXML
	private ChoiceBox<String> Banner;

	@FXML
	private ChoiceBox<String> Type;

	@FXML
	private ChoiceBox<String> Environment;

	@FXML
	private Button Reset;
	
    @FXML
    private ImageView LogoView;

	@FXML
	private TableView<TableDataItemInfo> table;

	@FXML
	private TableColumn<TableDataItemInfo, String> itemidtable;

	@FXML
	private TableColumn<TableDataItemInfo, String> skucodetable;

	@FXML
	private TableColumn<TableDataItemInfo, String> modeltable;

	@FXML
	private TableColumn<TableDataItemInfo, String> upctable;
	
    @FXML
    private TableColumn<TableDataItemInfo, String> sizetable;

    @FXML
    private TableColumn<TableDataItemInfo, String> colortable;

	@FXML
	public void initialize() {
		Environment.setItems(EnvironmentValues);
		Banner.setItems(BannerValues);
		Type.setItems(TypeValues);

		itemidtable.setCellValueFactory(new PropertyValueFactory<TableDataItemInfo, String>("itemid"));
		//skucodetable.setCellValueFactory(new PropertyValueFactory<TableDataItemInfo, String>("skucode"));
		modeltable.setCellValueFactory(new PropertyValueFactory<TableDataItemInfo, String>("model"));
		upctable.setCellValueFactory(new PropertyValueFactory<TableDataItemInfo, String>("upc"));
		sizetable.setCellValueFactory(new PropertyValueFactory<TableDataItemInfo, String>("size"));
		colortable.setCellValueFactory(new PropertyValueFactory<TableDataItemInfo, String>("color"));
		
		String Path= ClassLoader.getSystemClassLoader().getResource(".").getPath() + "HBCLogo.png";
		File file = new File(Path);
        Image image = new Image(file.toURI().toString());
        LogoView.setImage(image);
		
		
	


		}	
	public ObservableList<TableDataItemInfo> getTableData(String itemidtable, String skucodetable, String modeltable, String upctable,String sizetable, String colortable) {
		ObservableList<TableDataItemInfo> tableData = FXCollections.observableArrayList();
		tableData.add(new TableDataItemInfo(itemidtable, skucodetable, modeltable, upctable,sizetable,colortable));
		return tableData;
	}

	@FXML
	void ResetAction(ActionEvent event) {

		ItemID.clear();
		Type.getSelectionModel().clearSelection();
		Banner.getSelectionModel().clearSelection();
		Environment.getSelectionModel().clearSelection();
		ObservableList<TableDataItemInfo> resultData = FXCollections.observableArrayList();
		resultData.add(new TableDataItemInfo("", "", "", "","",""));
		table.setItems(resultData);

	}

	@FXML
	void SearchAction(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {

		try {
			String ItemIDValue = ItemID.getText();
			String EnvironmentValue = Environment.getSelectionModel().getSelectedItem();
			String BannerValue = Banner.getSelectionModel().getSelectedItem();
			String TypeValue = Type.getSelectionModel().getSelectedItem();
			table.getSelectionModel().clearSelection();
			resultData.clear();
			String ItemIDResult = null;
			String ModelResult = null;
			String UPCResult = null;
			String Color_Code=null;
			String Size_Code=null;

			System.out.println(ItemIDValue);
			System.out.println(EnvironmentValue);

			DataValidator DBV = new DataValidator();

			DBV.openConnection(EnvironmentValue);
			List<String> results = new ArrayList<String>();
			List<String> resultsColorSize = new ArrayList<String>();
			HashMap<String, String> resultsColorSizeModel = new HashMap<String,String>();

			switch (TypeValue) {

			case "ItemID/SKU":
				
				results = DBV.getDBItemInfoItemID(EnvironmentValue,ItemIDValue);
				ItemIDResult = results.get(1);
				ModelResult = results.get(2);
				UPCResult = results.get(0);
				
				resultsColorSizeModel = DBV.getDBItemInfoColorSizeModel(EnvironmentValue,ItemIDResult);
				Color_Code = resultsColorSizeModel.get("Color");
				Size_Code = resultsColorSizeModel.get("Size");
				setUpdata(ItemIDResult,ModelResult,UPCResult,Size_Code,Color_Code);
				results.clear();
				
				break;

			case "Model/WebID":
				
				ResultSet resultModel =DBV.getDBItemInfoModel(EnvironmentValue,ItemIDValue);
				ResultSetMetaData rsmd = resultModel.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				
				while(resultModel.next()) {
					for (int i = 1; i <= columnsNumber; i++) {
						results.add(resultModel.getString(i));

					}
					ItemIDResult = results.get(1);
					ModelResult = results.get(2);
					UPCResult = results.get(0);
					resultsColorSizeModel = DBV.getDBItemInfoColorSizeModel(EnvironmentValue,ItemIDResult);
					Color_Code = resultsColorSizeModel.get("Color");
					Size_Code = resultsColorSizeModel.get("Size");
					setUpdata(ItemIDResult,ModelResult,UPCResult,Size_Code,Color_Code);

					results.clear();

				}

				
				break;

			case "UPC":
				results =DBV.getDBItemInfoUPC(EnvironmentValue,ItemIDValue);
				ItemIDResult = results.get(1);
				ModelResult = results.get(2);
				UPCResult = results.get(0);
				resultsColorSizeModel = DBV.getDBItemInfoColorSizeModel(EnvironmentValue,ItemIDResult);
				Color_Code = resultsColorSizeModel.get("Color");
				Size_Code = resultsColorSizeModel.get("Size");
				setUpdata(ItemIDResult,ModelResult,UPCResult,Size_Code,Color_Code);
				break;

			}
			table.getSelectionModel().setCellSelectionEnabled(true);
			table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			table.setItems(resultData);	
			results.clear();
			resultsColorSize.clear();
			
			DBV.closeConnection();
		} catch (Exception e) {
			
			ObservableList<TableDataItemInfo> resultData = FXCollections.observableArrayList();
			resultData.add(new TableDataItemInfo("", "", "", "","",""));
			table.setItems(resultData);
			resultData.clear();

		}
		
	}
	
	
	public void setUpdata(String ItemIDResult,String ModelResult,String UPCResult, String size_Code, String color_Code)
	{

	resultData.add(new TableDataItemInfo(ItemIDResult,ModelResult,ModelResult,UPCResult,size_Code,color_Code));
	
	
	//table.setItems(resultData);
	//TableUtils.installCopyPasteHandler(table);
	}



    @FXML
    void CopyMethod(MouseEvent event) {
    	String ItemValue = table.getSelectionModel().getSelectedItem().getItemid();
    	String ModelValue = table.getSelectionModel().getSelectedItem().getModel();
    	String UPCValue = table.getSelectionModel().getSelectedItem().getUpc();
    	
    	String Value = ItemValue+"\n"+ModelValue+"\n"+UPCValue;
    	TableUtils.CopyToClipBoard(Value);

    }
}
