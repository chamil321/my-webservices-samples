package echoServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * This is netty simple Echo server with default config
 *
 * To run the package:
 *  mvn clean package
 *  mvn exec:java
 *
 * Use following client
 *  curl -v http://localhost:8880/ -d "hello"
 *
 * This class is responsible for bootStrapping of the server.
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() + "<port>");
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    private void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(); // register channels to be used by the eventLoop.
        ServerBootstrap b = new ServerBootstrap(); //server channel initialization.
        try {
            b.group(group) // specify EventLoopGroup which is used for the parent (acceptor) and the child (client).
                    .channel(NioServerSocketChannel.class) // implementation which uses NIO selector based implementation to accept new connections.
                    // or simple the connection created to given address

                    .localAddress(new InetSocketAddress(port)) // socketAddress which is used to bind the local "end" to.
                    // Update channel pipeline of each new channel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            ChannelFuture f = b.bind().sync(); // Bind the server; sync wait until the bind completes. (The call to sync() causes the current Thread to block until then.)
            System.out.println(EchoServer.class.getName() + " started on " + f.channel().localAddress());
            f.channel().closeFuture().sync(); // application will wait until the server’s Channel closes since we sync on close future
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
