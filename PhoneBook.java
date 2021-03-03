
import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class PhoneBook implements Serializable {
 
  
    /**    
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	
	ConcurrentHashMap<String, Integer> phoneBook = new ConcurrentHashMap<>();

	
	
	@SuppressWarnings("unchecked")
	String load(String fileName) {
        try {
        	ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
        	phoneBook = (ConcurrentHashMap<String, Integer>)inputStream.readObject();
        	inputStream.close();
        	
        } catch (Exception e) {
        	
            e.printStackTrace();
            return "ERROR: Błąd podczas wczytywania danych z pliku";
            
        }
        
        return "OK"; 
    }

    String save(String fileName) {
    	try {
    		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
        	outputStream.writeObject(phoneBook);
        	outputStream.close();
        	
        } catch (Exception e) {
        	
            e.printStackTrace();
            return "ERROR: Błąd podczas zapisywania pliku";
        }
        return "OK";
    }

    String get(String name) {
        if (phoneBook.containsKey(name))
            return "OK\n " + phoneBook.get(name) + "\n";
        else
            return "ERROR: Nie ma takiej osoby w książce\n";
    }

    String put(String name, String number) {
        if (phoneBook.containsKey(name)) {
            return "ERROR: Taki kontakt juz istnieje\n";
        }
        else {
        	phoneBook.put(name, Integer.valueOf(number));
            return "OK";
        }
    }

    String replace (String name, String newNumber) {
        if (phoneBook.containsKey(name)) {
        	phoneBook.replace(name, Integer.valueOf(newNumber));
            return "OK";
        }
        else {
        	
            return "Nie ma takiej osoby w książce\n";
        } 
    }

   String delete (String name) {
        if (phoneBook.containsKey(name)) {
        	phoneBook.remove(name);
            return "OK";
        }
        else {
        	
            return "ERROR: Nie ma takiej osoby w bazie\n";
        }
    }

    @SuppressWarnings("rawtypes")
	String list() {
        StringBuilder list = new StringBuilder();
        Iterator iterator = phoneBook.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            list.append(pair.getKey()+ " ");
        }
        return "Aktualna książka:\n" + list;
    }

}