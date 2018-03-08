import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BackupFolder {
    private String path;
    private List<PartialBackup> backups;
    private List<MyFile> allFiles;
    private String indexFile;
    
    private PartialBackup last = null;
    
    public BackupFolder(String destinationPath) {
        this.path = destinationPath;
        this.indexFile = path + "/index.ini";
    }
    
    public void init() throws IOException {
        try {
            backups = new ArrayList<PartialBackup>();
            BufferedReader index = new BufferedReader(new FileReader(new File(indexFile)));
            
            String line;
            while((line = index.readLine()) != null) {
                if(!line.equals("")) {
                    String[] splited = line.split("\t");
                    backups.add(new PartialBackup(path + "/" + splited[0], splited[0], this));
                }
            }
            
            index.close();
        }
        catch(IOException e) {
            createIndex();
        }
        
        for(int i = 0; i < backups.size(); i++) {
            backups.get(i).init();
        }
    }

    private void createIndex() throws IOException {
        new File(path).mkdirs();
        new File(indexFile).createNewFile();
    }

    public void addFile(MyFile toAdd) {
        allFiles.add(toAdd);
    }

    public String getPathForFiles() {
        return last.getPathForFiles();
    }

    public boolean fileAlreadyBackuped(MyFile file) {
        for(int i = 0; i < backups.size(); i++)
            if(backups.get(i) != last && backups.get(i).fileAlreadyBackuped(file))
                return true;
        
        return false;
    }

    public void createFolder() throws IOException {
        String currentTime = new SimpleDateFormat("YYYYMMdd_HHmm").format(new Date());

        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(indexFile, true)));
        w.println(currentTime);
        w.close();
        
        new File(path + "/" + currentTime).mkdir();
        last = new PartialBackup(path + "/" + currentTime, currentTime, this);
        last.createDependencies();
        backups.add(last);
    }

    public void createLink(MyFile sourceFile) throws IOException {
        for(int i = 0; i < backups.size(); i++)
            if(backups.get(i) != last && backups.get(i).fileAlreadyBackuped(sourceFile)) {
                last.createLink(sourceFile, backups.get(i).getRelativePath());
                return;
            }
    }

    public PartialBackup getLast() {
        return last;
    }

    public void createLinkToCurrent(MyFile sourceFile) throws IOException {
        last.createLink(sourceFile, last.getRelativePath());
    }

    public PartialBackup[] getBackups() {
        PartialBackup[] back = new PartialBackup[this.backups.size()];
        for(int i = 0; i < back.length; i++)
            back[i] = this.backups.get(i);
        
        return back;
    }

    public String getCorrectFilePath(MyFile myFile) {
        for(int i = 0; i < backups.size(); i++) {
            List<MyFile> allFiles = backups.get(i).getFolder().getAllFiles();
            for(int j = 0; j < allFiles.size(); j++) {
                if(allFiles.get(j).getType() == MyFile.FULL && allFiles.get(j).equals(myFile)) {
                    return allFiles.get(j).getAbsolutePath();
                }
            }
        }
        return null;
    }
}
