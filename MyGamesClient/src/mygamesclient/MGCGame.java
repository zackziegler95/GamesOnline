package mygamesclient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * A generic game class that all other games classes must use.
 * 
 * @author Zack Ziegler
 * @version 0.9
 * @since 11/30/2012
 */
public class MGCGame extends JFrame {
    protected MyGamesClient client = null;
    protected MGCMainWindow mainWindow = null;
    protected int id = 0;
    protected String name;
    protected String gameType;
    
    protected SimpleAttributeSet selfNameStyle = null;
    protected SimpleAttributeSet otherNameStyle = null;
    protected SimpleAttributeSet messageNameStyle = null;
    
    protected StyledDocument chatDoc = null;
    
    public MGCGame(int id, MyGamesClient client, MGCMainWindow mainWindow, String gameType) {
        this.id = id;
        this.client = client;
        this.mainWindow = mainWindow;
        this.gameType = gameType;
        
        name = mainWindow.name;
        
        chatScrollPane = new javax.swing.JScrollPane();
        chatTextPane = new javax.swing.JTextPane();
        sendField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        
        selfNameStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(selfNameStyle, Color.RED);
        StyleConstants.setBold(selfNameStyle, true);
        
        otherNameStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(otherNameStyle, Color.BLUE);
        StyleConstants.setBold(otherNameStyle, true);
        
        messageNameStyle = new SimpleAttributeSet();
        
        chatDoc = chatTextPane.getStyledDocument();
        
        addWindowListener(new MGCGame.gameWindowListener());
        
        DefaultCaret caret = (DefaultCaret)chatTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chatScrollPane = new javax.swing.JScrollPane();
        chatTextPane = new javax.swing.JTextPane();
        sendField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        javax.swing.JPanel gamePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        chatScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setAutoscrolls(true);
        chatScrollPane.setFocusable(false);
        chatScrollPane.setHorizontalScrollBar(null);

        chatTextPane.setEditable(false);
        chatTextPane.setFocusable(false);
        chatScrollPane.setViewportView(chatTextPane);

        sendButton.setText("Send");

        gamePanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout gamePanelLayout = new javax.swing.GroupLayout(gamePanel);
        gamePanel.setLayout(gamePanelLayout);
        gamePanelLayout.setHorizontalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 266, Short.MAX_VALUE)
        );
        gamePanelLayout.setVerticalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(sendField, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton))
                    .addComponent(chatScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sendButton)
                            .addComponent(sendField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Called when a the send button is pressed or the field is activated, sends text through the
     * client to the person.
     */
    protected void sendText() {
        String txt = sendField.getText();
        if (!txt.equals("")) {
            if (client.sendMessage("server", "###game_message###id="+id+"###txt="+txt+"###") == 1) {
                try {
                    chatDoc.insertString(chatDoc.getLength(), "You: ", selfNameStyle);
                    chatDoc.insertString(chatDoc.getLength(), txt + "\n", messageNameStyle);
                } catch(Exception e) {
                    System.err.println(e);
                }                
                sendField.setText("");
            } else {
                System.err.println("Error writing message");
            }
        }
    }
    
    /**
     * Called from the main GUI window when the client receives a message from the partner.
     * 
     * @param message String message of the text.
     */
    public void recievedMessage(String from, String message) {
        if (!from.equals(name)) {
            try {
                chatDoc.insertString(chatDoc.getLength(), from+": ", otherNameStyle);
                chatDoc.insertString(chatDoc.getLength(), message + "\n", messageNameStyle);
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
    
    abstract class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JScrollPane chatScrollPane;
    protected javax.swing.JTextPane chatTextPane;
    protected javax.swing.JButton sendButton;
    protected javax.swing.JTextField sendField;
    // End of variables declaration//GEN-END:variables
    
    // on window close listener
    protected class gameWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (mainWindow.client != null) {
                mainWindow.alertGameClosed(id);
            }
        }
    }
}
