package publisher;
import price.Price;

public class MarketDataDTO {
	
	public String product;
	public Price buyPrice;
	public int buyVolume;
	public Price sellPrice;
	public int sellVolume;
	
	public MarketDataDTO(String prdct, Price bPrice, int bVol, Price sPrice, int sVol){
		product = prdct;
		buyPrice = bPrice;
		buyVolume = bVol;
		sellPrice = sPrice;
		sellVolume = sVol;
	}
	
	public String getProduct() {
		return product;
	}

	public Price getBuyPrice() {
		return buyPrice;
	}
	
	public int getBuyVolume(){
		return buyVolume;
	}
	
	public int getSellVolume(){
		return sellVolume;
	}
	
	public Price getSellPrice() {
		return sellPrice;
	}
	
	public String toString(){
		return "Product: " + getProduct() + ", BuyPrice: " + getBuyPrice()  + ", BuyVolume: " + getBuyVolume() +
				", SellPrice: " + getSellPrice() + ", SellVolume: " + getSellVolume();
	}
	
}
