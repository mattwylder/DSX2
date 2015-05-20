package publisher;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import client.User;
import price.Price;
import price.PriceFactory;

public class LastSalePublisher extends PublisherImpl{
	
	private volatile static LastSalePublisher instance;
	private static Publisher publisher;
	private Price lastSalePrice;
	private int lastSaleVolume;

	private LastSalePublisher(){
		super();
		publisher = PublisherFactory.makePublisher();
	}
	
	public static LastSalePublisher getInstance(){
		if(instance == null){
			synchronized(LastSalePublisher.class){
				if(instance == null){
					instance = new LastSalePublisher();
				}
			}
		}
		return instance;
	}
	
	public synchronized void publishLastSale(String product, Price price, int volume){
		if(!subscriptions.containsKey(product)){
			return;
		}
		ArrayList<User> subscribers = subscriptions.get(product);
		Iterator<User> itr = subscribers.iterator();
		while(itr.hasNext()){
			sendToSubscriber(itr.next(), product, price, volume);
		}
		
		TickerPublisher tp = TickerPublisher.getInstance();
		tp.publishTicker(product, price);
	}
	
	private void sendToSubscriber(User user, String product, Price price, int volume){
		Price tmp = price;
		if(price == null){
			tmp = PriceFactory.makeLimitPrice(0);
		}
		user.acceptLastSale(product, price, volume);
	}

}
