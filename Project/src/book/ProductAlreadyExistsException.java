package book;

public class ProductAlreadyExistsException extends Exception {
	public ProductAlreadyExistsException(String msg){
		super(msg);
	}
}
