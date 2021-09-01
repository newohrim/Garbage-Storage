import java.io.File;
import java.util.Scanner;

public class Main
{
    private static final int PARAMS_COUNT = 3;

    public static void main(String[] args)
    {
        FileTorrent torrent = initialize(args);
        try
        {
            Thread torrentThread = new Thread(torrent);
            torrentThread.setDaemon(true);
            torrentThread.start();
            //torrentThread.join();
            Scanner adminInput = new Scanner(System.in);
            while (true)
            {
                String command = adminInput.next().trim();
                switch (command)
                {
                    case "update":
                        torrent.getFiles();
                        break;
                }
                if (command.equals("shutdown"))
                {
                    torrent.terminate();
                    break;
                }
            }
            torrentThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static FileTorrent initialize(String[] args)
    {
        int i = 0;
        String pathToRoot = "";
        int port = 0;
        if (args.length >= PARAMS_COUNT - 1)
        {
            try
            {
                pathToRoot = args[0];
                i = 1;
                port = Integer.parseInt(args[1]);
                i = 2;
                if (args.length >= PARAMS_COUNT)
                {
                    String password = args[2];
                    return new FileTorrent(pathToRoot, port, password);
                }
            }
            catch (ClassCastException | NumberFormatException | IndexOutOfBoundsException e)
            {
                System.out.println("Input arguments format exception.");
            }
        }
        if (i == 2)
        {
            String password = askForPassword(new Scanner(System.in));
            return new FileTorrent(pathToRoot, port, password);
        }
        else
        {
            return askForInput();
        }
    }

    private static FileTorrent askForInput()
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Input path to root directory: ");
            String pathToRoot = scanner.next().trim();
            System.out.print("\n");
            System.out.print("Input server port: ");
            int port = scanner.nextInt();
            System.out.print("\n");
            String password = askForPassword(scanner);
            return new FileTorrent(pathToRoot, port, password);
        }
        catch (Exception e)
        {
            System.out.println("Input format exception.");
            return askForInput();
        }
    }

    private static String askForPassword(Scanner scanner)
    {
        System.out.print("Input password if necessary: ");
        return scanner.next().trim();
    }
}
