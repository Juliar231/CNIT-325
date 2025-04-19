import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private JButton btnSend;
    private JTextArea txtChat;
    private JTextField txtMessage;
    private JLabel lblInfo;
    
    public GUI() {
        // Set up the JFrame
        setTitle("Battleship Game");
        setLayout(new BorderLayout());
        
        // Text area for chat
        txtChat = new JTextArea(20, 40);
        txtChat.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtChat);
        
        // Text field for message input
        txtMessage = new JTextField(30);
        
        // Send button
        btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        // Label for game info (opponent name, game time)
        lblInfo = new JLabel("Opponent: Bob, Country: Brazil, Game Started at: 12:30 PM");
        
        // Adding components to frame
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(txtMessage);
        bottomPanel.add(btnSend);
        
        add(lblInfo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Set default behavior for window closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    public void sendMessage() {
        String message = txtMessage.getText();
        // Send the message to the server or opponent (you would add the actual logic for this)
        txtChat.append("You: " + message + "\n");
        txtMessage.setText(""); // Clear the message box after sending
    }
}
