package patterns;

import javax.swing.*;
import java.awt.*;
import java.nio.channels.SocketChannel;

public class View {
    private String      appName     = "Chat v0.1";
    private JFrame      newFrame    = new JFrame(appName);
    private JButton     sendMessage;
    private JTextField  messageBox;
    private JTextArea   chatBox;

    public View(SocketChannel socket){


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());


        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);

        messageBox = new JTextField(100);
        messageBox.requestFocusInWindow();

        sendMessage = new JButton("Send Message");

        Controller controller = new Controller(messageBox, chatBox, socket);
        messageBox.addActionListener(controller);
        sendMessage.addActionListener(controller);

        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);

        mainPanel.add(BorderLayout.SOUTH, southPanel);

        newFrame.add(mainPanel);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(800, 500);
        newFrame.setVisible(true);
        centreWindow(newFrame);
    }

    public static void centreWindow(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
    }

    public void setText(String msg){
        chatBox.append(msg);
    }
}
