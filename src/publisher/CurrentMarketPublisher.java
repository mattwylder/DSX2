package publisher;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import client.User;
import price.Price;
import price.PriceFactory;


public class CurrentMarketPublisher extends PublisherImpl{
	
	private volatile static CurrentMarketPublisher instance;
	private Publisher publisher;
	
	private CurrentMarketPublisher(){
		super();
		publisher = PublisherFactory.makePublisher();
	}
	
	public static CurrentMarketPublisher getInstance(){
		if(instance == null){
			synchronized(CurrentMarketPublisher.class){
				if(instance == null){
					instance = new CurrentMarketPublisher();
				}
			}
		}
		return instance;
	}
	
	public synchronized void publishCurrentMarket(MarketDataDTO md){
		if(!subscriptions.containsKey(md.getProduct())){
			return;
		}
		ArrayList<User> subscribers = subscriptions.get(md.product);
		Iterator<User> itr = subscribers.iterator();
		while(itr.hasNext()){
			sendToSubscriber(itr.next(), md.product, md.buyPrice, md.buyVolume, md.sellPrice, md.sellVolume);
		}
	}
	
	private void sendToSubscriber(User user, String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume){
		Price tmpBuy = buyPrice;
		Price tmpSell = sellPrice;
		if(buyPrice == null){
			tmpBuy = PriceFactory.makeLimitPrice(0);
		}
		if(sellPrice == null){
			tmpSell = PriceFactory.makeLimitPrice(0);
		}
		user.acceptCurrentMarket(product, tmpBuy, buyVolume, tmpSell, sellVolume);
	}

}
