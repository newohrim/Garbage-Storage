import java.io.*;
import java.net.Socket;

public class FileTorrentClient
{
    private final String ipAddress;
    private final int serverPort;

    private String password;
    private String username;
    private Socket client;
    private MainWindowController windowController;

    private boolean isDownloading = false;

    public FileTorrentClient(final String ipAddress, final int serverPort, final String password, final String username)
    {
        this.ipAddress = ipAddress;
        this.serverPort = serverPort;
        this.password = password;
        this.username = username;
    }

    public boolean establishConnection() throws IOException, ClassNotFoundException
    {
        client = new Socket(ipAddress, serverPort);
        RequestForm requestForm = new RequestForm(RequestType.CONNECTION, "", password, username);
        ObjectOutputStream outputStream = sendRequestForm(requestForm);
        ObjectInputStream responseStream = new ObjectInputStream(client.getInputStream());
        ConnectionForm response = (ConnectionForm)responseStream.readObject();
        responseStream.close();
        outputStream.close();
        client.close();
        return response.getConnectionStatus();
    }

    public FileData[] getFileList() throws IOException, ClassNotFoundException
    {
        client = new Socket(ipAddress, serverPort);
        System.out.println("getting file list");
        RequestForm requestForm = new RequestForm(RequestType.GET_FILES, "", password, username);
        ObjectOutputStream outputStream = sendRequestForm(requestForm);
        System.out.println("wait for response");
        ObjectInputStream responseStream = new ObjectInputStream(client.getInputStream());
        FileData[] response = (FileData[])responseStream.readObject();
        System.out.println("got file list");
        outputStream.close();
        responseStream.close();
        client.close();
        return response;
    }

    public void getFile(final FileData file, final String pathToDir) throws IOException
    {
        isDownloading = true;
        checkDirectoryExistence(pathToDir);
        client = new Socket(ipAddress, serverPort);
        System.out.println("getting file");
        RequestForm requestForm = new RequestForm(RequestType.DOWNLOAD_FILE, file.getFileName(), password, username);
        ObjectOutputStream outputStream = sendRequestForm(requestForm);
        System.out.println("wait for response");
        ObjectInputStream responseStream = new ObjectInputStream(client.getInputStream());
        StringBuilder pathToFile = new StringBuilder();
        pathToFile.append(pathToDir);
        if (!pathToDir.endsWith("/") && !pathToDir.endsWith("\\"))
            pathToFile.append("/");
        pathToFile.append(file.getFileName());
        writeFile(file, pathToFile.toString(), responseStream);
        System.out.println("downloaded file");
        outputStream.close();
        responseStream.close();
        client.close();
        windowController.addSavedFile(file);
        isDownloading = false;
    }

    private ObjectOutputStream sendRequestForm(final RequestForm requestForm) throws IOException
    {
        ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
        outputStream.writeObject(requestForm);
        outputStream.flush();
        return outputStream;
    }

    private void writeFile(final FileData file, final String path, final ObjectInputStream responseStream) throws IOException
    {
        FileOutputStream fileWriter = new FileOutputStream(path);
        long sum = 0;
        int count;
        byte[] buffer = new byte[16 * 1024];
        while ((count = responseStream.read(buffer)) > 0)
        {
            sum += count;
            fileWriter.write(buffer, 0, count);
            windowController.updateProgressBar((double)sum / file.getFileSize());
        }
        fileWriter.close();
    }

    private void checkDirectoryExistence(final String path)
    {
        File downloadDir = new File(path);
        if (!downloadDir.exists())
            downloadDir.mkdir();
    }

    public void changePassword(final String password)
    {
        this.password = password;
    }
    public void setWindowController(final MainWindowController controller)
    {
        windowController = controller;
    }

    public boolean isLock()
    {
        return isDownloading;
    }

    public void resetLock()
    {
        isDownloading = false;
    }
}
