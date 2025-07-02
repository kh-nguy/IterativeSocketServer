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

        System.out.println("\nEnter command (Date and Time, Uptime, Memory Use, Netstat, Current Users, Running Processes): ");
        String command = scanner.nextLine();

        System.out.println("\nEnter number of client requests to generate (1, 5, 10, 15, 20, or 25):");
        int count = scanner.nextInt();

        long totalTime = 0;
        Thread[] threads = new Thread[count];

        for (int i = 0; i < count; i++)
        {
            threads[i] = new ClientTask(address, port, command);
            long start = System.currentTimeMillis();
            threads[i].start();

            try
            {
                threads[i].join();
            }
            catch (InterruptedException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            long end = System.currentTimeMillis();
            totalTime += (end - start);
        }
        System.out.println("Total Turn-around Time: " + totalTime + " ms");
        System.out.println("Average Turn-around Time: " + (totalTime / (double) count) + " ms");

        scanner.close();

    } //end main
} //end MultiThreaded Client class
