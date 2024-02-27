import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPIMProgramClient extends JFrame
        implements ActionListener {
    private static boolean firstRun;
    private static String userInputIPAddress = "";

    private static InetAddress host;
    private static final int PORT = 4444;
    private static Socket link;

    private static Scanner input;
    private static PrintWriter output;

    private static JTextArea messageField;
    private static JTextArea messageThread;

    private static String messageToSend = "";
    private static String response = "";

    public static void main(String[] args) throws InterruptedException { // InterruptedException for Thread.sleep()
        firstRun = true;

        TCPIMProgramClient frame = new TCPIMProgramClient();
        frame.setSize(600, 500);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        if (link != null)
                        {
                            try {
                                link.close();
                            }
                            catch (IOException ioEx)
                            {
                                System.out.println(
                                        "\n* Unable to close link! *\n");
                                System.exit(1);
                            }
                        }
                        System.exit(0);
                    }
                }
        );

        messageThread.append("welcome to the most amazing instant messenger ever created! (◕‿◕✿) \n" +
                "please enter the ip address of the person you would like to message: \n\n");

        while (!messageToSend.equals("***CLOSE***")) {
            try {
                response = input.nextLine();
            } catch (NoSuchElementException noSuchElementEx) {
                messageThread.append("\n\nyour friend has disconnected!\nthank you for using this program ;D");
                messageField.setEnabled(false);
                Thread.sleep(5000);
                break;
            } catch (NullPointerException nullEx) {
                continue;
            }
            messageThread.append("\nyour friend said: " + response);
        }

        try {
            link.close();
            System.exit(0);
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
            ioEx.printStackTrace();
            System.exit(1);
        }

    }

    // =================================================================================================================================================================

    public TCPIMProgramClient() {

        this.setTitle("im client");

        messageThread = new JTextArea(20, 20);
        Font courierFont = new Font("Courier", Font.PLAIN, 14);
        messageThread.setFont(courierFont);
        messageThread.setBackground(new Color(67, 171, 208));
        messageThread.setLineWrap(true);
        messageThread.setWrapStyleWord(true);
        messageThread.setAutoscrolls(true);
        messageThread.setEnabled(false);
        messageThread.setDisabledTextColor(Color.BLACK);
        add(new JScrollPane(messageThread), BorderLayout.NORTH);

        messageField = new JTextArea(4, 20);
        messageField.setSize(2, 10);
        messageField.setFont(courierFont);
        messageField.setBackground(new Color(0, 55, 255));
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
        messageField.setAutoscrolls(true);
        messageField.setBorder(new LineBorder(Color.BLUE, 1));
        add(new JScrollPane(messageField));

        JButton sendButton = new JButton("send");
        add(sendButton, BorderLayout.SOUTH);
        sendButton.addActionListener(this);
    }

    // =================================================================================================================================================================

    private static void accessServer() throws InterruptedException {

        try {
            host = InetAddress.getByName(userInputIPAddress);
        } catch (UnknownHostException uhEx) {
            messageThread.append("\nHost ID not found!");
            uhEx.printStackTrace();
            Thread.sleep(5000);
            System.exit(1);
        }

        link = null;
        try {
            link = new Socket(host, PORT);
            input = new Scanner(link.getInputStream());
            output = new PrintWriter(link.getOutputStream(), true);
        } catch (ConnectException connectEx) {
            messageThread.append("\nthere was an error connecting to your friend. maybe they're not using their messenger right now?");
            connectEx.printStackTrace();
            Thread.sleep(5000);
            System.exit(1);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }

        messageThread.append("you are now connected to your friend! \n" +
                "type a message, or if you'd like to disconnect, enter '***CLOSE***' at any time.\n");
    }

    // =================================================================================================================================================================

    @Override
    public void actionPerformed(ActionEvent e) {

        if (firstRun == true) { // !!!!!!!
            userInputIPAddress = messageField.getText();
            messageField.setText("");
            firstRun = false;
            try {
                accessServer();
            } catch (InterruptedException ex) { // try/catch blocks necessary for Thread.sleep(5000) in accessServer()
                throw new RuntimeException(ex);
            }
            return;
        }

        messageToSend = messageField.getText();
        messageField.setText("");
        if (messageToSend.equals("***CLOSE***")) {
            try {
                link.close();
                messageThread.append("\nthank you for using this instant message service! goodbye!");
                System.exit(0);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                messageThread.append("Unable to disconnect!");
                System.exit(1);
            }
        }

        messageThread.append("\nyou: " + messageToSend);
        output.println(messageToSend);
    }

    // =================================================================================================================================================================
}