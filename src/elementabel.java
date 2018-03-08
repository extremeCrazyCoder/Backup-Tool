import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public interface elementabel {
    public void init() throws NoSuchAlgorithmException;
    public void calculateHash(Callable toCallIfFinished) throws NoSuchAlgorithmException, IOException;
    public String getName();
}