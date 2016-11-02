package mygamesclient;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class MGCLobby extends javax.swing.JFrame {
    private MGCMainWindow mainWindow = null;
    private String name = "";
    private MyGamesClient client = null;
    private boolean isHost = false;
    
    private int id = 0;
    private String host = "";
    public String game = "";
    
    DefaultListModel playersListModel = null;
    
    private StyledDocument chatDoc = null;
    private SimpleAttributeSet selfNameStyle = null;
    private SimpleAttributeSet otherNameStyle = null;
    private SimpleAttributeSet messageNameStyle = null;
    
    public MGCLobby(int id, String host, String game, MGCMainWindow mainWindow, MyGamesClient client) {
        this.id = id;
        this.host = host;
        this.game = game;
        this.mainWindow = mainWindow;
        this.client = client;
        
        if (host.equals(mainWindow.name)) {
            isHost = true;
        }
        name = mainWindow.name;
        
        setTitle(game+" Lobby");
        playersListModel = new DefaultListModel();
        initComponents();
        
        selfNameStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(selfNameStyle, Color.RED);
        StyleConstants.setBold(selfNameStyle, true);
        
        otherNameStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(otherNameStyle, Color.BLUE);
        StyleConstants.setBold(otherNameStyle, true);
        
        messageNameStyle = new SimpleAttributeSet();
        
        chatDoc = chatTextPane.getStyledDocument();
        
        DefaultCaret caret = (DefaultCaret)chatTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        addWindowListener(new MGCLobbyWindowListener());
    }
    
    public void updatePlayersList(String[] players) {
        playersListModel.clear();
        for (String player : players) {
            playersListModel.addElement(player);
        }
    }
    
    public void notifyHostDisconnect() {
        statusLabel.setText("Host has disconnected.");
        readyButton.setEnabled(false);
        startButton.setEnabled(false);
        sendButton.setEnabled(false);
        host = "";
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playersConnectedScrollPane = new javax.swing.JScrollPane();
        playersConnectedList = new javax.swing.JList();
        playersConnectedLabel = new javax.swing.JLabel();
        chatScrollPane = new javax.swing.JScrollPane();
        chatTextPane = new javax.swing.JTextPane();
        chatLabel = new javax.swing.JLabel();
        sendField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        readyButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        startButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        playersConnectedList.setModel(playersListModel);
        playersConnectedScrollPane.setViewportView(playersConnectedList);

        playersConnectedLabel.setText("Players Connected:");

        chatScrollPane.setAutoscrolls(true);

        chatTextPane.setEditable(false);
        chatScrollPane.setViewportView(chatTextPane);

        chatLabel.setText("Chat:");

        sendField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFieldActionPerformed(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        readyButton.setText("Ready");
        readyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readyButtonActionPerformed(evt);
            }
        });

        statusLabel.setText("Waiting for other players...");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        startButton.setText("Start");
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playersConnectedLabel)
                            .addComponent(playersConnectedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(statusLabel))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(readyButton)
                        .addGap(18, 18, 18)
                        .addComponent(startButton)
                        .addGap(43, 43, 43)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(chatScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sendField, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playersConnectedLabel)
                            .addComponent(chatLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playersConnectedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startButton)
                                    .addComponent(readyButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(statusLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chatScrollPane)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(sendField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sendButton))
                                .addContainerGap())))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        mainWindow.startNewGame(id);
    }//GEN-LAST:event_startButtonActionPerformed

    private void readyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readyButtonActionPerformed
        client.sendMessage("server", "###player_ready###id="+id+"###player="+name+"###");
        readyButton.setEnabled(false);
    }//GEN-LAST:event_readyButtonActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        sendToPlayers();
    }//GEN-LAST:event_sendButtonActionPerformed

    private void sendFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFieldActionPerformed
        sendToPlayers();
    }//GEN-LAST:event_sendFieldActionPerformed

    public void allReady(boolean yes) {
        if (isHost) {
            startButton.setEnabled(yes);
        }
        
        if (yes) {
            statusLabel.setText("Everyone ready, waiting for host.");
        } else {
            statusLabel.setText("Waiting for other players...");
        }
    }
    
    private void sendToPlayers() {
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
    
    public void recievedMessage(String sender, String message) {
        if (!sender.equals(name)) {
            try {
                chatDoc.insertString(chatDoc.getLength(), sender+": ", otherNameStyle);
                chatDoc.insertString(chatDoc.getLength(), message + "\n", messageNameStyle);
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel chatLabel;
    private javax.swing.JScrollPane chatScrollPane;
    private javax.swing.JTextPane chatTextPane;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel playersConnectedLabel;
    private javax.swing.JList playersConnectedList;
    private javax.swing.JScrollPane playersConnectedScrollPane;
    private javax.swing.JButton readyButton;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField sendField;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
    
    // on window close listener
    private class MGCLobbyWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            mainWindow.notifyLobbyClose(id, host);
        }
    }
}
