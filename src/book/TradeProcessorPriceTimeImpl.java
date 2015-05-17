package book;

import java.util.HashMap;

import tradable.*;
import message.*;

public class TradeProcessorPriceTimeImpl implements TradeProcessor{
	
	private HashMap<String, FillMessage> fillMessages = new HashMap<String, FillMessage>();
	private ProductBookSide productBookSide;
	
	public TradeProcessorPriceTimeImpl(ProductBookSide pbs){
		productBookSide = pbs;
	}
	
	public HashMap<String, FillMessage> doTrade(Tradable trd){
		return null;
	}
	
	private String makeFillKey(FillMessage fm){
		return fm.getUser() + fm.getId() + fm.getPrice();
	}
	
	private boolean isNewFill(FillMessage fm){
		String key = makeFillKey(fm);
		if(fillMessages.containsKey(key)){
			return true;
		}
		return false;
	}
	
	private void addFillMessage(FillMessage fm){
		
	}
	
}
