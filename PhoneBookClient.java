
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class PhoneBookClient extends JFrame implements ActionListener, Runnable {
 
	public static final String COMMEND_INFO = "Dostępne polecenia:\n" +
    		"LOAD nazwa_pliku - wczytanie danych z pliku\n" +
    		"SAVE nazwa_pliku - zapis danych do pliku\n" +
    		"GET imię - pobranie numeru osoby o imieniu\n" +
    		"PUT imię numer - zapis numeru osoby o imieniu\n" +
    		"REPLACE imie numer - zmiana numeru osoby o imieniu\n" +
    		"DELETE imie - usunięcie z kolekcji osoby o imieniu\n" +
    		"LIST - przesłanie listy kontaktów \n" +
    		"CLOSE - zakończenie nasłuchu połączeń i zamknięcie gniazda serwera\n" +
    		"BYE - zakończenie komunikacji z serwerem i zamknięcie strumieni danych oraz gniazda\n" ;
	
	public static final String AUTHOR ="Autor: Adam Lekan\nindeks: 235026" ;
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final int SERVER_PORT = 25000;

    private String name;
    private String host; 
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
 
     
    public static void main(String [] args) {

        String host = JOptionPane.showInputDialog("Podaj adres serwera: ");
        String name = JOptionPane.showInputDialog("Podaj swoje imie: ");

        if (name != null && !name.equals("")) {
            new PhoneBookClient(name, host);
        }
    } 
    
    JPanel panel = new JPanel(); 
    JLabel messageLabel = new JLabel("Zapytanie:");
    JLabel consoleLabel = new JLabel("Konsola:");
    JTextField messageField = new JTextField(20);
    JTextArea console = new JTextArea(15,20);
    JButton clear = new JButton("Wyczyść");
    
	JMenuBar menuBar = new JMenuBar();
	JMenu menuHelp = new JMenu("Pomoc");
	JMenuItem menuCommend = new JMenuItem("Komendy");
	JMenuItem menuAuthor = new JMenuItem("Autor");
	JScrollPane scroll = 
    		new JScrollPane(console, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	 
	
    PhoneBookClient(String name, String host) {
    	
        super(name);
        this.name = name;
        this.host = host; 
        
        setSize(300, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setJMenuBar(menuBar);
		menuBar.add(menuHelp);
		menuHelp.add(menuCommend);
		menuHelp.add(menuAuthor);

        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        console.setEditable(false);

        panel.add(messageLabel);
        panel.add(messageField);
        panel.add(consoleLabel);
        panel.add(scroll);
        panel.add(clear);

        messageField.addActionListener(this);
        clear.addActionListener(this);
        menuCommend.addActionListener(this);
        menuAuthor.addActionListener(this);

        setContentPane(panel);
        setVisible(true);

        new Thread(this).start();
    }
    
    synchronized public void printMessage(String message) {
        String text = console.getText();
        console.setText(this.name + " do Server: " + message + '\n' + text);
    }
 
    synchronized public void receivedMessage(String message) {
        String text = console.getText();
        console.setText("Server do " + this.name + ": " + message + '\n' + text);
    }
 
    @Override
    public void actionPerformed(ActionEvent event) {
    	// TODO Auto-generated method stub
 
        String message;
        Object source = event.getSource();
        if (source == messageField) {
            try {
                message = messageField.getText();
                outputStream.writeObject(message);
                printMessage(message);
                messageField.setText("");
            } catch (Exception e) { 
            	e.printStackTrace();
            }
        }
        if (source == clear) {
            console.setText("");
        }
        if (source == menuAuthor) {
			JOptionPane.showMessageDialog(this, AUTHOR);
		}
        if (source == menuCommend) {
        	JOptionPane.showMessageDialog(this,COMMEND_INFO);
		}
    }

    @Override
    public void run() {

        if (host != null  && host.equals("")) {
            host = "localhost";
        }
        try {
            socket = new Socket(host, SERVER_PORT);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(name);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Połaczenie nie może byc utworzone");
            setVisible(false);
            e.printStackTrace();
            return;
        }
        try {
            while(true) {
                String message = (String)inputStream.readObject();
                receivedMessage(message);
                if (message.equals("CLOSE")) {
                	inputStream.close();
                	outputStream.close();
                    socket.close();
                    setVisible(false);
                    dispose();
                    break;
                }
            }
        } catch (Exception e) {
        	JOptionPane.showMessageDialog(null, "Połaczenie przerwane");
            e.printStackTrace();   
            dispose();
        }
    }


    
}