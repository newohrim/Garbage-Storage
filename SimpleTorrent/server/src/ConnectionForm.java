import java.io.Serializable;

public class ConnectionForm implements Serializable
{
    private final boolean success;

    public ConnectionForm(final boolean success)
    {
        this.success = success;
    }

    public boolean getConnectionStatus()
    {
        return success;
    }
}
