import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.security.NoSuchAlgorithmException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;


@SuppressWarnings("serial")
public class BackupGUI extends JFrame {

    private JPanel contentPane;
    private JTextField txtDestination;
    private JTextField txtSource;
    
    private JRadioButton rdbtnFull;
    private JRadioButton rdbtnIntelligent;
    
    private JFileChooser chooser = new JFileChooser();
    
    /**
     * Create the frame.
     * @param rectangle 
     */
    public BackupGUI(Rectangle bounds) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Backup Data - Backup Tool");
        setBounds(bounds.x, bounds.y, 450, 372);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblHeader = new JLabel("Backup Tool");
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        lblHeader.setFont(new Font("Dialog", Font.BOLD, 25));
        lblHeader.setBounds(0, 0, 434, 51);
        contentPane.add(lblHeader);
        
        JLabel lblDestination = new JLabel("Enter the destination Path");
        lblDestination.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDestination.setBounds(10, 149, 264, 28);
        contentPane.add(lblDestination);
        
        JLabel lblSource = new JLabel("Enter the source path");
        lblSource.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblSource.setBounds(10, 62, 264, 28);
        contentPane.add(lblSource);
        
        txtDestination = new JTextField();
        txtDestination.setBounds(10, 180, 414, 28);
        contentPane.add(txtDestination);
        txtDestination.setColumns(10);
        
        txtSource = new JTextField();
        txtSource.setColumns(10);
        txtSource.setBounds(10, 93, 414, 28);
        contentPane.add(txtSource);
        
        JButton btnSrcSelect = new JButton("Search ...");
        btnSrcSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSourceSelector();
            }
        });
        btnSrcSelect.setBounds(304, 62, 120, 28);
        contentPane.add(btnSrcSelect);
        
        JButton buttonDestSelect = new JButton("Search ...");
        buttonDestSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDestinationSelector();
            }
        });
        buttonDestSelect.setBounds(304, 149, 120, 28);
        contentPane.add(buttonDestSelect);
        
        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startBackup();
            }
        });
        btnStart.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnStart.setBounds(123, 238, 162, 51);
        contentPane.add(btnStart);
        
        rdbtnIntelligent = new JRadioButton("Intelligent");
        rdbtnIntelligent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(rdbtnFull.isSelected() && rdbtnIntelligent.isSelected()) {
                    rdbtnFull.setSelected(false);
                }
                else if(!rdbtnFull.isSelected() && !rdbtnIntelligent.isSelected()) {
                    rdbtnFull.setSelected(true);
                }
            }
        });
        rdbtnIntelligent.setBounds(6, 276, 109, 15);
        rdbtnIntelligent.setSelected(true);
        contentPane.add(rdbtnIntelligent);
        
        rdbtnFull = new JRadioButton("Full");
        rdbtnFull.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(rdbtnFull.isSelected() && rdbtnIntelligent.isSelected()) {
                    rdbtnIntelligent.setSelected(false);
                }
                else if(!rdbtnFull.isSelected() && !rdbtnIntelligent.isSelected()) {
                    rdbtnIntelligent.setSelected(true);
                }
            }
        });
        rdbtnFull.setBounds(6, 258, 109, 15);
        contentPane.add(rdbtnFull);
        
        JLabel lblMode = new JLabel("Mode");
        lblMode.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblMode.setBounds(10, 229, 105, 22);
        contentPane.add(lblMode);
        
        JButton btnZurueck = new JButton("zur\u00FCck");
        btnZurueck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.openMenueGUI(getBounds());
                setVisible(false);
            }
        });
        btnZurueck.setFont(new Font("Dialog", Font.PLAIN, 20));
        btnZurueck.setBounds(318, 286, 120, 42);
        contentPane.add(btnZurueck);
        
        setVisible(true);
    }

    protected void startBackup() {
        this.setVisible(false);
        String source = txtSource.getText();
        String destination = txtDestination.getText();
        
        int mode;
        if(rdbtnFull.isSelected())
            mode = Backup.FULL;
        else if(rdbtnIntelligent.isSelected())
            mode = Backup.INTELLIGENT;
        else
            mode = Backup.INTELLIGENT;
        
        try {
            Progresser.init(this.getBounds());
            new Backup().doBackup(destination, source, mode);
        } catch (NoSuchAlgorithmException | IOException e) {
            ErrorHandler.showErr(e);
        }
    }

    protected void openDestinationSelector() {
        chooser.setDialogTitle("Select the destination directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = chooser.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            txtDestination.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    protected void openSourceSelector() {
        chooser.setDialogTitle("Select the source directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = chooser.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            txtSource.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}