package patterns;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Controller implements ActionListener {
    private JTextField messageBox;
    private JTextArea   chatBox;
    private SocketChannel socket;

    public Controller(JTextField messageBox, JTextArea chatBox, SocketChannel socket){
        super();
        this.chatBox = chatBox;
        this.messageBox = messageBox;
        this.socket = socket;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if (messageBox.getText().length() < 1) {
                // do nothing
            } else if (messageBox.getText().equals(".clear")) {
                chatBox.setText("Cleared all messages\n");
                messageBox.setText("");
            } else {
                try {
                    byte[] str = messageBox.getText().getBytes();
                    ByteBuffer buffer = ByteBuffer.wrap(str);
                    socket.write(buffer);
                    buffer.clear();
                    chatBox.append("<" + System.getProperty("user.name") + ">:  " + new String(str) + "\n");
                    System.out.println(System.getProperty("user.name") + " sent:  " + new String(str));
                } catch (IOException ex) {
                    chatBox.append("<" + "Chat" + ">:  " + "Error sending message" + "\n");
                }
                messageBox.setText("");
            }
            messageBox.requestFocusInWindow();
        }
}
