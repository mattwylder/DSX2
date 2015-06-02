package tradable;

import message.InvalidDataOperation;
import price.InvalidPriceOperation;
import price.Price;

public class TradableDTO implements Tradable {

	public String userName;
	public String product;
	public String id;
	public Price price;
	public int originalVolume;
	public int remainingVolume;
	public int cancelledVolume;
	public long time = System.nanoTime();
	public String side;
	public boolean quote;
	
	public TradableDTO(String prdct, Price p, int oVolume, int rVolume,
            int cVolume, String user, String bSide, boolean quoted, String i) 
            		throws InvalidDataOperation, InvalidPriceOperation{
		setProduct(prdct);
		setPrice(p);
		setOriginalVolume(oVolume);
		setRemainingVolume(rVolume);
		setCancelledVolume(cVolume);
		setUser(user);
		setSide(bSide);
		quote = quoted;
		setId(i);
	}
	
	private void setProduct(String stockSymbolIn)
			throws InvalidDataOperation {
		if(stockSymbolIn == null || stockSymbolIn.isEmpty()){
			throw new InvalidDataOperation("Stock Symbol is null or the empty string");
		}
		product = stockSymbolIn;
	}
	
	private void setUser(String userIn)
			throws InvalidDataOperation {
		if(userIn == null || userIn.isEmpty()){
			throw new InvalidDataOperation("User is null or the empty string");
		}
		userName = userIn;
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

	private void setId(String IdIn)
			throws InvalidDataOperation {
		if(IdIn == null || IdIn.isEmpty()){
			throw new InvalidDataOperation("User is null or the empty string");
		}
		id = IdIn;
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