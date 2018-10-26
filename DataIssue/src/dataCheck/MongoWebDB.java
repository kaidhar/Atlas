package dataCheck;

import org.bson.Document;
import org.testng.annotations.Test;

import com.jcraft.jsch.JSchException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongoWebDB {

	@Test
	public void updateMongo(String UPC, String Quantity, String Banner,String Environment) throws JSchException, IOException {

		String User = null;
		String Password =null;
		String Host = getProperty("HostMongo"+Environment);
		System.out.println("");
		String Database = getProperty("Database"+Environment+Banner);
		
		MongoClientURI uri = null;
		String EnvFlag=null;
		
		if(Environment=="QA2"||Environment=="QA3")
		{
			 User = getProperty("UserMongo"+Environment);
			 Password = getProperty("PasswordMongo"+Environment);
			EnvFlag="QA";
		}
		else
		{
			
			EnvFlag="STG";
			 User = getProperty("UserMongo"+Banner+Environment);
			 Password = getProperty("PasswordMongo"+Environment);
			
			
		}
		
		switch(EnvFlag) {
		
		case "QA" :
			
			switch (Banner) {

			case "LT":
			uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27019/"+Database);
			break;
			
			case "BAY":
				
				uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27019/"+Database);
				break;
				
			case "SAKS":
				 uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27019/"+Database);
				 
				// uri = new MongoClientURI("mongodb://msltqa:msltqa321@hd5qmdb02lx.digital.hbc.com:27019/s5a_slot05_saks_services");
				break;
				
			case "OFF5":
				uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27019/"+Database);
				break;
				
			}
		
			break;
			
		case "STG" :
			
			switch (Banner) {

			case "LT":
			uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27018/"+Database);
			break;
			
			case "BAY":
				
				uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27018/"+Database);
				break;
				
			case "SAKS":
				 uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27018/"+Database);
				 
				// uri = new MongoClientURI("mongodb://msltqa:msltqa321@hd5qmdb02lx.digital.hbc.com:27019/s5a_slot05_saks_services");
				break;
				
			case "OFF5":
				uri = new MongoClientURI("mongodb://"+User+":"+Password+"@"+Host+":27018/"+Database);
				break;
				
			}
			
			break;
		
		
		}
			

		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase db = mongoClient.getDatabase(uri.getDatabase());
		MongoCollection<Document> inventory = db.getCollection("inventory");
		int QuantityValue = Integer.parseInt(Quantity);
		
		Document myDoc = inventory.find(eq("UPC_CODE", UPC)).first();
		System.out.println(myDoc.toJson());

		Document updateQuery = new Document("UPC_CODE", UPC);
		inventory.updateOne(updateQuery, new Document("$set", new Document("STORE_ON_HAND", QuantityValue)));
		inventory.updateOne(updateQuery, new Document("$set", new Document("ON_HAND_QTY", QuantityValue)));
		inventory.updateOne(updateQuery, new Document("$set", new Document("ON_ORDER_QTY", QuantityValue)));
		inventory.updateOne(updateQuery, new Document("$set", new Document("FIND_IN_STORE_ON_HAND", QuantityValue)));

		
		myDoc = inventory.find(eq("UPC_CODE", UPC)).first();
		System.out.println(myDoc.toJson());
		
		
		mongoClient.close();

	}

	
	String getProperty(String Prop) throws IOException {

		Properties prop = new Properties();
		InputStream input = null;

		// String Path = getLocation();
		// input = new FileInputStream(Path);
		input = this.getClass().getResourceAsStream("Keys.properties");
		// FileInputStream("//Users//h895458//Desktop//Workspace//DataIssue//src//dataCheck//Keys.properties");
		prop.load(input);
		String Value = prop.getProperty(Prop);
		return Value;

}
}