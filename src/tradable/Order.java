package tradable;

import price.*;

public class Order implements Tradable {

	String user;
	long time = System.nanoTime();
	String stockSymbol;
	Price price;
	String id;
	int originalVolume;
	int remainingVolume;
	int cancelledVolume;
	String side;
	boolean quote;

	public Order(String userName, String productSymbol, Price orderPrice,
			int oVolume, String bSide) throws Exception {
		
		//TODO: Change all this to setMethods
		user = userName;
		stockSymbol = productSymbol;
		price = orderPrice;
		originalVolume = oVolume;
		side = bSide;
		id = user + stockSymbol + price + time;
		if(oVolume < 1){
			throw new Exception("Cannot have 0 or negative volume");
		}
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