package tradable;

import price.InvalidPriceOperation;
import price.Price;

public class Quote {
	
	private static final String BUY_SIDE = "BUY";
	private static final String SELL_SIDE = "SELL";
	
	String user;
	String stockSymbol;
	QuoteSide buy;
	QuoteSide sell;
	
	long time = System.nanoTime();
	Price price;
	int originalVolume;
	int remainingVolume;
	int cancelledVolume;
	String id = user + stockSymbol + price + time;
	
	
	public Quote(String userName, String productSymbol,
			Price buyPrice, int buyVolume,
			Price sellPrice, int sellVolume) throws Exception{
		if(buyVolume < 1 || sellVolume < 1){
			throw new InvalidPriceOperation("Cannot have 0 or negative buy volume");
		}
		user = userName;
		stockSymbol = productSymbol;
		buy = new QuoteSide(userName, productSymbol, buyPrice, buyVolume, BUY_SIDE);
		sell = new QuoteSide(userName, productSymbol, sellPrice, sellVolume, SELL_SIDE);
	}
	
	public String getUserName(){
		return user;
	}
	
	public String getProdcut(){
		return stockSymbol;
	}
	
	public QuoteSide getQuoteSide(String sideln){
		return buy;
	}
	
	public String toString(){
		return user + "order : BUY " + buy + stockSymbol + " at " + price + "(Original vol: " + originalVolume + "CXL vol: " + cancelledVolume + "_), ID: " + id;
	}
}