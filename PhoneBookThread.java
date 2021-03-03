
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;



public class PhoneBookThread implements Runnable {
	
    private String name;
    private Socket socket;
    private PhoneBookServer server;
    private PhoneBook phoneBook;
    private ObjectOutputStream outputStream = null;

    
    PhoneBookThread(PhoneBookServer server, Socket socket, PhoneBook phoneBook) {
    	
        this.server = server;
        this.socket = socket;
        this.phoneBook = phoneBook;
        
        new Thread(this).start();
    }

    public void sendMessage(String message) {
        try {
        	outputStream.writeObject(message);
            if (message.equals("CLOSE")) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public void run() {
    	 
        String message; 
 
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());) {

            this.outputStream = output;
            this.name = (String)input.readObject();
            while(true) { 
            	
                message = (String)input.readObject();
                server.receivedMessage(this, message);
                String [] str = message.split(" ");
                
                if (str[0].equals("LOAD")) {
                	String fileName = JOptionPane.showInputDialog(null, "Podaj nazwę pliku");
                    String newMessage = phoneBook.load(fileName);
                    server.printMessage(this, newMessage);
                    sendMessage(newMessage);
                }
                else if (str[0].equals("SAVE")) {
                	String fileName = JOptionPane.showInputDialog(null, "Podaj nazwę pliku");
                    String newMessage = phoneBook.save(fileName);
                    server.printMessage(this, newMessage);
                    sendMessage(newMessage);
                }
                else if (str[0].equals("GET")) {
                    String newMessage = phoneBook.get(str[1]);
                    server.printMessage(this, newMessage);
                    sendMessage(newMessage);
                }
                else if (str[0].equals("PUT")) {
                    String newMessage = phoneBook.put(str[1], str[2]);
                    server.printMessage(this, newMessage);
                    sendMessage(newMessage); 
                }
                else if (str[0].equals("REPLACE")) {
                    String newMessage = phoneBook.replace(str[1], str[2]);
                    server.printMessage(this, newMessage);
                    sendMessage(newMessage);
                }
                else if (str[0].equals("DELETE")) {
                    String newMessage = phoneBook.delete(str[1]);
                    server.printMessage(this, newMessage);
                    sendMessage(newMessage);
                }
                else if (message.equals("LIST")) {
                    String newMessage = phoneBook.list();
                    server.printMessage(this, newMessage);
                    sendMessage(newMessage);
                }
                else if (message.equals("CLOSE")){ 
                    sendMessage("Zamykanie nasłuchu połączeń");
                    socket.close();      	
                    socket = null;
                    
                }
                else if (message.equals("BYE")) {
                    server.printMessage(this, "Zamkanie okna\n");
                    sendMessage("Zamkanie okna\n");
                    socket.close();
                    input.close();
                	output.close();
                
                    throw new Exception("Zamkniecie programu na życzenie użytkownika");
                }
            
                else {
                 
                	JOptionPane.showMessageDialog(null,PhoneBookClient.COMMEND_INFO);
                } 
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
}