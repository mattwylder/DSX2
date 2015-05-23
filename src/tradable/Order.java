package tradable;

import message.InvalidDataOperation;
import price.*;

public class Order implements Tradable {

	private String user;
	private long time = System.nanoTime();
	private String stockSymbol;
	private Price price;
	private String id;
	private int originalVolume;
	private int remainingVolume;
	private int cancelledVolume;
	private String side;
	private boolean quote;

	public Order(String userName, String productSymbol, Price orderPrice,
			int oVolume, String bSide) throws InvalidDataOperation {
		setUser(userName);
		setStockSymbol(productSymbol);
		setPrice(orderPrice);
		setOriginalVolume(oVolume);
		setSide(bSide);
		setId();
	}
	
	private void setUser(String userIn)
			throws InvalidDataOperation {
		if(userIn == null || userIn.isEmpty()){
			throw new InvalidDataOperation("User is null or the empty string");
		}
		user = userIn;
	}
	
	private void setStockSymbol(String productIn)
			throws InvalidDataOperation{
		if(productIn == null || productIn.isEmpty()){
			throw new InvalidDataOperation("Product name is null or empty string");
		}
		stockSymbol = productIn;
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

	private void setSide(String side) {
		this.side = side;
	}

	private void setId() 
			throws InvalidDataOperation{
		id = user + stockSymbol + price + time;
	}

	public String getUserName() {
		return user;
	}

	public String toString() {
		//USER1 order: BUY 250 GE at $21.59 (Original Vol: 250, CXL'd Vol: 0), ID: USER1GE$21.591684416944495943
		return String.format("%s order: %s %s  %s at %s (Original vol: %s, CXL'd vol: %s), ID: %s", 
				user, side,remainingVolume, stockSymbol, price.toString(), originalVolume, cancelledVolume, id);	
	}

	public String getProduct() {
		return stockSymbol;
	}

	public Price getPrice(){
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
	@Override
	public String getId() {
		return id;
	}
	
}