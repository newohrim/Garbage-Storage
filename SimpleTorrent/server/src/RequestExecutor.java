import java.io.*;
import java.net.Socket;

public class RequestExecutor implements Runnable {
    private final Socket client;
    private final FileTorrent torrent;

    public RequestExecutor(final Socket client, final FileTorrent torrent)
    {
        this.client = client;
        this.torrent = torrent;
    }

    @Override
    public void run()
    {
        waitUntilUnlocked();
        proceedRequest();
    }

    private void proceedRequest()
    {
        try
        {
            ObjectInputStream dataStream = new ObjectInputStream(client.getInputStream());
            RequestForm request = (RequestForm)dataStream.readObject();
            System.out.printf("%s connected from %s with form type %s\n",
                    request.getUsername(),
                    client.getInetAddress().toString(),
                    request.getRequestType().toString());
            switch (request.getRequestType())
            {
                case CONNECTION:
                    torrent.sendConnectionConfirmation(client, request.getPassword());
                    break;
                case GET_FILES:
                    torrent.sendFileList(client);
                    break;
                case DOWNLOAD_FILE:
                    torrent.sendFile(client, request.getFileName());
                    break;
                case UPLOAD_FILE:
                    break;
                default:
                    System.out.println("Unsupported request type " + request.getRequestType().name());
            }
            dataStream.close();
            client.close();
        }
        catch (ClassCastException e)
        {
            RequestForm.reportCastError();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void waitUntilUnlocked()
    {
        while (torrent.isLocked()) ;
    }
}
