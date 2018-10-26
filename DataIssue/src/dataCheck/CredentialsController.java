package dataCheck;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CredentialsController {

	@FXML
	private TextField UserName;

	@FXML
	private Button Submit;

	@FXML
	private TextField Password;

	@FXML
	void updateCredentials(ActionEvent event) throws IOException {

		String User = UserName.getText();
		String PasswordValue = Password.getText();
		setProperty(User,PasswordValue);

		
		
		Stage stage = (Stage) Submit.getScene().getWindow();
	    // do what you have to do
	    stage.close();
	    
	}

	void setProperty(String User, String Password) throws IOException {

		Properties prop = new Properties();
		//String Path=this.getClass().getResource("Credentials.properties").toString();
		String Path = "./Credentials.properties";

		String finalPath = Path.substring(5);
		System.out.println(finalPath);
		File file = new File(Path);

		FileOutputStream Output = new FileOutputStream(file);

		prop.setProperty("UserCredentials", User);
		prop.setProperty("PWDCredentials", Password);
		prop.store(Output, null);
		Output.close();


	}
}
