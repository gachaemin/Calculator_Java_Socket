import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A simple client application for interacting with the arithmetic calculator server.
 */
public class HW1_Client {
    public static void main(String[] args) {
        BufferedReader in = null;
        BufferedWriter out = null;
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);
        try {
            // Connect to the server on localhost and port 9999
            socket = new Socket("localhost", 9999);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                System.out.println("Enter an arithmetic expression (separated by spaces): ");
                String outputMessage = scanner.nextLine(); // Read the arithmetic expression from the keyboard

                if (outputMessage.equalsIgnoreCase("bye")) {
                    // If the user enters "bye," send it to the server and break out of the loop
                    out.write(outputMessage + "\n");
                    out.flush();
                    break;
                }

                // Send the arithmetic expression to the server
                out.write(outputMessage + "\n");
                out.flush();

                // Receive and print the result from the server
                String inputMessage = in.readLine();
                System.out.println("Calculation result: " + inputMessage);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                // Close resources and handle potential exceptions
                scanner.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                System.out.println("An error occurred while chatting with the server.");
            }
        }
    }
}
