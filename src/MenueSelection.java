import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
public class MenueSelection extends JFrame {

    private JPanel contentPane;
    
    /**
     * Create the frame.
     */
    public MenueSelection(Rectangle bounds) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Backup Tool");
        setBounds(bounds.x, bounds.y, 450, 300);
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
        
        JButton btnBackup = new JButton("Backup Data");
        btnBackup.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnBackup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.openBackupGUI(getBounds());
                setVisible(false);
            }
        });
        btnBackup.setBounds(82, 84, 270, 58);
        contentPane.add(btnBackup);
        
        JButton btnrestore = new JButton("restore Data");
        btnrestore.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnrestore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Main.openRestoreGUI(getBounds());
                setVisible(false);
            }
        });
        btnrestore.setBounds(82, 179, 270, 58);
        contentPane.add(btnrestore);
        
        setVisible(true);
    }
}
