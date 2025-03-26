package nl.stokpop.nettyclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class EnvironmentListener {

    private static final int DEFAULT_PORT = 19090;
    private volatile boolean running = false;
    private Thread serverThread;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new EnvironmentListener().start();
    }

    public EnvironmentListener start() {
        if (running) {
            log("Server is already running");
            return this;
        }

        running = true;
        serverThread = new Thread(this::runServer, "EnvironmentListener-Server");
        serverThread.setDaemon(true);
        serverThread.start();
        return this;
    }

    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log("Error closing server socket:" + e);
            }
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    private void runServer() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            log("Listening on port ... " + DEFAULT_PORT);

            while (running) {
                try (Socket clientSocket = serverSocket.accept()) {
                    processClientRequest(clientSocket);
                } catch (IOException e) {
                    if (running) {
                        log("Error accepting client connection:" + e);
                    }
                }
            }
        } catch (IOException e) {
            log("Error starting server:" + e);
        } finally {
            closeServerSocket();
        }
    }

    private void processClientRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()))) {
            String message = in.readLine();
            int delayMillis = parseDelayMessage(message);
            if (delayMillis >= 0) {
                log("Delaying message by "+delayMillis+" ms");
                DelayedMessageHandler.changeDelay(delayMillis);
            }
        } catch (Exception e) {
            log("Error processing client message:" + e);
        }
    }

    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log("Error closing server socket:" + e);
            }
        }
    }

    private static final String VALUE_SEPARATOR = "=";
    private static final String DELAY_MILLIS_KEY = "DELAYED_MESSAGE_HANDLER_DELAY_MILLISECONDS";
    private static final int MIN_DELAY_MILLIS = 0;
    private static final int MAX_DELAY_MILLIS = 60000; // 1 minute max delay

    private int parseDelayMessage(String message) {
        if (message == null) {
            return -1;
        }

        String[] parts = message.split(VALUE_SEPARATOR, 2);
        if (parts.length != 2 || !DELAY_MILLIS_KEY.equals(parts[0])) {
            return -1;
        }

        try {
            int delayMillis = Integer.parseInt(parts[1].trim());
            if (delayMillis < MIN_DELAY_MILLIS || delayMillis > MAX_DELAY_MILLIS) {
                log("Delay value out of range: " + delayMillis);
                return -1;
            }
            log("Parsed delay: " + delayMillis + " ms");
            return delayMillis;
        } catch (NumberFormatException e) {
            log("Invalid delay value format: " + parts[1]);
            return -1;
        }
    }

    private void log(String message) {
        System.out.println("[EnvironmentListener] " + message);
    }
}
