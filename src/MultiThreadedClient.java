import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MultiThreadedClient
{
    static class ClientTask extends Thread
    {
        private final String address;
        private final int port;
        private final String command;

        public ClientTask(String address, int port, String command)
        {
            this.address = address;
            this.port = port;
            this.command = command;
        } //end ClientTask

        public void run()
        {
            try
            {
                long start = System.currentTimeMillis();

                Socket socket = new Socket(address, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(command);

                String response;
                while ((response = in.readLine()) != null)
                {
                    System.out.println(response);
                }
                socket.close();
                long end = System.currentTimeMillis();
                System.out.println("Turn-around time: " + (end - start) + " ms\n");
            }
            catch (IOException e)
            {
                System.out.println("Error:" + e.getMessage());
            }
        } //end run
    } //end static class

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter server address: ");
        String address = scanner.nextLine();

        System.out.println("\nEnter port number: ");
        int port = scanner.nextInt();
        scanner.nextLine();

        boolean run = true;
        boolean valid = false;
        int count = 0;
        int[] allowed = {1, 5, 10, 15, 20, 25};
        String command = null;
        while (run) {
            System.out.println("\nEnter command (Date and Time, Uptime, Memory Use, Netstat, Current Users, Running Processes, Quit): ");
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("Quit") || command.equalsIgnoreCase("quit")) {
                System.out.println("Quitting client program ...");
                run = false;
                break;
            }
            // while loop to validate number of requests to generate
            while (!valid) {
                System.out.println("\nEnter number of client requests to generate (1, 5, 10, 15, 20, or 25):");
                if (scanner.hasNextInt()) {
                    count = scanner.nextInt();
                    scanner.nextLine(); // consume newline character
                    for (int a : allowed) {
                        if (count == a) {
                            valid = true;
                            break;
                        } // end if
                    } // end enhanced for
                    if (!valid) {
                        System.out.println("Invalid input. Please enter one of the allowed values (1, 5, 10, 15, 20, or 25).");
                    } // end if
                } else {
                    System.out.println("Please enter a valid number.");
                    scanner.nextLine(); // consume invalid input
                } // end else-if
            } // end while loop for client requests
            long totalTime = 0;
            Thread[] threads = new Thread[count];
            for (int i = 0; i < count; i++) {
                threads[i] = new ClientTask(address, port, command);
                long start = System.currentTimeMillis();
                threads[i].start();
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                long end = System.currentTimeMillis();
                totalTime += (end - start);
            }
            if (run) {
                System.out.println("Total turn-around time: " + (double) totalTime + " ms");
                System.out.println("Average turn-around time: " + (totalTime / (double) count) + " ms");
                totalTime = 0;
                valid = false;
            } // end if
        } // end while loop for command input
        scanner.close();
    } //end main
} //end MultiThreaded Client class
