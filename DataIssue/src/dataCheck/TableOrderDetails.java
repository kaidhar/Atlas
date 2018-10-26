package dataCheck;

import javafx.beans.property.SimpleStringProperty;

public class TableOrderDetails {

	
	public String getAttributeName() {
		return AttributeName.get();
	}

	public void setAttributeName(SimpleStringProperty attributeName) {
		AttributeName = attributeName;
	}

	public String getAttributeValue() {
		return AttributeValue.get();
	}

	public void setAttributeValue(SimpleStringProperty attributeValue) {
		AttributeValue = attributeValue;
	}

	private SimpleStringProperty AttributeName,AttributeValue;
	
	public TableOrderDetails(String AttributeName, String AttributeValue) {
		this.AttributeName = new SimpleStringProperty(AttributeName);
		this.AttributeValue = new SimpleStringProperty(AttributeValue);


	}
	
}


