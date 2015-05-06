package publisher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import message.CancelMessage;
import message.FillMessage;
import message.MarketMessage;
import client.User;

public class MessagePublisher implements Publisher{

	private static volatile MessagePublisher instance;
	private static Publisher publisher;
	
	protected MessagePublisher(){
		super();
		publisher = PublisherFactory.makePublisher();
	}
	
	public static MessagePublisher getInstance(){
		if(instance == null){
			synchronized(MessagePublisher.class){
				if(instance == null){
					instance = new MessagePublisher();
				}
			}
		}
		return instance;
	}
	
	public synchronized void publishCancel(CancelMessage cm){
		//TODO: All Publishers need to check if things are actually in subscriptions
		
		if(!publisher.getSubscriptions().containsKey(cm.getProduct())){
			return;
		}
		ArrayList<User> subscribers = publisher.getSubscriptions().get(cm.getProduct());
		for(int i = 0; i < subscribers.size(); i ++){
			if(subscribers.get(i).getUserName().equals(cm.getUser())){
				subscribers.get(i).acceptMessage(cm);
			}
		}
	}
	
	public synchronized void publishFill(FillMessage message){
		if(!publisher.getSubscriptions().containsKey(message.getProduct())){
			return;
		}
		ArrayList<User> subscribers = publisher.getSubscriptions().get(message.getProduct());
		for(int i = 0; i < subscribers.size(); i ++){
			if(subscribers.get(i).getUserName().equals(message.getUser())){
				subscribers.get(i).acceptMessage(message);	
			}
		}
	}
	
	public synchronized void publishMarketMessage(MarketMessage message){
		Iterator<ArrayList<User>> i = publisher.getSubscriptions().values().iterator();
		Iterator<User> j;
		while(i.hasNext()){
			j = i.next().iterator();
			while(j.hasNext()){
				j.next().acceptMarketMessage(message.toString());
			}
		}
	}

	@Override
	public void subscribe(User user, String product)
			throws AlreadySubscribedException {
		publisher.subscribe(user, product);
	}

	@Override
	public void unSubscribe(User user, String product)
			throws NotSubscribedException {
		publisher.unSubscribe(user, product);
		
	}

	@Override
	public Map<String, ArrayList<User>> getSubscriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
