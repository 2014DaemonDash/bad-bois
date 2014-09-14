package databases;

public class FoodItem {
	private String foodName;
	private String expirationDate;
	private String price;
	private int id;
	
	public FoodItem(String foodName, String expirationDate, String price, int id){
		this.foodName = foodName;
		this.expirationDate = expirationDate;
		this.price = price;
		this.id = id;
	}
	
	public String toString(){
		return foodName + "Expiration date: " + expirationDate;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

}
