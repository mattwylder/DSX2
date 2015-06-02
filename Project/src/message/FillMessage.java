package message;

import price.Price;

public class FillMessage implements Comparable<FillMessage> {

	private String user;
	private String product;
	private Price price;
	private int volume;
	private String details;
	private String side;
	private String id;

	public FillMessage(String u, String prd, Price p, int v, String dt,
			String s, String i) throws InvalidDataOperation {
		setUser(u);
		setProduct(prd);
		setPrice(p);
		setVolume(v);
		setDetails(dt);
		setSide(s);
		setId(i);
	}
	
	private void setUser(String userIn)
			throws InvalidDataOperation {
		if(userIn == null || userIn.isEmpty()){
			throw new InvalidDataOperation("User is null or the empty string");
		}
		user = userIn;
	}
	private void setProduct(String productIn)
			throws InvalidDataOperation{
		if(productIn == null || productIn.isEmpty()){
			throw new InvalidDataOperation("Product name is null or empty string");
		}
		product = productIn;
	}

	private void setPrice(Price priceIn)
			throws InvalidDataOperation{
		if(priceIn == null){
			throw new InvalidDataOperation("Price is null");
		}
		price = priceIn;
	}
	
	public void setVolume(int volumeIn)
			throws InvalidDataOperation{
		if(volumeIn < 1){
			throw new InvalidDataOperation("Volume is less than 1");
		}
		volume = volumeIn;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public void setId(String id) 
			throws InvalidDataOperation{
		if(id == null){
			throw new InvalidDataOperation("ID is null");
		}
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public String getProduct() {
		return product;
	}

	public Price getPrice() {
		return price;
	}

	public int getVolume() {
		return volume;
	}

	public String getDetails() {
		return details;
	}

	public String getSide() {
		return side;
	}

	public String getId() {
		return id;
	}

	public int compareTo(FillMessage fm) {
		return price.compareTo(fm.getPrice());
	}

	public String toString() {
		return "User: " + user + " Product: " + product + " Price: " + price
				+ " Volume: " + volume + " Details: " + details+ " SIDE: " + side;
	}

}
