import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MyFile implements elementabel {
    public static final int FULL = 1;
    public static final int LINK = 2;
    private static final int MAXBUFFERSIZE = 10;
    
    //Size of the buffer for the hash
    private final int BUFFERSIZE = 1048576;
    private String path = null;
    private long  fileSize = -1;
    private File file;
    private String name;
    
    private List<ByteBuffer> bigMD5Buffer = new ArrayList<ByteBuffer>();
    private List<ByteBuffer> bigSHA256Buffer = new ArrayList<ByteBuffer>();
    private List<ByteBuffer> bigSHA512Buffer = new ArrayList<ByteBuffer>();
    
    private MessageDigest md5;
    private MessageDigest sha256;
    private MessageDigest sha512;
    
    private boolean readFinished;
    private boolean hashReady;
    private boolean MD5Ready;
    private boolean SHA256Ready;
    private boolean SHA512Ready;
    private byte[] hashMD5 = null;
    private byte[] hashSHA256 = null;
    private byte[] hashSHA512 = null;
    
    private String relativePath;
    private Callable toCall;
    private int type;
    private BackupFolder main;
    private int charChangerVersion;
    
    public MyFile(String absolutePath, String relativePath, String name, int version) throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
        sha256 = MessageDigest.getInstance("SHA-256");
        sha512 = MessageDigest.getInstance("SHA-512");
        this.path = absolutePath + "/" + name;
        this.relativePath = relativePath;
        this.name = name;
        this.type = MyFile.FULL;
        this.charChangerVersion = version;
    }

    public MyFile(String[] splited, String absoluteDirectoryPath, BackupFolder main, int version) {
        //type(l...link, f...full) \t filename \t relativePath \t backupFolderName \t md5 \t sha-256 \t sha-512 \t Filesize
        if(splited[0].equals("l"))
            this.type = MyFile.LINK;
        else if(splited[0].equals("f"))
            this.type = MyFile.FULL;
            
        this.name = splited[1];
        this.relativePath = splited[2];
        this.hashMD5 = hexToByte(splited[4]);
        this.hashSHA256 = hexToByte(splited[5]);
        this.hashSHA512 = hexToByte(splited[6]);
        this.hashReady = true;
        this.fileSize = Long.parseLong(splited[7]);
        
        if(this.type == MyFile.FULL)
            this.path = absoluteDirectoryPath + "/" + splited[3] + "/data" + splited[2] + "/" + name;
        
        this.main = main;
        this.charChangerVersion = version;
    }

    @Override
    public void init() {
        if(path != null) {
            file = new File(path);
            fileSize = file.length();
        }
    }

    @Override
    public void calculateHash(Callable toCallIfFinished) throws NoSuchAlgorithmException, IOException {
        this.toCall = toCallIfFinished;
        FileInputStream in = new FileInputStream(file);
        
        byte[] buffer = new byte[BUFFERSIZE];
        if(BUFFERSIZE < fileSize) {
            new Thread(new Runnable() {
                @Override public void run() {updateMD5();}}).start();
            new Thread(new Runnable() {
                @Override public void run() {updateSHA256();}}).start();
            new Thread(new Runnable() {
                @Override public void run() {updateSHA512();}}).start();
        }
        
        Progresser.setFile(this);
        
        long read = 0;
        int unitSize;
        while(read < fileSize) {
            unitSize = (int) (((fileSize - read) >= BUFFERSIZE) ?
                    BUFFERSIZE : (fileSize - read));
            
            in.read(buffer, 0, unitSize);
            if(BUFFERSIZE < fileSize) {
                bigMD5Buffer.add(new ByteBuffer(buffer, 0, unitSize));
                bigSHA256Buffer.add(new ByteBuffer(buffer, 0, unitSize));
                bigSHA512Buffer.add(new ByteBuffer(buffer, 0, unitSize));
            }
            else {
                md5.update(buffer, 0, unitSize);
                sha256.update(buffer, 0, unitSize);
                sha512.update(buffer, 0, unitSize);
            }
            
            Progresser.setProgress(Progresser.FILEREAD, unitSize);
            read += unitSize;
            
            while(bigMD5Buffer.size() > MAXBUFFERSIZE ||
                    bigSHA256Buffer.size() > MAXBUFFERSIZE ||
                    bigSHA512Buffer.size() > MAXBUFFERSIZE) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    ErrorHandler.showErr(e);
                }
            }
        }
        
        if(BUFFERSIZE < fileSize) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                ErrorHandler.showErr(e);
            }
        }
        else {
            hashMD5 = md5.digest();
            hashSHA256 = sha256.digest();
            hashSHA512 = sha512.digest();
            MD5Ready = true;
            SHA256Ready = true;
            SHA512Ready = true;
            hashReady = true;
        }
        readFinished = true;
        
        in.close();
        mayHasFinished();
    }
    
    protected void updateMD5() {
        while(!readFinished || bigMD5Buffer.size() != 0) {
            if(bigMD5Buffer.size() > 1 ||
                    (bigMD5Buffer.size() > 0 && readFinished)) {
                md5.update(bigMD5Buffer.get(0).buffer,
                        bigMD5Buffer.get(0).start,
                        bigMD5Buffer.get(0).unitSize);
                Progresser.setProgress(Progresser.FILEREADMD5,
                        bigMD5Buffer.get(0).unitSize);
                bigMD5Buffer.remove(0);
            }
            try {
                if(bigMD5Buffer.size() <= 1)
                    Thread.sleep(10);
            } catch (InterruptedException e) {
                ErrorHandler.showErr(e);
            }
        }
        hashMD5 = md5.digest();
        MD5Ready = true;
        mayHasFinished();
    }
    
    protected void updateSHA256() {
        while(!readFinished || bigSHA256Buffer.size() != 0) {
            if(bigSHA256Buffer.size() > 1 ||
                    (bigSHA256Buffer.size() > 0 && readFinished)) {
                sha256.update(bigSHA256Buffer.get(0).buffer,
                        bigSHA256Buffer.get(0).start,
                        bigSHA256Buffer.get(0).unitSize);
                Progresser.setProgress(Progresser.FILEREADSHA256,
                        bigSHA256Buffer.get(0).unitSize);
                bigSHA256Buffer.remove(0);
            }
            try {
                if(bigSHA256Buffer.size() <= 1)
                    Thread.sleep(10);
            } catch (InterruptedException e) {
                ErrorHandler.showErr(e);
            }
        }
        hashSHA256 = sha256.digest();
        SHA256Ready = true;
        mayHasFinished();
    }
    
    protected void updateSHA512() {
        while(!readFinished || bigSHA512Buffer.size() != 0) {
            if(bigSHA512Buffer.size() > 1 ||
                    (bigSHA512Buffer.size() > 0 && readFinished)) {
                sha512.update(bigSHA512Buffer.get(0).buffer,
                        bigSHA512Buffer.get(0).start,
                        bigSHA512Buffer.get(0).unitSize);
                Progresser.setProgress(Progresser.FILEREADSHA512,
                        bigSHA512Buffer.get(0).unitSize);
                bigSHA512Buffer.remove(0);
            }
            try {
                if(bigSHA512Buffer.size() <= 1)
                    Thread.sleep(10);
            } catch (InterruptedException e) {
                ErrorHandler.showErr(e);
            }
        }
        hashSHA512 = sha512.digest();
        SHA512Ready = true;
        mayHasFinished();
    }
    
    private void mayHasFinished() {
        if(MD5Ready && SHA256Ready && SHA512Ready) {
            hashReady = true;
            new Thread(new Runnable() {@Override public void run()
                {nextStep();}}).start();
        }
    }
    
    protected void nextStep() {
        toCall.call();
    }

    public String getRelativePath() {
        return relativePath;
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if(!(otherObj instanceof MyFile)) return false;
        
        MyFile other = (MyFile)otherObj;
        
        if(fileSize != other.fileSize) return false;
        if(this.isHashReady() && !other.isHashReady()) return false;
        if(!this.isHashReady() && other.isHashReady()) return false;
        if(!this.isHashReady() && !other.isHashReady()) {
            return this.getName().equals(other.getName());
        }
        
        for(int i = 0; i < hashMD5.length; i++)
            if(hashMD5[i] != other.hashMD5[i])
                return false;
        
        for(int i = 0; i < hashSHA256.length; i++)
            if(hashSHA256[i] != other.hashSHA256[i])
                return false;
        
        for(int i = 0; i < hashSHA512.length; i++)
            if(hashSHA512[i] != other.hashSHA512[i])
                return false;
        
        return true;
    }

    private boolean isHashReady() {
        return hashReady;
    }

    public String getName() {
        return this.name;
    }

    public String getStringHashMD5() {
        return byteToHex(this.hashMD5);
    }
    
    public String getStringHashSHA256() {
        return byteToHex(this.hashSHA256);
    }
    
    public String getStringHashSHA512() {
        return byteToHex(this.hashSHA512);
    }

    private String byteToHex(byte[] bytes) {
        char[] hexChars = "0123456789ABCDEF".toCharArray();
        StringBuilder hex = new StringBuilder();
        
        for(int i = 0; i < bytes.length; i++) {
            int temp = bytes[i] & 0xFF;
            hex.append(hexChars[temp >> 4]);
            hex.append(hexChars[temp & 0x0F]);
        }
        
        return hex.toString();
    }

    private byte[] hexToByte(String hex) {
        String hexChars = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        byte[] bytes = new byte[hex.length() / 2];
        
        for(int i = 0; i < bytes.length; i++) {
            int first = hexChars.indexOf(hex.charAt(i * 2));
            int second = hexChars.indexOf(hex.charAt((i * 2) + 1));
            
            first = first << 4;
            bytes[i] = (byte) (first + second);
        }
        
        return bytes;
    }

    public long getSize() {
        return fileSize;
    }

    public String getAbsolutePath() {
        if(path == null)
            this.path = main.getCorrectFilePath(this);
        return path;
    }

    public int getType() {
        return type;
    }
    
    @Override public String toString() {
        return getCorrectName();
    }

    public String getCorrectName() {
        return CharChanger.decode(name, charChangerVersion);
    }

    public void setType(int type) {
        this.type = type;
    }
}

class ByteBuffer {
    public int start, unitSize;
    public byte[] buffer;
    
    ByteBuffer(byte[] buffer, int start, int unitSize) {
        this.buffer = new byte[buffer.length];
        for(int i = 0; i < buffer.length; i++)
            this.buffer[i] = buffer[i];
        
        this.start = start;
        this.unitSize = unitSize;
    }
}