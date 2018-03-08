import java.awt.Rectangle;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class Main {
    //FIXME kein Fortschritt bei Calculation what todo
    //FIXME sortierung ordner & datein getrennt behandeln
    public static void main(String[] args) {
        ArgumentsReader arguments;
        try {
            arguments = new ArgumentsReader(args);
        } catch (IOException e) {
            ErrorHandler.showErr(e);
            return;
        }
        
        if(argsOK(arguments)) {
            Progresser.setProgressReportType(Progresser.TEXTONLY);
            //automatic Backup mode
            String destinationPath = arguments.getIndex("d").getValue();
            String toBackupPath = arguments.getIndex("s").getValue();
            Argument tempMode = arguments.getIndex("m");
            
            int mode;
            if(tempMode == null)
                mode = Backup.INTELLIGENT;
            else if(tempMode.getValue().toLowerCase().equals("intelligent"))
                mode = Backup.INTELLIGENT;
            else if(tempMode.getValue().toLowerCase().equals("full"))
                mode = Backup.FULL;
            else
                //Standard: inteligent backup
                mode = Backup.INTELLIGENT;
            
            try {
                Progresser.init(null);
                new Backup().doBackup(destinationPath, toBackupPath, mode);
            } catch (NoSuchAlgorithmException | IOException e) {
                ErrorHandler.showErr(e);
            }
        }
        else if(helpArgs(arguments)) {
            showHelpText();
        }
        else {
            Progresser.setProgressReportType(Progresser.GUI);
            new MenueSelection(new Rectangle(100, 100, 100, 100));
        }
    }
    
    private static boolean argsOK(ArgumentsReader arguments) {
        if(!arguments.indexExists("s")) return false;
        if(!arguments.indexExists("d")) return false;
        return true;
    }

    private static void showHelpText() {
        System.out.println("Needed Arguments:\n" +
                "\t-d\tThe destination: path where the Backup should be stored\n" +
                "\t-s\tThe source: the directory from that a backup should be created\n" +
                "\n" +
                "Aditional Arguments:\n" +
                "\t-m\tThe Backup mode (normal intelligent)\n" +
                "\tModes:\n" +
                "\t\tinteligent: copies only changed data" +
                "\t\tfull: creates a full copy of all data, even if not changed");
    }

    private static boolean helpArgs(ArgumentsReader args) {
        if(args.indexExists("h")) return true;
        if(args.indexExists("help")) return true;
        if(args.indexExists("-h")) return true;
        if(args.indexExists("-help")) return true;
        
        return false;
    }

    public static void openBackupGUI(Rectangle orig) {
        new BackupGUI(orig);
    }

    public static void openRestoreGUI(Rectangle orig) {
        new RestoreGUI(orig);
    }

    public static void openMenueGUI(Rectangle orig) {
        new MenueSelection(orig);
    }
}
