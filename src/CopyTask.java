import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CopyTask {
    private final int BUFFERSIZE = 10485760;
    
    private MyFile from;
    private String to;
    private String directoryTo;
    private PartialBackup last;

    public CopyTask(MyFile from, String to, String directoryTo,
            PartialBackup last) {
        this.from = from;
        this.to = to;
        this.directoryTo = directoryTo;
        this.last = last;
    }

    public MyFile getSourceFile() {
        return from;
    }

    public void doTask() {
        InputStream is = null;
        OutputStream os = null;
        try {
            new File(directoryTo).mkdirs();
            is = new FileInputStream(new File(from.getAbsolutePath()));
            os = new FileOutputStream(new File(to));
            byte[] buffer = new byte[BUFFERSIZE];
            int length;
            while ((length = is.read(buffer)) > 0) {
                Progresser.setProgress(Progresser.FILECOPY, length);
                os.write(buffer, 0, length);
            }
        }
        catch(IOException e) {
            ErrorHandler.showErr(e);
        }
        finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                ErrorHandler.showErr(e);
            }
        }
        
        try {
            if(last != null)
                last.createFullFileLink(from);
        } catch (IOException e) {
            ErrorHandler.showErr(e);
        }
    }
}
