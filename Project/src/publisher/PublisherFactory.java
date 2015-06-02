package publisher;

public class PublisherFactory {
	public static Publisher makePublisher(){
		return new PublisherImpl();
	}
}
