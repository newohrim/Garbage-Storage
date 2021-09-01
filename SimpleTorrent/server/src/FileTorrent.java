import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileTorrent implements Runnable {

    private final String rootDirectory;
    private final int serverPort;
    private final boolean passwordRequired;
    private final String password;

    private static FileTorrent instance;
    private boolean serverShouldClose = false;
    private File currentDirectory;
    private Map<String, FileData> fileMap;
    private FileData[] allFiles;
    private ServerSocket server;
    private boolean lock = false;

    public FileTorrent(final String rootDirectory, final int serverPort, final String password)
    {
        this.rootDirectory = rootDirectory;
        this.serverPort = serverPort;
        this.passwordRequired = !password.isBlank();
        currentDirectory = new File(this.rootDirectory);
        this.password = password;
        fileMap = new HashMap<String, FileData>();
    }

    @Override
    public void run()
    {
        if (instance != null)
        {
            System.out.println("Server instance is already online.");
            return;
        }
        instance = this;
        if(getFiles())
            if (establishServer())
                waitForRequest();
        shutdown();
        instance = null;
    }

    private boolean establishServer()
    {
        try
        {
            server = new ServerSocket(serverPort);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private void waitForRequest()
    {
        while (!serverShouldClose)
        {
            try
            {
                Socket client = server.accept();
                RequestExecutor executor = new RequestExecutor(client, this);
                Thread executorThread = new Thread(executor);
                executorThread.setDaemon(true);
                executorThread.start();
            }
            catch (SocketException e)
            {
                System.out.println("Server was interrupted.");
                return;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean getFiles()
    {
        lock = true;
        fileMap.clear();
        allFiles = null;
        try
        {
            File root = new File(rootDirectory);
            if (!root.exists())
                root.mkdir();
            File[] files = currentDirectory.listFiles();
            if (files == null)
            {
                System.out.println("Root directory doesn't exist.");
                lock = false;
                return false;
            }
            allFiles = new FileData[files.length];
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isFile())
                {
                    Path filePath = files[i].toPath();
                    FileData fd = new FileData(files[i].getName(), Files.size(filePath),
                            Files.getLastModifiedTime(filePath).toMillis(), filePath.toString());
                    allFiles[i] = fd;
                    fileMap.put(files[i].getName(), fd);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            lock = false;
        }
        lock = false;
        return true;
    }

    private void shutdown()
    {
        try
        {
            server.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean isCorrectPassword(final String password)
    {
        return password.equals(this.password);
    }

    public void terminate()
    {
        serverShouldClose = true;
        try
        {
            server.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private FileData getFile(String fileName)
    {
        return fileMap.getOrDefault(fileName, null);
    }

    public void sendFileList(final Socket client) throws IOException
    {
        System.out.println("sending file list");
        ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
        outputStream.writeObject(allFiles);
        outputStream.flush();
        System.out.println("file list sent");
        outputStream.close();
    }

    public void sendFile(final Socket client, final String fileName) throws IOException
    {
        ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
        FileData requestedFile = getFile(fileName);
        if (requestedFile == null)
        {
            System.out.println("File with name " + fileName + " doesn't exist.");
            return;
        }
        FileInputStream reader = new FileInputStream(requestedFile.getFilePath());
        int count;
        byte[] buffer = new byte[16 * 1024];
        while ((count = reader.read(buffer)) > 0)
        {
            outputStream.write(buffer, 0, count);
        }
        System.out.println("sent file");
        //byte[] content = Files.readAllBytes(Path.of(requestedFile.getFilePath()));
        //outputStream.write(content);
        reader.close();
        outputStream.flush();
        outputStream.close();
    }

    public void sendConnectionConfirmation(final Socket client, final String password) throws IOException
    {
        System.out.println("sending connection confirmation");
        ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
        outputStream.writeObject(new ConnectionForm(isCorrectPassword(password)));
        outputStream.flush();
        System.out.println("connection confirmation sent");
        outputStream.close();
    }

    public boolean isLocked()
    {
        return lock;
    }
}