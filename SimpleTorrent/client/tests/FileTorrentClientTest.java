import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileTorrentClientTest {

    private final static String ROOT_DIRECTORY = "data";
    private final static int PORT = 65535;
    private final static String PASSWORD = "1234";
    private final static String LOCAL_IP = "localhost";
    private final static String USERNAME = "qwerty";
    private final static String DOWNLOAD_DIRECTORY = "testdir";

    @Test
    void establishConnection() throws IOException, ClassNotFoundException, InterruptedException
    {
        FileTorrent torrent = new FileTorrent(ROOT_DIRECTORY, PORT, PASSWORD);
        Thread torrentThread = new Thread(torrent);
        torrentThread.start();
        FileTorrentClient torrentClient = new FileTorrentClient(LOCAL_IP, PORT, PASSWORD, USERNAME);
        assertTrue(torrentClient.establishConnection());
        torrentClient = new FileTorrentClient(LOCAL_IP, PORT, "4321", USERNAME);
        assertFalse(torrentClient.establishConnection());
        torrent.terminate();
        torrentThread.join();
    }

    @Test
    void getFileList() throws IOException, ClassNotFoundException, InterruptedException
    {
        FileTorrent torrent = new FileTorrent(ROOT_DIRECTORY, PORT, PASSWORD);
        Thread torrentThread = new Thread(torrent);
        torrentThread.start();
        FileTorrentClient torrentClient = new FileTorrentClient(LOCAL_IP, PORT, PASSWORD, USERNAME);
        FileData[] response = torrentClient.getFileList();
        assertNotNull(response);
        torrent.terminate();
        torrentThread.join();
    }

    @Test
    void getFile() throws IOException, ClassNotFoundException, InterruptedException
    {
        FileTorrent torrent = new FileTorrent(ROOT_DIRECTORY, PORT, PASSWORD);
        Thread torrentThread = new Thread(torrent);
        torrentThread.start();
        FileTorrentClient torrentClient = new FileTorrentClient(LOCAL_IP, PORT, PASSWORD, USERNAME);
        FileData[] response = torrentClient.getFileList();
        assertNotNull(response);
        for (FileData file : response)
        {
            torrentClient.getFile(file, DOWNLOAD_DIRECTORY);
        }
        torrent.terminate();
        torrentThread.join();
    }
}