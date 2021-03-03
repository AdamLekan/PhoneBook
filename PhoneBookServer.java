
import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



class PhoneBookServer extends JFrame implements Runnable {


    /**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	static final int SERVER_PORT = 25000;

	private JLabel consoleLabel = new JLabel("Konsola :");
    private JTextArea console = new JTextArea(15,20);
    private JScrollPane consoleScroll = 
    		new JScrollPane(console, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JPanel panel = new JPanel();
    private PhoneBook phoneBook = new PhoneBook();

    
    public static void main(String [] args){
    	
        new PhoneBookServer();
        
    } 
    
    PhoneBookServer() {
    	
        super("Serwer ksiażki telefonicznej");
        setSize(300,340);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
      
        panel.add(consoleLabel);
        panel.add(consoleScroll);
        
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        
        setContentPane(panel);
        setVisible(true);

        new Thread(this).start();
    }

    synchronized public void receivedMessage(PhoneBookThread client, String message) {
        String text = console.getText();
        console.setText(client.getName() + " do Server: " + message + '\n' + text);
    }

    synchronized public void printMessage(PhoneBookThread client, String message) {
        String text = console.getText();
        console.setText("Server do " + client.getName() + ": " + message + '\n' + text);
    }


    @Override
    public void run() {
    	
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            String host = InetAddress.getLocalHost().getHostName();
            System.out.println("Serwer uruchomiony na : " + host);
            
            while (true) {
                Socket socket = server.accept();
                if (socket != null) {
                    new PhoneBookThread(this, socket, phoneBook);
                }
            } 
        } 
        catch (IOException e) {
			System.out.println(e);
        }
    }
}