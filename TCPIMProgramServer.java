import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;


public class TCPIMProgramServer extends JFrame
        implements ActionListener {
    private static ServerSocket serverSocket;
    private static final int PORT = 4444;
    private static Socket link;
    private static Scanner input;
    private static PrintWriter output;

    private static JTextArea messageField;
    private static JTextArea messageThread;

    private static String incomingMessage = "";
    private static String outgoingMessage = "";

    public static void main(String[] args) throws InterruptedException {  // InterruptedException just for Thread.sleep
        TCPIMProgramServer frame = new TCPIMProgramServer();
        frame.setSize(600,500);
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

        try {
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }

        handleClient();

        while (!outgoingMessage.equals("***CLOSE***")) {
            try {
                incomingMessage = input.nextLine();
            }
            catch (NoSuchElementException noSuchElementEx) {
                messageThread.append("\nyour friend has disconnected!\nthank you for using this program!");
                messageField.setEnabled(false);
                Thread.sleep(5000);
                break;
            }
            messageThread.append("your friend said: " + incomingMessage + "\n");
        }

        try {
            link.close();
            System.exit(0);
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
            messageThread.append("Unable to disconnect!");
            System.exit(1);
        }
    }

    // =================================================================================================================================================================


    public TCPIMProgramServer() {

        this.setTitle("im server");

        messageThread = new JTextArea(20 ,20);
        messageThread.setBackground(new Color(0, 55, 255));
        Font courierFont = new Font("Courier", Font.PLAIN, 14);
        messageThread.setFont(courierFont);
        messageThread.setMaximumSize(new Dimension(250, 500));
        messageThread.setLineWrap(true);
        messageThread.setWrapStyleWord(true);
        messageThread.setAutoscrolls(true);
        messageThread.setEnabled(false);
        messageThread.setDisabledTextColor(Color.BLACK);
        add(new JScrollPane(messageThread), BorderLayout.NORTH);

        messageField = new JTextArea(4, 20);
        messageField.setSize(2, 10);
        messageField.setFont(courierFont);
        messageField.setBackground(new Color(67, 171, 208));
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
        messageField.setAutoscrolls(true);
        messageField.setBorder(new LineBorder(Color.BLUE, 1));
        messageField.setEnabled(false);
        add(new JScrollPane(messageField));

        JButton sendButton = new JButton("Send");
        add(sendButton, BorderLayout.SOUTH);
        sendButton.addActionListener(this);
        messageThread.append("welcome! please wait for a friend to connect!\n\n");
    }

// =================================================================================================================================================================

    private static void handleClient() {
        link = null;
        try {
            link = serverSocket.accept();
            messageThread.append("your friend has connected! (✿◠‿◠) send a message, if you'd like." +
                    "\nif you'd like to disconnect, enter '***CLOSE***' at any time.\n\n");
            messageField.setEnabled(true);
            input = new Scanner(link.getInputStream());
            output = new PrintWriter(link.getOutputStream(), true);
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

// =================================================================================================================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        outgoingMessage = messageField.getText();
        if (outgoingMessage.equals("***CLOSE***")) {
            try {
                link.close();
                messageThread.append("\nthank you for using this instant message service! goodbye! ");
                System.exit(0);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }

        messageField.setText("");
        messageThread.append("you: " + outgoingMessage + "\n");
        output.println(outgoingMessage);
    }

// =================================================================================================================================================================

}