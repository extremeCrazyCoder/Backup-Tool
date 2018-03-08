import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class PartialBackup {
    private String absolutePath;
    private String relativePath;
    private String indexFile;
    private MyFolder folder;
    private BackupFolder parent;
    private int charChangerVersion;
    
    public PartialBackup(String absolutePath, String relativePath, BackupFolder parent) {
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
        this.indexFile = absolutePath + "/index.ini";
        this.parent = parent;
    }
    
    public void createDependencies() throws IOException {
        new File(absolutePath + "/data").mkdirs();
        new File(indexFile).createNewFile();
        
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(indexFile, true)));
        w.println(CharChanger.getCurrentVerion());
        w.close();
    }
    
    public void init() throws IOException {
        List<MyFile> files = new ArrayList<MyFile>();
        BufferedReader index = new BufferedReader(new FileReader(
                new File(indexFile)));
        
        String line = index.readLine();
        this.charChangerVersion = Integer.parseInt(line);
        while((line = index.readLine()) != null) {
            if(!line.equals("")) {
                String[] splited = line.split("\t");
                files.add(new MyFile(splited, absolutePath.substring(0,
                        absolutePath.length() - relativePath.length()),
                        parent, charChangerVersion));
            }
        }
        
        index.close();
        
        folder = new MyFolder(absolutePath + "/data", getName());
        for(int i = 0; i < files.size(); i++) {
            folder.sortIn(files.get(i), files.get(i).getRelativePath());
        }
        
        folder.sortElements(MyFolder.NAMEUP);
    }
    
    public boolean fileAlreadyBackuped(MyFile file) {
        return folder.contains(file);
    }
    
    public void createLink(MyFile sourceFile, String relativePath) throws IOException {
        //type(l...link, f...full) \t filename \t relativePath \t backupFolderName \t md5 \t sha-256 \t sha-512 \t Filesize
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(indexFile, true)));
        
        w.println("l\t" + CharChanger.codeNormal(sourceFile.getName()) +
                "\t" + sourceFile.getRelativePath() +
                "\t" + relativePath +
                "\t" + sourceFile.getStringHashMD5() +
                "\t" + sourceFile.getStringHashSHA256() +
                "\t" + sourceFile.getStringHashSHA512() +
                "\t" + sourceFile.getSize());
        
        w.close();
    }

    public String getPathForFiles() {
        return  absolutePath + "/data";
    }

    public void createFullFileLink(MyFile from) throws IOException {
        //type(l...link, f...full) \t filename \t relativePath \t backupFolderName \t md5 \t sha-256 \t sha-512 \t Filesize
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(indexFile, true)));
        
        w.println("f\t" + CharChanger.codeNormal(from.getName()) + "\t" + from.getRelativePath() +
                "\t" + this.relativePath + "\t" + from.getStringHashMD5() +
                "\t" + from.getStringHashSHA256() + "\t" + from.getStringHashSHA512() +
                "\t" + from.getSize());
        
        w.close();
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    private String getName() {
        return relativePath.replaceAll("/", "").replaceAll("\\\\", "");
    }

    public MyFolder getFolder() {
        return folder;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
