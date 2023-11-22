/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view;

import static classes.ConfigFile.getVariable;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import static view.JFSistema.usuarioLogado;

/**
 *
 * @author wynvern
 */
public class JIFChat extends javax.swing.JInternalFrame {
    private Socket socket;
    private BufferedReader inV;
    private PrintWriter outV;
    private Thread reciver;
    
    
    /**
     * Creates new form JIFChat
     */
    public JIFChat() {
        initComponents();
        messagePanel.setPreferredSize(new Dimension(messagePanel.getSize().width, 0));
        messagePanel.revalidate();
        messagePanel.repaint();
        
        try {
            socket = new Socket(getVariable("hostnameChat"), Integer.parseInt(getVariable("portChat")));
            System.out.println("Connected to the server.");

            inV = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outV = new PrintWriter(socket.getOutputStream(), true);

            // Start a separate thread to continuously receive messages
            reciver = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String receivedMessage = inV.readLine();
                            System.out.println(receivedMessage);
                            if (receivedMessage != null) {
                                String[] splitted = splitStringByLastDelimiter(receivedMessage, ",");

                                String text = splitted[1];
                                String base64Image = splitted[0];, 

                                // Decode the Base64 image back to bytes
                                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                                // Now you have the text and the imageBytes, and you can do something with them
                                JPanel newMessage = createMessage(text, imageBytes, splitted[2]);
                                messagePanel.add(newMessage);

                                JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
                                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

                                messagePanel.add(separator);

                                messagePanel.revalidate();
                                messagePanel.repaint();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            reciver.start();
        } catch (IOException e) {
        }
    }
    
    public static String[] splitStringByLastDelimiter(String input, String delimiter) {
        String[] result = new String[3];

        // Find the first occurrence of the delimiter
        int firstIndex = input.indexOf(delimiter);

        // Check if the delimiter is found
        if (firstIndex != -1) {
            // Split the string based on the first occurrence of the delimiter
            result[0] = input.substring(0, firstIndex);
            result[1] = input.substring(firstIndex + delimiter.length());
        } else {
            // Delimiter not found
            result[0] = input;
            result[1] = ""; // Empty string for the second part
        }

        // Find the last occurrence of the delimiter
        int lastIndex = input.lastIndexOf(delimiter);

        // Check if the delimiter is found
        if (lastIndex != -1 && lastIndex != firstIndex) {
            // Split the string based on the last occurrence of the delimiter
            result[1] = input.substring(firstIndex + delimiter.length(), lastIndex);
            result[2] = input.substring(lastIndex + delimiter.length());
        } else {
            // Delimiter not found or only one occurrence found
            result[2] = ""; // Empty string for the third part
        }

        return result;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        message = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        messagePanel = new javax.swing.JPanel();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setTitle("Chat");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chat.png"))); // NOI18N

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/enviar.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        messagePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout messagePanelLayout = new javax.swing.GroupLayout(messagePanel);
        messagePanel.setLayout(messagePanelLayout);
        messagePanelLayout.setHorizontalGroup(
            messagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        messagePanelLayout.setVerticalGroup(
            messagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 245, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(messagePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(message)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private int messageDistance = 0;
    
    private JPanel createMessage(String message, byte[] imageData, String nomeUsuario) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        panel.setBounds(0, messageDistance, messagePanel.getWidth() - 20, 50);
        messageDistance += 56;
        
        JLabel nomeLabel = new JLabel("<html><b>" + nomeUsuario + "</b></html>", SwingConstants.CENTER);
        nomeLabel.setPreferredSize(new Dimension(nomeLabel.getPreferredSize().width, 20));

        JLabel profilePicture = new JLabel();
        profilePicture.setBounds(0, 0, 50, 50);
        ImageIcon imageIcon = new ImageIcon(imageData);
        Image imageIcoResized = imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(imageIcoResized);
        profilePicture.setIcon(imageIcon);
        
        
        JTextArea messageLabel = new JTextArea(message);
        messageLabel.setEditable(false);
        messageLabel.setWrapStyleWord(true); 
        messageLabel.setPreferredSize(new Dimension(messagePanel.getWidth() - 50 - nomeLabel.getPreferredSize().width - 20,  20));
        
        
        panel.add(profilePicture);
        panel.add(nomeLabel);
        panel.add(messageLabel);
        
        messagePanel.setPreferredSize(new Dimension(messagePanel.getSize().width, messageDistance));
        
        return panel;
    }

    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (message.getText().equals("")) return;
        
        byte[] imageBytes = usuarioLogado.getImagemPerfil();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String messageToSend = base64Image + "," + message.getText() + "," + usuarioLogado.getNome();
        outV.println(messageToSend);

        JPanel newMessage = createMessage(message.getText(), usuarioLogado.getImagemPerfil(), usuarioLogado.getNome());
        messagePanel.add(newMessage);
        
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        
        messagePanel.add(separator);

        messagePanel.revalidate();
        messagePanel.repaint();
        
        message.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField message;
    private javax.swing.JPanel messagePanel;
    // End of variables declaration//GEN-END:variables
}
