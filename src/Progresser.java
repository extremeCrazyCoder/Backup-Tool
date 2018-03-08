import java.awt.Rectangle;
import java.util.List;


public class Progresser {
    //States
    public static final int INIT = 1;
    public static final int CALCULATEHASH = 2;
    public static final int CALCULATETODO = 3;
    public static final int COPY = 4;
    public static final int FINISHED = 5;
    
    //Hashes that must be calculated
    public static final int FILEREADMD5 = 11;
    public static final int FILEREADSHA256 = 12;
    public static final int FILEREADSHA512 = 13;
    public static final int FILEREAD = 14;
    
    //Copying of file
    public static final int FILECOPY = 21;
    
    //Where should the output go to ?
    public static final int TEXTONLY = 1;
    public static final int GUI = 2;
    public static final int BOTH = 3;
    
    private static List<MyFile> filesTodo;
    private static int nowFileNum = 0;
    private static int state = 0;
    private static long fileReadWrite = 0;
    private static long hashMD5 = 0;
    private static long hashSHA256 = 0;
    private static long hashSHA512 = 0;
    private static int progressReportType = 0;
    private static ProgressGUI gui;
    private static long completeTodoSize;
    
    public static void setFile(MyFile file) {
        fileReadWrite = 0;
        hashMD5 = 0;
        hashSHA256 = 0;
        hashSHA512 = 0;
        for(int i = 0; i < filesTodo.size(); i++) {
            if(filesTodo.get(i).equals(file)) {
                nowFileNum = i;
                break;
            }
        }
        
        if(progressReportType == GUI  || progressReportType == BOTH)
            gui.setFile();
    }
    
    public static void init(Rectangle bounds) {
        if(progressReportType == GUI  || progressReportType == BOTH) {
            gui = new ProgressGUI(bounds);
            gui.setVisible(true);
        }
    }
    
    public static void setFiles(List<MyFile> allFiles) {
        nowFileNum = -1;
        filesTodo = allFiles;
        fileReadWrite = 0;
        hashMD5 = 0;
        hashSHA256 = 0;
        hashSHA512 = 0;
        
        completeTodoSize = 0;
        for(int i = 0; i < filesTodo.size(); i++)
            completeTodoSize+= filesTodo.get(i).getSize();
        
        if(progressReportType == GUI  || progressReportType == BOTH)
            gui.setFiles();
    }
    
    public static void setProgress(int mode, int length) {
        if(mode == Progresser.FILEREAD) {
            fileReadWrite += length;
        }
        else if(mode == Progresser.FILEREADMD5) {
            hashMD5 += length;
        }
        else if(mode == Progresser.FILEREADSHA256) {
            hashSHA256 += length;
        }
        else if(mode == Progresser.FILEREADSHA512) {
            hashSHA512 += length;
        }
        else if(mode == Progresser.FILECOPY) {
            fileReadWrite += length;
        }
        writeProgress();
    }
    
    private static void writeProgress() {
        if(progressReportType == TEXTONLY  || progressReportType == BOTH)
            writeProgressToConsol();
        if(progressReportType == GUI  || progressReportType == BOTH)
            gui.writeProgress();
    }
    
    private static void writeProgressToConsol() {
        if(state == Progresser.INIT)
            System.out.println("Init !!!");
        else if(state == Progresser.CALCULATEHASH)
            System.out.println("Hash:\tFile: " + getFilesProgress() +
                    "\t" + fileReadWrite + "\t" + hashMD5 + "\t" + hashSHA256 + "\t" +
                    hashSHA512 + "\t" + filesTodo.get(nowFileNum).getSize());
        else if(state == Progresser.CALCULATETODO)
            System.out.println("Calculation what todo !!!");
        else if(state == Progresser.COPY)
            System.out.println("Copy:\tFile: " + getFilesProgress()
                    + "\t" + fileReadWrite + "/" + filesTodo.get(nowFileNum).getSize());
        else if(state == Progresser.FINISHED)
            System.out.println("Finished !!");
    }

    public static void setStep(int step) {
        state = step;
        if(progressReportType == GUI  || progressReportType == BOTH)
            gui.setStep();
        if(step == Progresser.INIT || step == Progresser.CALCULATETODO
                || step == Progresser.FINISHED)
            writeProgress();
    }

    public static void setProgressReportType(int type) {
        progressReportType = type;
    }

    public static long getFullFileSize() {
        return completeTodoSize;
    }

    public static long getDoneFileSize() {
        long done = 0;
        for(int i = 0; i < nowFileNum; i++)
            done+= filesTodo.get(i).getSize();
        done+= fileReadWrite;
        
        return done;
    }

    public static int getReadDone() {
        return (int) fileReadWrite;
    }

    public static int getHashingDone() {
        return (int) ((hashMD5 + hashSHA256 + hashSHA512) / 3);
    }

    public static int getNowFilesize() {
        return (int) (filesTodo.get(nowFileNum).getSize());
    }

    public static int getCurrentStep() {
        return state;
    }

    public static String getFilesProgress() {
        return (nowFileNum + 1) + "/" + filesTodo.size();
    }
}
