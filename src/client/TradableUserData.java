package client;

import message.InvalidDataOperation;

public class TradableUserData {
	String user;
	String product;
	String side;
	String id;
	
	public TradableUserData(String userIn, String productIn, String sideIn, String idIn)
			throws InvalidDataOperation{
		setUser(userIn);
		setProduct(productIn);
		setSide(sideIn);
		setId(idIn);
	}
	
	public String getUser(){
		return user;
	}
	
	public String getProduct(){
		return product;
	}
	
	public String getSide(){
		return side;
	}
	
	public String getId(){
		return id;
	}
	
	private void setUser(String userIn)throws InvalidDataOperation{
		if(userIn == null || userIn.isEmpty()){
			throw new InvalidDataOperation("User is null or empty string");
		}
		user = userIn;
	}
	
	private void setProduct(String productIn)throws InvalidDataOperation{
		if(productIn == null || productIn.isEmpty()){
			throw new InvalidDataOperation("Product is null or empty string");
		}
		product = productIn;
	}
	
	private void setSide(String sideIn)throws InvalidDataOperation{
		if(sideIn == null || sideIn.isEmpty()){
			throw new InvalidDataOperation("Side is null or empty string");
		}
		else if(!sideIn.equals("BUY") || !sideIn.equals("SELL")){
			throw new InvalidDataOperation("Side is neither buy nor sell");
		}
		side = sideIn;
	}
	
	private void setId(String idIn) throws InvalidDataOperation{
		if(idIn == null || idIn.isEmpty()){
			throw new InvalidDataOperation("ID is null or empty string");
		}
		id = idIn;
	}
	
	public String toString(){
		return "User " + user + ", " + side + " " + product + "(" + id + ")";
	}
}
