package patterns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Selectorcillo {
    private static final int BUFFER_SIZE = 1024;
    private final static int DEFAULT_PORT = 9000;

    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private InetAddress hostAddress = null;

    private Set<SocketChannel> clientChannels;
    private ServerSocketChannel serverSocket = null;
    private Selector selector = null;

    public Selectorcillo() {
        try {
            clientChannels = new HashSet<>();
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.socket().bind(new InetSocketAddress(hostAddress, DEFAULT_PORT));
            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception exc) {
            System.out.println("SERVER LOG: EXIT");
            exc.printStackTrace();
            System.exit(1);
        }
        System.out.println("SERVER R LOG: Server started and ready for handling requests");
    }

    public void run() {
        boolean running = true;

        while (running) try {
            selector.select();
            for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                SelectionKey key = (SelectionKey) i.next();
                if (key.isAcceptable()) {
                    accept(key);
                }
                if (key.isReadable()) {
                    read(key);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void accept(SelectionKey key) throws IOException {
        SocketChannel sc = serverSocket.accept();
        clientChannels.add(sc);
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException{
        String msg = null;
        int numRead = 0;
        SocketChannel cc = (SocketChannel) key.channel();
        if (!cc.isOpen()) {
            return;
        }
        try {
            buffer.clear();
            while ((numRead = cc.read(buffer)) > 0) {
                buffer.flip();
                msg = new String(trim(buffer.array()));
                buffer.clear();
            }
            write(msg, cc);
        } catch (Exception exc) {
            clientChannels.remove(cc);
            System.out.println("SERVER E LOG: " + exc.getMessage());
            try {
                cc.close();
                cc.socket().close();
            } catch (Exception e) {
            }
        }
        if (numRead == -1) {
            System.out.println("Graceful shutdown");
            key.channel().close();
            key.cancel();

            return;
        }
    }

    private void write(String message, SocketChannel channel){
        for (Iterator<SocketChannel> i = clientChannels.iterator(); i.hasNext(); )
            try {
                SocketChannel client = i.next();
                ByteBuffer encodedMessage = ByteBuffer.wrap(message.getBytes("UTF-8"));
                if (client.isConnected()) {
                    client.write(encodedMessage);
                    System.out.println("Sent a message to a client!");
                } else {
                    i.remove();
                }
            } catch (IOException e) {
                System.out.println("SERVER D LOG: " + e.getMessage());
                i.remove();
            }
    }

    public static void main(String[] args) {
        new Selectorcillo().run();
    }

    private byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0){
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }
}
