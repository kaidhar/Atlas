package dataCheck;

import java.io.File;
import java.io.IOException;
import org.jdom2.JDOMException;
import org.json.JSONException;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

public class OrderDetailsController {

	@FXML
	private ChoiceBox<String> BannerValue;

	@FXML
	private ChoiceBox<String> EnvironmentValue;

	@FXML
	private TextField OrderID;

	@FXML
	private Button findOrder;

	@FXML
	private Button ResetButton;

	@FXML
	private ImageView OrderLogo;

	@FXML
	private TableView<TableOrderDetails> OrderDetailTable;

	@FXML
	private TableColumn<TableOrderDetails, String> AttributeName;

	@FXML
	private TableColumn<TableOrderDetails, String> AttributeValue;

	ObservableList<String> EnvironmentValues = FXCollections.observableArrayList("QA2", "QA3", "STG2", "STG3");
	ObservableList<String> BannerValues = FXCollections.observableArrayList("LT", "BAY", "SAKS", "OFF5");
	ObservableList<TableOrderDetails> resultData = FXCollections.observableArrayList();
	static Multimap<String, String> Attributes = HashMultimap.create();

	@FXML
	public void initialize() {
		EnvironmentValue.setItems(EnvironmentValues);
		BannerValue.setItems(BannerValues);

		AttributeName.setCellValueFactory(new PropertyValueFactory<TableOrderDetails, String>("AttributeName"));
		AttributeValue.setCellValueFactory(new PropertyValueFactory<TableOrderDetails, String>("AttributeValue"));

		String Path = ClassLoader.getSystemClassLoader().getResource(".").getPath() + "HBCLogo.png";
		// System.out.println(Path);
		File file = new File(Path);
		Image image = new Image(file.toURI().toString());
		OrderLogo.setImage(image);

	}

	@FXML
	void ResetFields(ActionEvent event) {

		OrderID.clear();
		BannerValue.getSelectionModel().clearSelection();
		EnvironmentValue.getSelectionModel().clearSelection();

		ObservableList<TableOrderDetails> resultData = FXCollections.observableArrayList();
		resultData.add(new TableOrderDetails("", ""));
		OrderDetailTable.setItems(resultData);
		resultData.clear();
		Attributes.clear();

	}

	@FXML
	void getOrderDetails(ActionEvent event) throws IOException, JSONException, JDOMException {

		try {
			Attributes.clear();
			resultData.clear();

			String OrderIDValue = OrderID.getText();
			String Environment = EnvironmentValue.getSelectionModel().getSelectedItem();
			String Banner = BannerValue.getSelectionModel().getSelectedItem();
			InventoryWebServices IWS = new InventoryWebServices();
			Attributes = IWS.getOrderDetails(OrderIDValue, Environment, Banner);

			for (String name : Attributes.keySet()) {

				String key = name.toString();
				String value = Attributes.get(name).toString();

				if (value.isEmpty()) {
					continue;

				}
				setUpdata(key, value);

			}

			OrderDetailTable.getSelectionModel().setCellSelectionEnabled(true);
			OrderDetailTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			OrderDetailTable.setItems(resultData);
		} catch (Exception e) {

			ObservableList<TableOrderDetails> resultData = FXCollections.observableArrayList();
			resultData.add(new TableOrderDetails("", ""));
			OrderDetailTable.setItems(resultData);
			Attributes.clear();
			resultData.clear();

		}

	}

	public void setUpdata(String AttributeName, String AttributeValue) {

		resultData.add(new TableOrderDetails(AttributeName, AttributeValue));

	}

	@FXML
	void CopyMethod(MouseEvent event) {
		StringBuilder FinalString = new StringBuilder();

		String Value = OrderDetailTable.getSelectionModel().getSelectedItem().getAttributeValue();

		char[] ValueArray = Value.toCharArray();

		for (int i = 0; i <= ValueArray.length - 1; i++) {
			if (ValueArray[i] == '[' || ValueArray[i] == ']'||ValueArray[i]==',') {
				continue;
			}
			FinalString = FinalString.append(ValueArray[i]);
		}

		String Finalvalue = FinalString.toString();

		TableUtils.CopyToClipBoard(Finalvalue);

	}

}
