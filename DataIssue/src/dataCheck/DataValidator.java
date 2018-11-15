package dataCheck;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.testng.annotations.Test;

public class DataValidator {

	public static Connection con;
	public static Connection conWeb;

	@Test
	public void openConnection(String Environment) throws ClassNotFoundException, SQLException, IOException {

		Class.forName("oracle.jdbc.OracleDriver");
		String User = getProperty("User" + Environment);
		String Password = getProperty("PWD" + Environment);
		String Host = getProperty("Host" + Environment);
		String SID = getProperty("SID" + Environment);

		con = DriverManager.getConnection("jdbc:oracle:thin:@" + Host + ":1521/" + SID, User, Password);

	}

	public void openConnectionWebDB(String Environment,String Banner) throws ClassNotFoundException, SQLException, IOException {

		Class.forName("oracle.jdbc.OracleDriver");
		String User = getProperty("User" +Environment+ Banner);
		String Password = getProperty("PWD" +Environment+ Banner);
		String Host = getProperty("Host" +Environment+ Banner);
		String SID = getProperty("SID" +Environment+ Banner);

		conWeb = DriverManager.getConnection("jdbc:oracle:thin:@" + Host + ":1521/" + SID, User, Password);

	}

	public ResultSet getDBResultsOMS(String Environment, String ItemID,String BannerValue) throws SQLException, IOException {

		Statement stmt = con.createStatement();
		String Query = "select Quantity,Shipnode_key from OMS" + Environment
				+ ".yfs_inventory_supply where inventory_item_key in ((Select inventory_item_key from OMS" + Environment
				+ ".YFS_INVENTORY_ITEM where item_id='" + ItemID + "'  and UOM='EACH' and organization_code='"+BannerValue+"')) and SUPPLY_TYPE='ONHAND'";
		ResultSet rs = stmt.executeQuery(Query);

		return rs;

	}

	
	// IS_SHIPPING_ALLOWED Doesn't work for DC
	public ResultSet getDBResultsBOPIS(String Environment, String ItemID,String BannerValue) throws SQLException, IOException {

		Statement stmt = con.createStatement();				
		String Query = "select is_Pickup_Allowed,IS_SHIPPING_ALLOWED,EXTN_STORE_SHIP_ALLOWED,EXTN_IS_DSV_ENABLED,Status from OMS" + Environment + ".yfs_Item where item_id='" + 
		ItemID + "'  and UOM='EACH' and organization_code='"+BannerValue+"'";
		ResultSet rs = stmt.executeQuery(Query);

		return rs;

	}
	
	
	public ResultSet getDBResultsDirtyNode(String Environment, String ItemID,String BannerValue) throws SQLException, IOException {

		Statement stmt = con.createStatement();				
		String Query = "select node_key from OMS" + Environment + ".yfs_inventory_node_control where item_id='" + 
		ItemID + "'  and UOM='EACH' and organization_code='"+BannerValue+"'";
		ResultSet rs = stmt.executeQuery(Query);

		return rs;

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

	public void closeConnection() throws SQLException {

		con.close();
		// TODO Auto-generated method stub

	}

	public void closeConnectionWeb() throws SQLException {

		conWeb.close();
		// TODO Auto-generated method stub

	}

	public void getDBResults(String environmentValue, String itemIDValue, String oMSAdjustValue,
			String shipNodeForInventoryValue) throws SQLException, IOException, ClassNotFoundException {

		ResultSet tempData = getDBResults(environmentValue, itemIDValue, shipNodeForInventoryValue);
		List<String> results = new ArrayList<String>();
		ResultSetMetaData rsmd = tempData.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		int updateValue = 0;
		String tempValue = null;
		while (tempData.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				results.add(tempData.getString(i));

			}

			String OMSInv = results.get(0);

			int OMSInvInteger = Integer.parseInt(OMSInv);

			int OMSAdjustInteger = Integer.parseInt(oMSAdjustValue);
			updateValue = OMSInvInteger + OMSAdjustInteger;
			tempValue = String.valueOf(updateValue);

		}
//		String Query = "UPDATE OMS" + environmentValue + ".yfs_inventory_supply SET Quantity = '" + tempValue
//				+ "' WHERE inventory_item_key in (Select inventory_item_key from OMS" + environmentValue
//				+ ".YFS_INVENTORY_ITEM where item_id='" + itemIDValue + "' and UOM='EACH') and Shipnode_key='"
//				+ shipNodeForInventoryValue + "'";
//
//		PreparedStatement preparedStmt = con.prepareStatement(Query);
//
//		preparedStmt.executeUpdate();
		
		String Query2 = "update oms"+environmentValue+".yfs_item set onhand_safety_factor_qty='0',extn_aggregate_safety_factor = '0' where item_id = '"+itemIDValue+"' and UOM='EACH'";
		PreparedStatement preparedStmt = con.prepareStatement(Query2);
		preparedStmt.executeUpdate();
		
		
		String Query3 = "update oms"+environmentValue+".YFS_SKU_SAFETY_FACTOR_DEFN "
				+ ""
				+ "set ONHAND_SAFETY_FACTOR_QTY='0' "
				+ "where PARENT_KEY in (select ITEM_KEY from OMS"+environmentValue+".yfs_Item where item_id = '"+itemIDValue+"'  and UOM='EACH')";
		preparedStmt = con.prepareStatement(Query3);
		preparedStmt.executeUpdate();
		con.commit();

	}

