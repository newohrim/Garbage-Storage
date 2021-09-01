import java.io.Serializable;

public class RequestForm implements Serializable
{
    private final static String castErrorMessage = "Failed to write";
    private final RequestType requestType;
    private final String fileName;
    private final String password;
    private final String username;

    public RequestForm(final RequestType requestType, final String fileName, final String password, final String username)
    {
        this.requestType = requestType;
        this.fileName = fileName;
        this.password = password;
        this.username = username;
    }

    public RequestType getRequestType()
    {
        return requestType;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUsername() { return username; }

    public static void reportCastError()
    {
        System.out.println(castErrorMessage);
    }
}
