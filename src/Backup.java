import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class Backup implements Callable {
    final public static int FULL = 1;
    final public static int INTELLIGENT = 2;
    private MyFolder source;
    private String destinationPath;
    private int mode;
    
    public void doBackup(String destinationPath, String sourcePath, int mode) throws IOException, NoSuchAlgorithmException {
        this.destinationPath = destinationPath;
        this.mode = mode;
        Progresser.setStep(Progresser.INIT);
        source = new MyFolder(sourcePath);
        new Thread(new Runnable() {@Override public void run(){start();}}).start();
    }
    
    protected void start() {
        try{
            source.init();
            call();
        }catch(NoSuchAlgorithmException e) {
            ErrorHandler.showErr(e);
        }
    }

    public void call() {
        try {
            if(Progresser.getCurrentStep() == Progresser.INIT)
                internalSecondPartOfBackup();
            else if(Progresser.getCurrentStep() == Progresser.CALCULATEHASH)
                internalThirdPartOfBackup();
        } catch (IOException e) {
            ErrorHandler.showErr(e);
        }
    }
    
    private void internalSecondPartOfBackup() {
        Progresser.setStep(Progresser.CALCULATEHASH);
        Progresser.setFiles(source.getAllFiles());
        
        source.calculateHash(this);
    }
    
    private void internalThirdPartOfBackup() throws IOException {
        BackupFolder destination = new BackupFolder(destinationPath);
        destination.init();
        destination.createFolder();
        
        Progresser.setStep(Progresser.CALCULATETODO);
        List<CopyTask> toCopy;
        if(mode == INTELLIGENT) {
            toCopy = calculateChanges(source, destination);
        }
        else if(mode == FULL) {
            List<MyFile> sourceFiles = source.getAllFiles();
            toCopy = new ArrayList<CopyTask>();
            for(int i = 0; i < sourceFiles.size(); i++) {
                String relativePath = sourceFiles.get(i).getRelativePath() +
                        "/" + CharChanger.codeNormal(sourceFiles.get(i).getName());
                toCopy.add(new CopyTask(sourceFiles.get(i),
                        destination.getPathForFiles() + relativePath,
                        destination.getPathForFiles() +
                        sourceFiles.get(i).getRelativePath(),
                        destination.getLast()));
            }
        }
        else
            return;
        
        List<MyFile> filesToCopy = new ArrayList<MyFile>();
        for(int i = 0; i < toCopy.size(); i++) {
            filesToCopy.add(toCopy.get(i).getSourceFile());
        }
        Progresser.setStep(Progresser.COPY);
        Progresser.setFiles(filesToCopy);
        doCopy(toCopy, filesToCopy);
        Progresser.setStep(Progresser.FINISHED);
    }
    
    private void doCopy(List<CopyTask> toCopy, List<MyFile> filesToCopy) {
        for(int i = 0; i < toCopy.size(); i++) {
            Progresser.setFile(filesToCopy.get(i));
            toCopy.get(i).doTask();
        }
    }

    //Calculates the Changes and writes the non-Changed files into the Backup
    private List<CopyTask> calculateChanges(MyFolder source, BackupFolder destination) throws IOException {
        List<CopyTask> toCopy = new ArrayList<CopyTask>();
        List<MyFile> sourceFiles = source.getAllFiles();
        
        for(int i = 0; i < sourceFiles.size(); i++) {
            if(destination.fileAlreadyBackuped(sourceFiles.get(i))) {
                destination.createLink(sourceFiles.get(i));
            }
            else if(insideOf(sourceFiles.get(i), sourceFiles, i)) {
                sourceFiles.get(i).setType(MyFile.LINK);
                destination.createLinkToCurrent(sourceFiles.get(i));
            }
            else {
                String relativePath = sourceFiles.get(i).getRelativePath() + "/" +
                        CharChanger.codeNormal(sourceFiles.get(i).getName());
                toCopy.add(new CopyTask(sourceFiles.get(i),
                        destination.getPathForFiles() + relativePath,
                        destination.getPathForFiles() +
                        sourceFiles.get(i).getRelativePath(), destination.getLast()));
            }
        }
        
        return toCopy;
    }

    private boolean insideOf(MyFile toSearchFor, List<MyFile> toLook,
            int notLookAt) {
        for(int i = 0; i < toLook.size(); i++)
            if(i != notLookAt && toLook.get(i).equals(toSearchFor) &&
                    toLook.get(i).getType() == MyFile.FULL)
                return true;
        return false;
    }
}