package dataCheck;

import javafx.beans.property.SimpleStringProperty;

public class TableData {
	
	private SimpleStringProperty OMSData,DCStore,BOPIS,WEBDATA,OnHandDATA,DCAvailable,StoreAvailable,
	VendorAvailable,AttributeSize,AttributeColor,AttributeModel,AttributeStatus;

	public TableData(String OMSData, String DCStore, String BOPIS,
			String WEBDATA, String OnHandDATA, String DCAvailable, String StoreAvailable, String VendorAvailable, String AttributeSize,String AttributeColor,String AttributeModel,String AttributeStatus) {
		this.OMSData = new SimpleStringProperty(OMSData);
		this.DCStore = new SimpleStringProperty(DCStore);
		this.BOPIS = new SimpleStringProperty(BOPIS);
		this.WEBDATA = new SimpleStringProperty(WEBDATA);
		this.OnHandDATA = new SimpleStringProperty(OnHandDATA);
		this.DCAvailable = new SimpleStringProperty(DCAvailable);
		this.StoreAvailable = new SimpleStringProperty(StoreAvailable);
		this.VendorAvailable = new SimpleStringProperty(VendorAvailable);
		this.AttributeSize = new SimpleStringProperty(AttributeSize);
		this.AttributeColor = new SimpleStringProperty(AttributeColor);
		this.AttributeModel = new SimpleStringProperty(AttributeModel);
		this.AttributeStatus = new SimpleStringProperty(AttributeStatus);
	}

	public String getAttributeSize() {
		return AttributeSize.get();
	}

	public void setAttributeSize(SimpleStringProperty attributeSize) {
		AttributeSize = attributeSize;
	}

	public String getAttributeColor() {
		return AttributeColor.get();
	}

	public void setAttributeColor(SimpleStringProperty attributeColor) {
		AttributeColor = attributeColor;
	}

	public String getAttributeModel() {
		return AttributeModel.get();
	}

	public void setAttributeModel(SimpleStringProperty attributeModel) {
		AttributeModel = attributeModel;
	}

	public String getAttributeStatus() {
		return AttributeStatus.get();
	}

	public void setAttributeStatus(SimpleStringProperty attributeStatus) {
		AttributeStatus = attributeStatus;
	}

	public String getOMSData() {
		return OMSData.get();
	}

	public void setOMSData(SimpleStringProperty OMSData) {
		this.OMSData = OMSData;
	}

	public String getDCStore() {
		return DCStore.get();
	}

	public void setDCStore(SimpleStringProperty DCStore) {
		this.DCStore = DCStore;
	}

	public String getBOPIS() {
		return BOPIS.get();
	}

	public void setBOPIS(SimpleStringProperty BOPIS) {
		this.BOPIS = BOPIS;
	}

	public String getWEBDATA() {
		return WEBDATA.get();
	}

	public void setWEBDATA(SimpleStringProperty WEBDATA) {
		this.WEBDATA = WEBDATA;
	}
	
	public String getOnHandDATA() {
		return OnHandDATA.get();
	}

	public void setOnHandDATA(SimpleStringProperty OnHandDATA) {
		this.OnHandDATA = OnHandDATA;
	}
	

	public String getStoreAvailable() {
		return StoreAvailable.get();
	}

	public void setStoreAvailable(SimpleStringProperty storeAvailable) {
		StoreAvailable = storeAvailable;
	}

	public String getDCAvailable() {
		return DCAvailable.get();
	}

	public void setDCAvailable(SimpleStringProperty DCAvailable) {
		this.DCAvailable = DCAvailable;
	}
	
	public String getVendorAvailable() {
		return VendorAvailable.get();
	}

	public void setVendorAvailable(SimpleStringProperty VendorAvailable) {
		this.VendorAvailable = VendorAvailable;
	}
	

}
