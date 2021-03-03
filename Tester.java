


public class Tester {
	

	
	
	public static void main(String [] args){
		new PhoneBookServer();
		
		try{
			Thread.sleep(1000);
		} catch(Exception e){}
			
	  	new PhoneBookClient("Adam", "localhost");

 
	}
}
 