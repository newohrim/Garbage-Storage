import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileTorrentTest {

    private final static String ROOT_DIRECTORY = "data";
    private final static int PORT = 65535;
    private final static String PASSWORD = "1234";

    @Test
    void getFiles()
    {
        FileTorrent torrent = new FileTorrent(ROOT_DIRECTORY, PORT, PASSWORD);
        torrent.getFiles();
    }
}