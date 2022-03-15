import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private final AESCrypto cryptic = new AESCrypto();

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(this.username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner userInput = new Scanner(System.in);
            while (socket.isConnected()) {
                String userMsg = userInput.nextLine();
                String messageToSend = cryptic.encryption(username + ": " + userMsg);
                bufferedWriter.write("User: " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGGroup;
            while (socket.isConnected()) {
                try {
                    msgFromGGroup = bufferedReader.readLine();
                    String[] messages = msgFromGGroup.split(": ");
                    if (messages[0].equals("SERVER"))
                        System.out.println(msgFromGGroup);
                    else if (messages[0].equals("User")) {
                        String msgToShow = cryptic.decryption(messages[1]);
                        System.out.println(msgToShow);
                    }

                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat: ");
        String userName = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, userName);
        client.listenForMessage();
        client.sendMessage();
    }
}
