package tradable;

import message.InvalidDataOperation;
import price.InvalidPriceOperation;
import price.Price;

public class QuoteSide implements Tradable {

	private String user;
	private String id;
	private int volume;
	private long time = System.nanoTime();
	private String side;
	private String product;
	private Price price;
	private int originalVolume;
	private int remainingVolume;
	private int cancelledVolume;

	public QuoteSide(String userName, String productSymbol, Price sidePrice,
			int originalVolume, String sideB) 
					throws InvalidPriceOperation, InvalidDataOperation {
		if(originalVolume < 1){
			throw new InvalidPriceOperation("Cannot have 0 or negative buy volume");
		}
		setUser(userName);
		setProduct(productSymbol);
		setOriginalVolume(originalVolume);
		setSide(sideB);
		setPrice(sidePrice);
		setId();

	}
	
	private void setUser(String userIn) 
			throws InvalidDataOperation {
		if(userIn == null || userIn == null){
			throw new InvalidDataOperation("Username is null or the empty string");
		}
		user = userIn;
	}
	
	private void setProduct(String stockSymbolIn)
			throws InvalidDataOperation {
		if(stockSymbolIn == null || stockSymbolIn.isEmpty()){
			throw new InvalidDataOperation("Stock Symbol is null or the empty string");
		}
		product = stockSymbolIn;
	}
	
	private void setPrice(Price priceIn)
			throws InvalidDataOperation{
		if(priceIn == null){
			throw new InvalidDataOperation("Price is null");
		}
		price = priceIn;
	}
	
	private void setOriginalVolume(int originalVolumeIn)
			throws InvalidDataOperation{
		if(originalVolumeIn < 1){
			throw new InvalidDataOperation("Volume is less than 1");
		}
		originalVolume = originalVolumeIn;
		remainingVolume = originalVolume;
	}
	
	private void setSide(String sideIn)
			throws InvalidDataOperation{
		if(!sideIn.equals("BUY") && !sideIn.equals("SELL")){
			throw new InvalidDataOperation("Invalid side: Side must be BUY or SELL, was entered as " + sideIn);
		}
		side = sideIn;
	}
	
	private void setId(){
		id = user + product + time;
	}
	
	public QuoteSide(QuoteSide qs) {
		user = qs.getUserName();
		product = qs.getProduct();
		volume = qs.getVolume();
		side = qs.getSide();
	}

	public String getUserName() {
		return user;
	}

	public int getVolume() {
		return volume;
	}

	public String getProduct() {
		return product;
	}

	public Price getPrice() {
		return price;
	}

	public int getOriginalVolume() {
		return originalVolume;
	}

	public int getRemainingVolume() {
		return remainingVolume;
	}

	public int getCancelledVolume() {
		return cancelledVolume;
	}

	public void setRemainingVolume(int newRemainingVolume) throws InvalidPriceOperation{
		if(newRemainingVolume > originalVolume){
			throw new InvalidPriceOperation("Cannot cancel more volume than original volume");
		}
		remainingVolume = newRemainingVolume;
	}
	
	public void setCancelledVolume(int newCancelledVolume) throws InvalidPriceOperation {
		if(newCancelledVolume > originalVolume){
			throw new InvalidPriceOperation("Cannot cancel more volume than original volume");
		}
		cancelledVolume = newCancelledVolume;
	}

	public String getUser() {
		return user;
	}

	public String getSide() {
		return side;
	}

	public boolean isQuote() {
		return false;
	}

	public String getId() {
		return user + product + time;
	}
	
	public String toString(){
		return price + " x " + originalVolume + "(Original Vol: " + originalVolume + ", CXL'd Vol: " + cancelledVolume + ") [" + id + "]"; 
	}


}