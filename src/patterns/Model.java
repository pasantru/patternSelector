package patterns;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Model {
    public static void main(String argv[]){

        SocketChannel socket = null;
        InetSocketAddress address;
        View view;
        try {
            address = new InetSocketAddress("localhost", 9000);
            socket = SocketChannel.open(address);
            UIManager.setLookAndFeel(UIManager
                    .getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(socket!=null){
            view  = new View(socket);
            while(true){
                ByteBuffer buff = ByteBuffer.allocate(1024);
                try {
                    buff.clear();
                    socket.read(buff);
                    view.setText("<user>:  " + new String(buff.array()) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buff.compact();
                buff.clear();
            }

        } else System.err.println("Error creating the socket");
        return;
    }
}
