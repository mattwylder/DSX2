package book;

import java.util.ArrayList;
import java.util.HashMap;

import price.InvalidPriceOperation;
import price.Price;
import tradable.*;
import message.*;

public class TradeProcessorPriceTimeImpl implements TradeProcessor{
	
	private HashMap<String, FillMessage> fillMessages = new HashMap<String, FillMessage>();
	private ProductBookSide productBookSide;
	
	public TradeProcessorPriceTimeImpl(ProductBookSide pbs){
		productBookSide = pbs;
	}
	
	private String makeFillKey(FillMessage fm){
		return fm.getUser() + fm.getId() + fm.getPrice();
	}
	
	private boolean isNewFill(FillMessage fm){
		String key = makeFillKey(fm);
		if(!fillMessages.containsKey(key)){
			return true;
		}
		return false;
	}
	
	private void addFillMessage(FillMessage fm) 
			throws InvalidDataOperation{
		if(isNewFill(fm)){
			fillMessages.put(makeFillKey(fm), fm);
		}
		else{
			String key = makeFillKey(fm);
			FillMessage msg = fillMessages.get(key);
			msg.setVolume(msg.getVolume() + fm.getVolume());
			msg.setDetails(fm.getDetails());
		}
	}
	
	public HashMap<String, FillMessage> doTrade(Tradable trd) 
			throws InvalidDataOperation, InvalidPriceOperation{
		fillMessages = new HashMap<String, FillMessage>();
		ArrayList<Tradable> tradedOut = new ArrayList<Tradable>();
		ArrayList<Tradable> entriesAtPrice = productBookSide.getEntriesAtTopOfBook();
		Price tPrice = null;
		FillMessage fm = null;
		int remainder = 0;
		for(Tradable t: entriesAtPrice){
			if(trd.getRemainingVolume() == 0){
				//Goto afterFor
				break;
			}
			else if(trd.getRemainingVolume() >= t.getRemainingVolume()){
				tradedOut.add(t);
				if(t.getPrice().isMarket()){
					tPrice = trd.getPrice();
				}
				else{
					tPrice = t.getPrice();
				}
				fm = new FillMessage(t.getUser(), t.getProduct(),
						tPrice,t.getRemainingVolume(),"leaving 0",t.getSide(), t.getId());
				addFillMessage(fm);
				fm = new FillMessage(trd.getUser(), trd.getProduct(),
						tPrice,t.getRemainingVolume(),("leaving " + (trd.getRemainingVolume() -t.getRemainingVolume())),
						trd.getSide(), trd.getId());
				addFillMessage(fm);
				trd.setRemainingVolume(trd.getRemainingVolume()-t.getRemainingVolume());
				t.setRemainingVolume(0);
				productBookSide.addOldEntry(t);
			}
			else{
				remainder = t.getRemainingVolume() - trd.getRemainingVolume();
				if(t.getPrice().isMarket()){
					tPrice = trd.getPrice();
				}
				else{
					tPrice = t.getPrice();
				}
				fm = new FillMessage(t.getUser(), t.getProduct(),
						tPrice,trd.getRemainingVolume(),"leaving " + remainder,t.getSide(), t.getId());
				addFillMessage(fm);
				fm = new FillMessage(trd.getUser(), trd.getProduct(),
						tPrice,trd.getRemainingVolume(),"leaving 0",trd.getSide(), trd.getId());
				addFillMessage(fm);
				trd.setRemainingVolume(0);
				t.setRemainingVolume(remainder);
				productBookSide.addOldEntry(trd);
			}
		}
		
		for(Tradable t : tradedOut){
			entriesAtPrice.remove(t);
		}
		if(entriesAtPrice.isEmpty()){
			productBookSide.clearIfEmpty(productBookSide.topOfBookPrice());
		}
		return fillMessages;
	}
	
}
