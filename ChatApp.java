import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatApp {
    private static final String HOST = "localhost";
    private Scanner scanner = new Scanner(System.in);
    private String userName;

    public static void main(String[] args) {
        ChatApp chatApp = new ChatApp();
        chatApp.requestUserName();
        chatApp.initiateChat();
    }

    private void requestUserName() {
        System.out.print("Enter your username: ");
        userName = scanner.nextLine();
    }

    private void initiateChat() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            System.out.println(userName + " listening on port: " + serverSocket.getLocalPort());
            System.out.print("Enter target port number: ");
            int targetPort = scanner.nextInt();
            scanner.nextLine(); 

            new Thread(new WriterHandler(targetPort, HOST, userName, scanner)).start();

            Socket connection = serverSocket.accept();
            System.out.println("Connection established with " + connection.getPort());
            handleConnection(connection);
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleConnection(Socket connection) {
        try (DataInputStream in = new DataInputStream(connection.getInputStream())) {
            while (true) {
                String message = in.readUTF();
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    connection.close();
                    System.exit(0);
                } else if (message.startsWith("transfer")) {
                    String filename = message.split(" ")[1];
                    receiveFile(filename, in);
                } else {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    

    private void receiveFile(String filename, DataInputStream in) throws IOException {
        long fileSize = in.readLong();
        FileOutputStream fileOut = new FileOutputStream("new" + filename);
        byte[] buffer = new byte[1024];
        int count;
        long totalRead = 0;
        while (totalRead < fileSize) {
            count = in.read(buffer);
            fileOut.write(buffer, 0, count);
            totalRead += count;
        }
        fileOut.close();
        System.out.println("File received: " + filename);
    }
}

class WriterHandler implements Runnable {
    private int port;
    private String host;
    private String userName;
    private Scanner scanner;

    public WriterHandler(int port, String host, String userName, Scanner scanner) {
        this.port = port;
        this.host = host;
        this.userName = userName;
        this.scanner = scanner;
    }

    public void run() {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            while (true) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    out.writeUTF("exit");
                    socket.close();
                    System.out.println("Exiting...");
                    System.exit(0);  
                } else if (message.startsWith("transfer")) {
                    handleFileTransfer(message, out);
                } else {
                    out.writeUTF(userName + ": " + message);
                }
            }
        } catch (ConnectException e) {
            System.out.println("No server found on port " + port + ". Please check the port number and try again.");
        } catch (IOException e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
    }

    private void handleFileTransfer(String message, DataOutputStream out) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("No file name specified.");
            return;
        }
        String fileName = parts[1];
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        out.writeUTF("transfer " + file.getName());
        sendFile(file, out);
    }

    private void sendFile(File file, DataOutputStream out) throws IOException {
        long fileSize = file.length();
        out.writeLong(fileSize);
        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fileIn.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
        }
        System.out.println("File sent: " + file.getName());
    }
}
