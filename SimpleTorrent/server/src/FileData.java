import java.io.Serializable;

public class FileData implements Serializable
{
    private String fileName;
    private long fileSize;
    private long fileLastChange;
    private String filePath;

    public FileData(final String fileName, final long fileSize, final long fileLastChange, final String filePath)
    {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileLastChange = fileLastChange;
        this.filePath = filePath;
    }

    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
    public long getFileLastChange() { return fileLastChange; }
    public String getFilePath()
    {
        return filePath;
    }

    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public void setFileLastChange(long fileLastChange) { this.fileLastChange = fileLastChange; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
