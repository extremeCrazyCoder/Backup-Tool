import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
public class ProgressGUI extends JFrame {
    private JPanel contentPane;
    private Thread animations;
    private JLabel lblWorking;
    private JLabel lblTask;
    private JLabel lblFile;
    private JLabel lblEverything;
    private JLabel lblRead;
    private JLabel lblHashing;
    private JLabel lblFileHeader;
    private JButton btnFinished;
    private boolean notStopping;
    
    private JProgressBar progressEverything;
    private JProgressBar progressRead;
    private JProgressBar progressHashing;
    
    /**
     * Create the frame.
     */
    public ProgressGUI(Rectangle bounds) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(bounds.x, bounds.y, 450, 300);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel labelHeader = new JLabel("Backup Tool");
        labelHeader.setHorizontalAlignment(SwingConstants.CENTER);
        labelHeader.setFont(new Font("Dialog", Font.BOLD, 25));
        labelHeader.setBounds(0, 0, 434, 51);
        contentPane.add(labelHeader);
        
        lblWorking = new JLabel("working ...");
        lblWorking.setHorizontalAlignment(SwingConstants.CENTER);
        lblWorking.setFont(new Font("Dialog", Font.BOLD, 15));
        lblWorking.setBounds(10, 228, 412, 24);
        contentPane.add(lblWorking);
        
        progressEverything = new JProgressBar();
        progressEverything.setBounds(102, 131, 320, 24);
        contentPane.add(progressEverything);
        
        progressRead = new JProgressBar();
        progressRead.setBounds(102, 160, 320, 24);
        contentPane.add(progressRead);
        
        progressHashing = new JProgressBar();
        progressHashing.setBounds(102, 192, 320, 24);
        contentPane.add(progressHashing);
        
        lblEverything = new JLabel("everything");
        lblEverything.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblEverything.setBounds(12, 130, 90, 24);
        contentPane.add(lblEverything);
        
        lblRead = new JLabel("reading");
        lblRead.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblRead.setBounds(12, 160, 90, 24);
        contentPane.add(lblRead);
        
        lblHashing = new JLabel("hashing");
        lblHashing.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblHashing.setBounds(12, 190, 90, 24);
        contentPane.add(lblHashing);
        
        JLabel lblTaskHeader = new JLabel("Task:");
        lblTaskHeader.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblTaskHeader.setBounds(10, 61, 90, 24);
        contentPane.add(lblTaskHeader);
        
        lblTask = new JLabel("");
        lblTask.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblTask.setBounds(100, 61, 180, 24);
        contentPane.add(lblTask);
        
        btnFinished = new JButton("Finished");
        btnFinished.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {closeGUI();}});
        btnFinished.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnFinished.setVisible(false);
        btnFinished.setBounds(152, 228, 130, 23);
        contentPane.add(btnFinished);
        
        lblFileHeader = new JLabel("File:");
        lblFileHeader.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblFileHeader.setBounds(12, 96, 90, 24);
        contentPane.add(lblFileHeader);
        
        lblFile = new JLabel("");
        lblFile.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblFile.setBounds(102, 96, 180, 24);
        contentPane.add(lblFile);
        
        notStopping = true;
        animations = new Thread(new Runnable() {@Override 
                public void run() {animate();}});
        animations.start();
    }
    
    protected void closeGUI() {
        Main.openMenueGUI(this.getBounds());
        this.setVisible(false);
    }
    
    protected void animate() {
        String tempString = "";
        try {
            while(notStopping) {
                lblWorking.setText("Working " + tempString);
                Thread.sleep(300);
                tempString+= ".";
                if(tempString.length() == 4)
                    tempString = "";
            }
        } catch (InterruptedException e) {
            ErrorHandler.showErr(e);
        }
    }
    
    public void setFile() {
        if(progressEverything.isVisible())
            progressEverything.setValue((int) (Progresser.getDoneFileSize() / 1024));
        if(progressRead.isVisible()) {
            progressRead.setMaximum(Progresser.getNowFilesize());
            progressRead.setMinimum(0);
            progressRead.setValue(0);
        }
        if(progressHashing.isVisible()) {
            progressHashing.setMaximum(Progresser.getNowFilesize());
            progressHashing.setMinimum(0);
            progressHashing.setValue(0);
        }
        if(lblFile.isVisible())
            lblFile.setText(Progresser.getFilesProgress());
    }
    
    public void writeProgress() {
        if(progressEverything.isVisible())
            progressEverything.setValue((int) (Progresser.getDoneFileSize() / 1024));
        if(progressRead.isVisible())
            progressRead.setValue(Progresser.getReadDone());
        if(progressHashing.isVisible())
            progressHashing.setValue(Progresser.getHashingDone());
    }
    
    public void setFiles() {
        //Progress of everything (size in Kib)
        if(progressEverything.isVisible()) {
            progressEverything.setMaximum((int) (Progresser.getFullFileSize() / 1024));
            progressEverything.setMinimum(0);
            progressEverything.setValue(0);
        }
    }
    
    public void setStep() {
        switch(Progresser.getCurrentStep()) {
        case Progresser.INIT:
            lblTask.setText("Initialising");
            progressEverything.setVisible(false);
            progressRead.setVisible(false);
            progressHashing.setVisible(false);
            lblFile.setVisible(false);
            lblEverything.setVisible(false);
            lblRead.setVisible(false);
            lblHashing.setVisible(false);
            lblFileHeader.setVisible(false);
            break;
        case Progresser.CALCULATEHASH:
            lblTask.setText("Calculating hashes");
            progressEverything.setVisible(true);
            progressRead.setVisible(true);
            progressHashing.setVisible(true);
            lblFile.setVisible(true);
            lblEverything.setVisible(true);
            lblRead.setVisible(true);
            lblRead.setText("reading");;
            lblHashing.setVisible(true);
            lblFileHeader.setVisible(true);
            break;
        case Progresser.CALCULATETODO:
            lblTask.setText("Calculating what todo");
            progressEverything.setVisible(false);
            progressRead.setVisible(false);
            progressHashing.setVisible(false);
            lblFile.setVisible(false);
            lblEverything.setVisible(false);
            lblRead.setVisible(false);
            lblHashing.setVisible(false);
            lblFileHeader.setVisible(false);
            break;
        case Progresser.COPY:
            lblTask.setText("Copying");
            progressEverything.setVisible(true);
            progressRead.setVisible(true);
            progressHashing.setVisible(false);
            lblFile.setVisible(true);
            lblEverything.setVisible(true);
            lblRead.setVisible(true);
            lblRead.setText("copying");;
            lblHashing.setVisible(false);
            lblFileHeader.setVisible(true);
            break;
        case Progresser.FINISHED:
            lblTask.setText("Finished");
            progressEverything.setVisible(false);
            progressRead.setVisible(false);
            progressHashing.setVisible(false);
            lblFile.setVisible(false);
            lblEverything.setVisible(false);
            lblRead.setVisible(false);
            lblHashing.setVisible(false);
            lblFileHeader.setVisible(false);
            lblWorking.setVisible(false);
            notStopping = false;
            btnFinished.setVisible(true);
            break;
        }
    }
}