	public void getDBResultsWeb(String environmentValue, String BannerValue, String itemIDValue, String webAdjustValue, String uPCValue)
			throws ClassNotFoundException, SQLException, IOException {

		int finalQuantity1;
		int finalQuantity2;
		int webAdjustValueWeb = Integer.parseInt(webAdjustValue);
		
		Statement stmt =  null;
		String firstQuery=null;
		int StoreQTY=0;
		int OnhandQTY=0;
		String Query = null;
		
		
		
		

		switch (BannerValue) {

		case "SAKS":
			
			openConnectionWebDB(environmentValue,BannerValue);
			
			stmt = conWeb.createStatement();
			 firstQuery = "select STORE_QTY,IN_STOCK_SELLABLE_QTY from saks_custom.inventory where SKU_CODE='"
					+ itemIDValue + "'";
			ResultSet rsSAKS = stmt.executeQuery(firstQuery);

			List<String> resultsSAKS = new ArrayList<String>();
			ResultSetMetaData rsmdSAKS = rsSAKS.getMetaData();
			int columnsNumber = rsmdSAKS.getColumnCount();

			while (rsSAKS.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					resultsSAKS.add(rsSAKS.getString(i));

				}
			}

			StoreQTY = Integer.parseInt(resultsSAKS.get(0));
			OnhandQTY = Integer.parseInt(resultsSAKS.get(1));

			finalQuantity1 = StoreQTY + webAdjustValueWeb;
			finalQuantity2 = OnhandQTY + webAdjustValueWeb;

			 Query = "Update saks_custom.inventory SET STORE_QTY = '" + finalQuantity1
					+ "' , IN_STOCK_SELLABLE_QTY = '" + finalQuantity2 + "' where SKU_CODE='" + itemIDValue + "'";

			PreparedStatement preparedStmtSAKS = conWeb.prepareStatement(Query);

			preparedStmtSAKS.executeUpdate();

			closeConnectionWeb();
			break; // optional
			
			
		case "BAY":
			
			openConnectionWebDB(environmentValue,BannerValue);
			
			stmt = conWeb.createStatement();
			 firstQuery = "select STORE_QTY,IN_STOCK_SELLABLE_QTY from saks_custom.inventory where SKU_CODE='"
					+ itemIDValue + "'";
			ResultSet rsBAY = stmt.executeQuery(firstQuery);

			List<String> resultsBAY = new ArrayList<String>();
			ResultSetMetaData rsmdBAY = rsBAY.getMetaData();
			columnsNumber = rsmdBAY.getColumnCount();

			while (rsBAY.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					resultsBAY.add(rsBAY.getString(i));

				}
			}

			StoreQTY = Integer.parseInt(resultsBAY.get(0));
			OnhandQTY = Integer.parseInt(resultsBAY.get(1));

			finalQuantity1 = StoreQTY + webAdjustValueWeb;
			finalQuantity2 = OnhandQTY + webAdjustValueWeb;

			 Query = "Update saks_custom.inventory SET STORE_QTY = '" + finalQuantity1
					+ "' , IN_STOCK_SELLABLE_QTY = '" + finalQuantity2 + "' where SKU_CODE='" + itemIDValue + "'";

			PreparedStatement preparedStmtBAY = conWeb.prepareStatement(Query);

			preparedStmtBAY.executeUpdate();

