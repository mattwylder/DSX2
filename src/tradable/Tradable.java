package tradable;

import price.InvalidPriceOperation;
import price.Price;

public interface Tradable {

		String getProduct();
		 
		Price getPrice();
		
		int getOriginalVolume();
		
		int getRemainingVolume();
		
		int getCancelledVolume();
		
		void setRemainingVolume(int newRemainingVolume) throws InvalidPriceOperation;
		
		void setCancelledVolume(int newCancelledVolume) throws InvalidPriceOperation;
		
		String getUser();
		
		String getSide();
		
		boolean isQuote();
		
		String getId();
				
}