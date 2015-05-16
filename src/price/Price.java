package price;

public class Price {
	
	private long value; //value in cents


	//CONSTRUCTORS
	protected Price(){
		this.value = 0; 
	}
	protected Price(long value){
		this.value = value;
	}

	protected long getValue(){
		return value;
	}
	
	// PRICE MATH
	public Price add(Price p) throws  InvalidPriceOperation{
		if(isMarket()){
			throw new InvalidPriceOperation("Cannot add Market Price");
		}
		//System.out.println(value + " + " +p.getValue() + " = " +(value + p.getValue()));
		return PriceFactory.makeLimitPrice(value + p.getValue());
	}
	
	public Price subtract(Price p) throws  InvalidPriceOperation{
		if(isMarket()){
			throw new InvalidPriceOperation("Cannot subtract Market Price");
		}
		return PriceFactory.makeLimitPrice(value - p.getValue());
	}
	
	public Price multiply(int p) throws InvalidPriceOperation{
		if(isMarket()){
			throw new InvalidPriceOperation("Cannot multiply Market Price");
		}
		return PriceFactory.makeLimitPrice(value * p);
	}
	
	
	
	// 	PRICE COMPARISONS
	//TODO: Throw InvalidPriceOperation if either are Market or null
	public boolean greaterOrEqual(Price p){
		if(isMarket()){
			return false;
		}
		return value >= p.getValue();
	}
	public boolean greaterThan(Price p){
		if(isMarket()){
			return false;
		}
		return value > p.getValue();
	}

	public boolean lessOrEqual(Price p){
		if(isMarket()){
			return false;
		}
		return value <= p.getValue();
	}	
	
	public boolean lessThan(Price p){
		if(isMarket()){
			return false;
		}
		return value < p.getValue();
	}
	
	public boolean equals(Price p){
		if(isMarket()){
			return false;
		}
		return value == p.getValue();
	}
	
	public boolean isMarket(){
		return false;
	}
	
	public boolean isNegative(){
		return value < 0;
	}
	
	public int compareTo(Price p)
	{
		if(value < p.getValue())
			return -1;
		else if(value > p.getValue())
			return 1;
		else
			return 0;	
	}
	
	public String toString(){
		long dollars = value / 100;
		long cents = value % 100;
		String message = "";
		if((dollars < 0) || (cents < 0))
		{
			message = String.format("$-%,d",Math.abs(dollars)) + String.format(".%02d",Math.abs(cents));
		}
		else
		{message = String.format("$%,d",dollars) + String.format(".%02d",cents);}
		return message;	}
}
