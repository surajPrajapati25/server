package org.sv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private final Map<String, RequestRunner> routes;
    private final ServerSocket socket;
    private final Executor threadPool;
    private HttpHandler handler;

    public Server(int port) throws IOException {
        routes = new HashMap<>();
        threadPool = Executors.newFixedThreadPool(100);
        socket = new ServerSocket(port);
    }

    public void start() throws IOException {
        handler = new HttpHandler(routes);

        while (true) {
            Socket clientConnection = socket.accept();
            handleConnection(clientConnection);
        }
    }

    public void addRoute(HttpMethod opCode, String route, RequestRunner runner) {
        routes.put(opCode.name().concat(route), runner);
    }

    /* this handles the connection between the client and the server
     it takes the client connection and passes it to the handler
     this will handle 1 request at a time howerver we can handle multiple requests at a time by using a thread pool
    */


//    private void handleConnection(Socket clientConnection) {
//        try {
//            handler.handleConnection(clientConnection.getInputStream(), clientConnection.getOutputStream());
//        } catch (IOException ignored) {
//        }
//    }
    
    /*
     * Capture each Request / Response lifecycle in a thread
     * executed on the threadPool. asynchronously and concurrently using a thread pool to handle multiple requests at a time
     */
    private void handleConnection(Socket clientConnection) {
        Runnable httpRequestRunner = () -> {
            try {
                handler.handleConnection(clientConnection.getInputStream(), clientConnection.getOutputStream());
            } catch (IOException ignored) {
            }
        };
        threadPool.execute(httpRequestRunner);
    }

}