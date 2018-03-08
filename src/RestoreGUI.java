import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class RestoreGUI extends JFrame implements Callable {
    private JPanel contentPane;
    private JTextField txtSrc;
    
    private JFileChooser chooser = new JFileChooser();
    
    private BackupFilesSelector selector;
    private JTextField txtDest;
    
    /**
     * Create the frame.
     * @param rectangle 
     */
    public RestoreGUI(Rectangle bounds) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Restore Data - Backup Tool");
        setBounds(bounds.x, bounds.y, 477, 348);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel label = new JLabel("Backup Tool");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, 25));
        label.setBounds(0, 0, 434, 51);
        contentPane.add(label);
        
        JButton btnZurueck = new JButton("zur\u00FCck");
        btnZurueck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.openMenueGUI(getBounds());
                setVisible(false);
            }
        });
        btnZurueck.setFont(new Font("Dialog", Font.PLAIN, 20));
        btnZurueck.setBounds(318, 246, 120, 42);
        contentPane.add(btnZurueck);
        
        JLabel lblSrc = new JLabel("Enter the path of the Backup");
        lblSrc.setFont(new Font("Dialog", Font.PLAIN, 15));
        lblSrc.setBounds(24, 63, 264, 28);
        contentPane.add(lblSrc);
        
        txtSrc = new JTextField();
        txtSrc.setColumns(10);
        txtSrc.setBounds(24, 94, 414, 28);
        contentPane.add(txtSrc);
        
        JButton btnSrcSelect = new JButton("Search ...");
        btnSrcSelect.setBounds(318, 63, 120, 28);
        btnSrcSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSourceSelector();
            }
        });
        contentPane.add(btnSrcSelect);
        
        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                loadBackup();
            }
        });
        btnLoad.setFont(new Font("Dialog", Font.BOLD, 30));
        btnLoad.setBounds(57, 238, 227, 50);
        contentPane.add(btnLoad);
        
        JLabel lblDest = new JLabel("Enter the path for the recovered files");
        lblDest.setFont(new Font("Dialog", Font.PLAIN, 15));
        lblDest.setBounds(20, 150, 294, 28);
        contentPane.add(lblDest);
        
        JButton btnDestSelect = new JButton("Search ...");
        btnDestSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                openDestinationSelector();
            }
        });
        btnDestSelect.setBounds(314, 150, 120, 28);
        contentPane.add(btnDestSelect);
        
        txtDest = new JTextField();
        txtDest.setColumns(10);
        txtDest.setBounds(20, 181, 414, 28);
        contentPane.add(txtDest);
        
        setVisible(true);
    }

    protected void loadBackup() {
        BackupFolder folder = new BackupFolder(txtSrc.getText());
        try {
            folder.init();
        } catch (IOException e) {
            ErrorHandler.showErr(e);
            Main.openMenueGUI(getBounds());
            return;
        }
        
        selector = new BackupFilesSelector(getBounds(), this, folder);
        setVisible(false);
    }

    protected void openSourceSelector() {
        chooser.setDialogTitle("Select the directory of the Backup");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = chooser.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            txtSrc.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    protected void openDestinationSelector() {
        chooser.setDialogTitle("Select the directory for the recovered files");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = chooser.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            txtDest.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    @Override
    public void call() {
        Progresser.init(selector.getBounds());
        Progresser.setStep(Progresser.CALCULATETODO);
        
        List<MyFile> files = selector.getSelectedItems();
        List<CopyTask> toDo = new ArrayList<CopyTask>();
        
        for(int i = 0; i < files.size(); i++) {
            String directoryTo = txtDest.getText() + files.get(i).getRelativePath();
            toDo.add(new CopyTask(files.get(i), directoryTo + "/" + files.get(i).getCorrectName(),
                    directoryTo, null));
        }
        
        Progresser.setStep(Progresser.COPY);
        Progresser.setFiles(files);
        for(int i = 0; i < toDo.size(); i++) {
            Progresser.setFile(files.get(i));
            toDo.get(i).doTask();
        }
        Progresser.setStep(Progresser.FINISHED);
    }
}