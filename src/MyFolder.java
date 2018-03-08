import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MyFolder implements elementabel, Callable {
    public static final int NAMEUP = 1;
    
    private String path;
    private List<elementabel> subElements = new ArrayList<elementabel>();
    private List<MyFile> subFiles = new ArrayList<MyFile>();
    private MyFolder parent;
    private String relativePath;
    private Callable toCall;
    private int subElementCalculating;
    private String name;
    
    public MyFolder(String absolutePath, MyFolder parent, String relativePath, String name) {
        path = absolutePath;
        this.parent = parent;
        this.relativePath = relativePath;
        this.name = name;
    }
    
    public MyFolder(String absolutePath, String name) {
        this(absolutePath, null, "", name);
    }
    
    public MyFolder(String absolutePath) {
        this(absolutePath, null, "", null);
    }

    @Override
    public void init() throws NoSuchAlgorithmException {
        subElements = new ArrayList<elementabel>();
        subFiles = new ArrayList<MyFile>();
        
        File thisFolder = new File(path);
        File[] listOfFiles = thisFolder.listFiles();
        
        if(listOfFiles == null) {
            return;
        }
        for(int i = 0; i < listOfFiles.length; i++) {
            if(listOfFiles[i].isFile()) {
                String absolutePath = listOfFiles[i].getAbsolutePath().replaceAll("\\\\", "/");
                absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
                MyFile newSubElement = new MyFile(absolutePath,
                        relativePath,
                        listOfFiles[i].getName(), CharChanger.getCurrentVerion());
                subElements.add(newSubElement);
                this.addFile(newSubElement);
            }
            else if(listOfFiles[i].isDirectory()) {
                subElements.add(new MyFolder(listOfFiles[i].getAbsolutePath(),
                        this,
                        relativePath + "/" + listOfFiles[i].getName(), listOfFiles[i].getName()));
            }
            subElements.get(i).init();
        }
    }
    
    @Override
    public void calculateHash(Callable toCallIfFinished) {
        this.toCall = toCallIfFinished;
        this.subElementCalculating = 0;
        if(subElements.size() > 0)
            startCalculation();
        else
            callParent();
    }
    
    private void callParent() {
        if(toCall != null)
            this.toCall.call();
    }

    private void startCalculation() {
        try {
            subElements.get(this.subElementCalculating).calculateHash(this);
        } catch (NoSuchAlgorithmException | IOException e) {
            ErrorHandler.showErr(e);
        }
    }

    public void addFile(MyFile file) {
        subFiles.add(file);
        
        if(this.parent == null) return;
        parent.addFile(file);
    }

    public List<MyFile> getAllFiles() {
        return subFiles;
    }

    public String getAbsolutePath() {
        return path;
    }
    
    public void call() {
        this.subElementCalculating++;
        if(this.subElements.size() <= this.subElementCalculating) {
            callParent();
            return;
        }
        startCalculation();
    }

    public boolean contains(MyFile file) {
        return subFiles.contains(file);
    }

    public void sortIn(MyFile myFile, String relativePath) {
        subFiles.add(myFile);
        int index = relativePath.indexOf("/", 1);
        
        if(index == -1) {
            //no / left in String
            if(relativePath.length() > 0) {
                //add last folder
                String lastFolderName = relativePath.substring(1);
                
                for(int i = 0; i < subElements.size(); i++) {
                    if(subElements.get(i) instanceof MyFolder)
                        if(((MyFolder) subElements.get(i)).getName().equals(lastFolderName)) {
                            ((MyFolder) subElements.get(i)).sortIn(myFile, "");
                            return;
                        }
                }
                
                MyFolder newFolder = new MyFolder(path + "/" + lastFolderName,
                        this, this.relativePath + "/" + lastFolderName, lastFolderName);
                subElements.add(newFolder);
                newFolder.sortIn(myFile, "");
            }
            else {
                //the current folder is the last
                subElements.add(myFile);
            }
        }
        else {
            String firstFolderName = relativePath.substring(1, index);
            String resultingString = relativePath.substring(index);
            
            for(int i = 0; i < subElements.size(); i++) {
                if(subElements.get(i) instanceof MyFolder)
                    if(((MyFolder) subElements.get(i)).getName().equals(firstFolderName)) {
                        ((MyFolder) subElements.get(i)).sortIn(myFile, resultingString);
                        return;
                    }
            }
            
            //if not found in existing Folders
            MyFolder newFolder = new MyFolder(path + "/" + firstFolderName,
                    this, this.relativePath + "/" + firstFolderName, firstFolderName);
            subElements.add(newFolder);
            newFolder.sortIn(myFile, resultingString);
        }
    }

    public String getName() {
        return name;
    }

    public List<elementabel> getSubelements() {
        return subElements;
    }

    public void sortElements(int policy) {
        //Sort own elements
        List<elementabel> newSub = new ArrayList<elementabel>();
        
        boolean done[] = new boolean[this.subElements.size()];
        int lowest;
        
        for(int i = 0; i < this.subElements.size(); i++) {
            lowest = -1;
            for(int j = 0; j < this.subElements.size(); j++) {
                if(done[j] == false) {
                    if(lowest == -1 || compareTo(this.subElements.get(lowest), this.subElements.get(j), policy)) {
                        lowest = j;
                    }
                }
            }
            
            newSub.add(this.subElements.get(lowest));
            done[lowest] = true;
        }
        
        this.subElements = newSub;

        for(int i = 0; i < this.subElements.size(); i++)
            if(this.subElements.get(i) instanceof MyFolder)
                ((MyFolder) this.subElements.get(i)).sortElements(policy);
    }

    private boolean compareTo(elementabel first, elementabel second, int policy) {
        if(policy == MyFolder.NAMEUP) {
            String nameFirst = first.getName().toLowerCase();
            String nameSecond = second.getName().toLowerCase();
            
            for(int i = 0; i < nameFirst.length() && i < nameSecond.length(); i++) {
                if(nameFirst.charAt(i) > nameSecond.charAt(i)) {
                    return true;
                }
                else if(nameFirst.charAt(i) == nameSecond.charAt(i) &&
                        first.getName().charAt(i) > second.getName().charAt(i)) {
                    return true;
                }
                else if(nameFirst.charAt(i) < nameSecond.charAt(i)) {
                    return false;
                }
            }
            //if names are the same till end of one of them, the one with the shorter name wins
            return nameFirst.length() < nameSecond.length();
            
        }
        return false;
    }
    
    @Override public String toString() {
        return getName();
    }
}