			closeConnectionWeb();
			break; // optional
			
			
		case "OFF5":
			
			
			openConnectionWebDB(environmentValue,BannerValue);
			
			stmt = conWeb.createStatement();
			 firstQuery = "select WH_Sellable_qty, On_Hand_QTY from SAKS_CUSTOM.sku_inventory where sku_id='"+ uPCValue + "'";
			ResultSet rsOFF5 = stmt.executeQuery(firstQuery);

			List<String> resultsOFF5 = new ArrayList<String>();
			ResultSetMetaData rsmdOFF5 = rsOFF5.getMetaData();
			columnsNumber = rsmdOFF5.getColumnCount();

			while (rsOFF5.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					resultsOFF5.add(rsOFF5.getString(i));

				}
			}

			StoreQTY = Integer.parseInt(resultsOFF5.get(0));
			OnhandQTY = Integer.parseInt(resultsOFF5.get(1));

			finalQuantity1 = StoreQTY + webAdjustValueWeb;
			finalQuantity2 = OnhandQTY + webAdjustValueWeb;

			 Query = "Update SAKS_CUSTOM.sku_inventory SET WH_Sellable_qty = '" + finalQuantity1
					+ "' , On_Hand_QTY = '" + finalQuantity2 + "' where SKU_ID='" + uPCValue + "'";

			PreparedStatement preparedStmtOFF5 = conWeb.prepareStatement(Query);

			preparedStmtOFF5.executeUpdate();

			closeConnectionWeb();
			break; // optional
			
			
		case "LT":
			
			openConnectionWebDB(environmentValue,BannerValue);
			
			stmt = conWeb.createStatement();
			 firstQuery = "select WH_Sellable_qty, On_Hand_QTY from SAKS_CUSTOM.sku_inventory where sku_id='"+ uPCValue + "'";
			ResultSet rsLT = stmt.executeQuery(firstQuery);

			List<String> resultsLT = new ArrayList<String>();
			ResultSetMetaData rsmdLT = rsLT.getMetaData();
			columnsNumber = rsmdLT.getColumnCount();

			while (rsLT.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					resultsLT.add(rsLT.getString(i));

				}
			}

			StoreQTY = Integer.parseInt(resultsLT.get(0));
			OnhandQTY = Integer.parseInt(resultsLT.get(1));

			finalQuantity1 = StoreQTY + webAdjustValueWeb;
			finalQuantity2 = OnhandQTY + webAdjustValueWeb;

			 Query = "Update SAKS_CUSTOM.sku_inventory SET WH_Sellable_qty = '" + finalQuantity1
					+ "' , On_Hand_QTY = '" + finalQuantity2 + "' where SKU_ID='" + uPCValue + "'";

			PreparedStatement preparedStmtLT = conWeb.prepareStatement(Query);

			preparedStmtLT.executeUpdate();

			closeConnectionWeb();
			break; // optional
			
			
			
			
		
		default:
			break;

		}

	}

	private ResultSet getDBResults(String environmentValue, String itemIDValue, String shipNodeForInventoryValue)
			throws SQLException {
		Statement stmt = con.createStatement();
		String Query = "select Quantity,Shipnode_key from OMS" + environmentValue
				+ ".yfs_inventory_supply where inventory_item_key in (Select inventory_item_key from OMS"
				+ environmentValue + ".YFS_INVENTORY_ITEM where item_id='" + itemIDValue + "' and UOM='EACH' ) and Shipnode_key = '"
				+ shipNodeForInventoryValue + "'";

		ResultSet rs = stmt.executeQuery(Query);

		return rs;
	}

	public ResultSet getDBResultsWebValue(String environmentValue, String itemIDValue) throws SQLException {

		Statement stmt = con.createStatement();

		String Query = "Select ALIAS_VALUE from OMS" + environmentValue
				+ ".YFS_ITEM_ALIAS WHERE ITEM_KEY in (SELECT ITEM_KEY FROM YFS_ITEM where item_id='" + itemIDValue + "' and UOM='EACH')";

		ResultSet rs = stmt.executeQuery(Query);

		return rs;
	}

	public ResultSet getDBResultsLT(String SKUIDValue) throws SQLException {

		Statement stmt = conWeb.createStatement();

		String Query = "select WH_Sellable_qty, On_Hand_QTY from sku_inventory where sku_id='" + SKUIDValue + "'";
		ResultSet rs = stmt.executeQuery(Query);

		return rs;

	}

	@Test
	public String getLocation() {

		URL pathSource = this.getClass().getResource("Keys.Properties");
		String absoluteFilePath = pathSource.getPath();
		return absoluteFilePath;
	}

	public ResultSet getDBResultsSAKS(String SKUCODEValue) throws SQLException {

		Statement stmt = conWeb.createStatement();

		String Query = "Select STORE_QTY,IN_STOCK_SELLABLE_QTY from saks_custom.inventory where SKU_CODE='"
				+ SKUCODEValue + "'";
		ResultSet rs = stmt.executeQuery(Query);

		return rs;

	}

	public ResultSet getDBResultsItemView(String environmentValue, String BannerValue) throws SQLException {

		Statement stmt = con.createStatement();

		String Query = "SELECT Item_ID,Quantity,Shipnode_key FROM oms" + environmentValue
				+ ".yfs_inventory_supply INNER JOIN oms" + environmentValue + ".YFS_INVENTORY_ITEM ON oms"
				+ environmentValue + ".yfs_inventory_supply.inventory_item_key = oms" + environmentValue
				+ ".YFS_INVENTORY_ITEM.inventory_item_key where oms" + environmentValue
				+ ".YFS_INVENTORY_ITEM.Organization_Code = '" + BannerValue + "' and oms" + environmentValue
				+ ".yfs_inventory_supply.Quantity>100  and oms" +environmentValue+".YFS_INVENTORY_ITEM.UOM='EACH'";

		ResultSet rs = stmt.executeQuery(Query);

		return rs;

	}

	public ResultSet getDBResultsLTItemView(ResultSet rs) throws SQLException {

		Statement stmt = conWeb.createStatement();

		ResultSetMetaData rsmdWebValue = rs.getMetaData();
		List<String> WebValue = new ArrayList<String>();

		while (rs.next()) {
			for (int i = 1; i <= 1; i++) {
				WebValue.add(rs.getString(i));

			}

		}

		StringBuilder Query = new StringBuilder(
				"select WH_Sellable_qty, On_Hand_QTY from sku_inventory where sku_id in (");
		boolean added = false;
		for (String s : WebValue) {
			if (added) {
				Query.append(",");
			}
			Query.append("'");
			Query.append(s);
			Query.append("'");
			added = true;
		}
		Query.append(")");

		ResultSet rsWeb = stmt.executeQuery(Query.toString());

		return rsWeb;
	}

	public ResultSet getDBResultsLTItemView(List<String> webValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDBResultsUPC(String itemIDValue,String EnvironmentValue) throws SQLException {

		Statement stmt = con.createStatement();

		String Query = "Select ALIAS_VALUE from OMS"+EnvironmentValue+".YFS_ITEM_ALIAS WHERE"
				+ " ITEM_KEY=(SELECT ITEM_KEY FROM OMS"+EnvironmentValue+".YFS_ITEM where item_id='"+itemIDValue+"')";
		List<String> results = new ArrayList<String>();
		ResultSet rs = stmt.executeQuery(Query);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();

		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				results.add(rs.getString(i));

			}}

		String UPC = results.get(0);
		return UPC;
		
		
		
	}

	public List<String> getDBItemInfoItemID(String environmentValue, String itemIDValue) throws SQLException {
		String Query = "Select OMS"+environmentValue+".YFS_ITEM_ALIAS.Alias_Value, OMS"+environmentValue+".YFS_ITEM.Item_id,OMS"+environmentValue+".YFS_ITEM.Model\n" + 
				"\n" + 
				"From OMS"+environmentValue+".YFS_ITEM_ALIAS\n" + 
				"\n" + 
				"Inner Join\n" + 
				"\n" + 
				"OMS"+environmentValue+".YFS_ITEM\n" + 
				"\n" + 
				"on\n" + 
				"\n" + 
				"OMS"+environmentValue+".YFS_ITEM_ALIAS.Item_Key=OMS"+environmentValue+".YFS_ITEM.ITEM_KEY\n" + 
				"where\n" + 
				"OMS"+environmentValue+".YFS_ITEM.Item_id = '"+itemIDValue+"'  and OMS"+environmentValue+".YFS_ITEM.UOM='EACH'  ";
		
		
		Statement stmt = con.createStatement();
		
		List<String> results = new ArrayList<String>();
		ResultSet rs = stmt.executeQuery(Query);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();

		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				results.add(rs.getString(i));

			}}

		return results;
		
		
		
		
		
		// TODO Auto-generated method stub
		
	}

	public ResultSet getDBItemInfoModel(String environmentValue, String ModelValue) throws SQLException {
		
		
		String Query = "Select OMS"+environmentValue+".YFS_ITEM_ALIAS.Alias_Value, OMS"+environmentValue+".YFS_ITEM.Item_id,OMS"+environmentValue+".YFS_ITEM.Model\n" + 
				"\n" + 
				"From OMS"+environmentValue+".YFS_ITEM_ALIAS\n" + 
				"\n" + 
				"Inner Join\n" + 
				"\n" + 
				"OMS"+environmentValue+".YFS_ITEM\n" + 
				"\n" + 
				"on\n" + 
				"\n" + 
				"OMS"+environmentValue+".YFS_ITEM_ALIAS.Item_Key=OMS"+environmentValue+".YFS_ITEM.ITEM_KEY\n" + 
				"where\n" + 
				"OMS"+environmentValue+".YFS_ITEM.Model= '"+ModelValue+"'  and OMS"+environmentValue+".YFS_ITEM.UOM='EACH'";
		
		
		Statement stmt = con.createStatement();
		
		List<String> results = new ArrayList<String>();
		ResultSet rs = stmt.executeQuery(Query);
//		ResultSetMetaData rsmd = rs.getMetaData();
//		int columnsNumber = rsmd.getColumnCount();
//
//		while (rs.next()) {
//			for (int i = 1; i <= columnsNumber; i++) {
//				results.add(rs.getString(i));
//
//			}}

		return rs;
		// TODO Auto-generated method stub
		
	}

	public List<String> getDBItemInfoUPC(String environmentValue, String UPCValue) throws SQLException {
		
		
		String Query = "Select OMS"+environmentValue+".YFS_ITEM_ALIAS.Alias_Value, OMS"+environmentValue+".YFS_ITEM.Item_id,OMS"+environmentValue+".YFS_ITEM.Model\n" + 
				"\n" + 
				"From OMS"+environmentValue+".YFS_ITEM_ALIAS\n" + 
				"\n" + 
				"Inner Join\n" + 
				"\n" + 
				"OMS"+environmentValue+".YFS_ITEM\n" + 
				"\n" + 
				"on\n" + 
				"\n" + 
				"OMS"+environmentValue+".YFS_ITEM_ALIAS.Item_Key=OMS"+environmentValue+".YFS_ITEM.ITEM_KEY\n" + 
				"where\n" + 
				"OMS"+environmentValue+".YFS_ITEM_ALIAS.ALIAS_VALUE = '"+UPCValue+"' and OMS"+environmentValue+".YFS_ITEM.UOM='EACH'";
		
		
		Statement stmt = con.createStatement();
		
		List<String> results = new ArrayList<String>();
		ResultSet rs = stmt.executeQuery(Query);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();

		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				results.add(rs.getString(i));

			}}

		return results;
		// TODO Auto-generated method stub
		
	}

	public List<String> getDBItemInfoColorSize(String environmentValue, String itemIDValue) throws SQLException {
		
		String Query = "select name, value from OMS"+environmentValue+".YFS_ADDITIONAL_ATTRIBUTE where parent_key in "
				+ "(select item_key from OMS"+environmentValue+".yfs_item where item_id='"+itemIDValue+"' and UOM='EACH') order by NAME";
		
		
		Statement stmt = con.createStatement();
		
		List<String> results = new ArrayList<String>();
		ResultSet rs = stmt.executeQuery(Query);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();

		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				results.add(rs.getString(i));

			}}

		return results;

	}

	public HashMap<String,String> getDBItemInfoColorSizeModel(String environmentValue, String itemIDResult) throws SQLException {
		
		String Query = "select name, value from OMS"+environmentValue+".YFS_ADDITIONAL_ATTRIBUTE where parent_key in "
				+ "(select item_key from OMS"+environmentValue+".yfs_item where item_id='"+itemIDResult+"' and UOM='EACH') order by NAME";
		
		
		Statement stmt = con.createStatement();
		
		HashMap<String,String> results = new HashMap<String,String>();
		ResultSet rs = stmt.executeQuery(Query);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();

		while (rs.next()) {
			for (int i = 1; i <= columnsNumber-1; i++) {
				
				results.put(rs.getString(i), rs.getString(i+1));


			}}

		return results;
		

	}

}
