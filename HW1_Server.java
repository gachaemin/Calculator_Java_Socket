import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple server application that performs basic arithmetic calculations based on client requests.
 */
public class HW1_Server {

    /**
     * Calculates the result of a given arithmetic expression.
     *
     * @param exp The arithmetic expression to be evaluated.
     * @return The result of the calculation or an error message if the expression is invalid.
     */
    public static String calc(String exp) {
        StringTokenizer st = new StringTokenizer(exp, " ");
        if (st.countTokens() < 3)
            return "error: too few arguments";
        if (st.countTokens() > 3)
            return "error: too many arguments";
        String res = "";
        int op1 = Integer.parseInt(st.nextToken());
        String opcode = st.nextToken();
        int op2 = Integer.parseInt(st.nextToken());
        switch (opcode) {
            case "+":
                res = Integer.toString(op1 + op2);
                System.out.println(res);
                break;
            case "-":
                res = Integer.toString(op1 - op2);
                System.out.println(res);
                break;
            case "*":
                res = Integer.toString(op1 * op2);
                System.out.println(res);
                break;
            case "/":
                if (op2 == 0) return "error: divided by zero";
                res = Integer.toString(op1 / op2);
                System.out.println(res);
                break;
            default:
                res = "error";
        }
        return res;
    }

    /**
     * The main entry point for the server application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        try (ServerSocket listener = new ServerSocket(9999)) {
            System.out.println("Waiting for connections...");

            while (true) {
                Socket socket = listener.accept();
                System.out.println("Connected");

                Runnable worker = new ServerWorker(socket);
                executorService.execute(worker);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * A worker thread that handles communication with a specific client.
     */
    static class ServerWorker implements Runnable {
        private Socket socket;

        /**
         * Constructs a new ServerWorker instance.
         *
         * @param socket The socket associated with the client connection.
         */
        public ServerWorker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                while (true) {
                    String inputMessage = in.readLine();
                    if (inputMessage.equalsIgnoreCase("bye")) {
                        System.out.println("Connection closed");
                        break;
                    }
                    System.out.println("Received: " + inputMessage);

                    String res = calc(inputMessage);
                    out.write(res + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
