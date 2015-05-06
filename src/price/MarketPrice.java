package price;

public class MarketPrice extends Price {

	@Override
	public boolean isMarket(){
		return true;
	}
	
	public String toString() {
		return "MKT";
	}
}
