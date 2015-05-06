package tradable;

import price.InvalidPriceOperation;
import price.Price;

public class TradableDTO implements Tradable {

	String userName;
	String product;
	String id;
	Price price;
	int originalVolume;
	int remainingVolume;
	int cancelledVolume;
	long time = System.nanoTime();
	String side;
	boolean quote;
	
	public TradableDTO(String prdct, Price p, int oVolume, int rVolume,
            int cVolume, String user, String bSide, boolean quoted, String i){
		product = prdct;
		price = p;
		originalVolume = oVolume;
		remainingVolume = rVolume;
		cancelledVolume = cVolume;
		userName = user;
		side = bSide;
		quote = quoted;
		id = i;
		System.out.println("\n---------------------\ninput id = " + i + "\nid =  " + id + "\n----------------------\n");
		
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

	public void setRemainingVolume(int newRemainingVolume) throws InvalidPriceOperation
	{
		if(newRemainingVolume > remainingVolume){
			throw new InvalidPriceOperation("Cannot set remaining volume to be greater than before. ");
		}
		remainingVolume = newRemainingVolume;
	}
	
	public void setCancelledVolume(int newCancelledVolume) throws InvalidPriceOperation{
		if(newCancelledVolume > originalVolume){
			throw new InvalidPriceOperation("Cannot cancel more volume than original volume");
		}
		cancelledVolume = newCancelledVolume;
	}

	public String getUser() {
		return userName;
	}

	public String getSide() {
		return side;
	}

	public boolean isQuote() {
		return false;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return  "Product: " + product + ", Price: $" + getPrice()
				+ ", OrginalVolume: " + getOriginalVolume()
				+ ", RemainingVolume: " + getRemainingVolume()
				+ ", CancelledVolume: " + getCancelledVolume() + ", User: "
				+ getUser() + ", Side: " + getSide() + ", isQuote: "
				+ isQuote() + ", Id: " + id;
	}

}