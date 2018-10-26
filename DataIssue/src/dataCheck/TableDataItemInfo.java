package dataCheck;


import javafx.beans.property.SimpleStringProperty;

public class TableDataItemInfo {
	
	private SimpleStringProperty itemid,skucode,model,upc,size,color;

	public TableDataItemInfo(String itemid, String skucode, String model,
			String upc,String size, String color) {
		this.itemid = new SimpleStringProperty(itemid);
		this.skucode = new SimpleStringProperty(skucode);
		this.model = new SimpleStringProperty(model);
		this.upc = new SimpleStringProperty(upc);
		this.size = new SimpleStringProperty(size);
		this.color = new SimpleStringProperty(color);

	}

	public String getSize() {
		return size.get();
	}

	public void setSize(SimpleStringProperty size) {
		this.size = size;
	}

	public String getColor() {
		return color.get();
	}

	public void setColor(SimpleStringProperty color) {
		this.color = color;
	}

	public String getItemid() {
		return itemid.get();
	}

	public void setItemid(SimpleStringProperty itemid) {
		this.itemid = itemid;
	}

	public String getSkucode() {
		return skucode.get();
	}

	public void setSkucode(SimpleStringProperty skucode) {
		this.skucode = skucode;
	}

	public String getModel() {
		return model.get();
	}

	public void setModel(SimpleStringProperty model) {
		this.model = model;
	}

	public String getUpc() {
		return upc.get();
	}

	public void setUpc(SimpleStringProperty upc) {
		this.upc = upc;
	}

}
