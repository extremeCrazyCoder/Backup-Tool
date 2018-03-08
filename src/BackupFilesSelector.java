import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.DefaultComboBoxModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class BackupFilesSelector extends JFrame {
    private JPanel contentPane;
    private JComboBox<PartialBackup> comboSelectPart;
    private JTree treeSelect;
    
    private Callable toCall;
    
    private Object lastSelected = null;
    
    private List<CheckNode> allNodes = new ArrayList<CheckNode>();
    private JLabel lblRestore;
    private JScrollPane treeSelectPane;
    private JButton btnFinish;
    
    /**
     * Create the frame.
     * @param toCall 
     * @param cordinates 
     * @param source 
     */
    public BackupFilesSelector(Rectangle cordinates, Callable toCall, BackupFolder source) {
        this.toCall = toCall;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Restore Data - Backup Tool");
        setBounds(cordinates.x, cordinates.y, 450, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        addComponentListener(new ComponentListener() {
            @Override public void componentHidden(ComponentEvent arg0) {}
            @Override public void componentMoved(ComponentEvent arg0) {}
            @Override public void componentResized(ComponentEvent arg0) {
                resized();
            }
            @Override public void componentShown(ComponentEvent arg0) {}
        });
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        lblRestore = new JLabel("Restore");
        lblRestore.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblRestore.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblRestore);
        
        comboSelectPart = new JComboBox<PartialBackup>();
        comboSelectPart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(lastSelected != comboSelectPart.getSelectedItem())
                    selectedChanged();
            }
        });
        comboSelectPart.setModel(new DefaultComboBoxModel<PartialBackup>(source.getBackups()));
        
        contentPane.add(comboSelectPart);
        
        treeSelect = new JTree();
        treeSelect.setVisible(false);
        treeSelect.setModel(new DefaultTreeModel(new CheckNode()));
        treeSelect.setCellRenderer(new CheckRenderer());
        treeSelect.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION
                );
        treeSelect.putClientProperty("JTree.lineStyle", "Angled");
        treeSelect.addMouseListener(new NodeSelectionListener(treeSelect));
        
        treeSelectPane = new JScrollPane(treeSelect);
        treeSelectPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeSelectPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPane.add(treeSelectPane);
        
        btnFinish = new JButton("Fertig");
        btnFinish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                finished();
            }
        });
        btnFinish.setFont(new Font("Dialog", Font.BOLD, 20));
        contentPane.add(btnFinish);
        
        selectedChanged();
        resized();
        
        setVisible(true);
    }

    protected void resized() {
        int width = this.getBounds().width;
        int height = this.getBounds().height;
        lblRestore.setBounds(0, 0, width, 50);
        comboSelectPart.setBounds(10, 55, width - 25, 24);
        treeSelectPane.setBounds(10, 90, width - 25, height - 175);
        btnFinish.setBounds((width - 266)/2, height - 75, 266, 35);
    }

    protected void finished() {
        new Thread(new Runnable () {
            @Override public void run() {
                toCall.call();
            }}).start();
        this.setVisible(false);
    }

    protected void selectedChanged() {
        if(comboSelectPart.getSelectedIndex() < 0) {
            //if nothing is selected
            treeSelect.setVisible(false);
            lastSelected = null;
            return;
        }
        PartialBackup selected = (PartialBackup) comboSelectPart.getSelectedItem();
        treeSelect.setVisible(true);
        treeSelect.setModel(new DefaultTreeModel(getTreeModel(selected.getFolder())));
        
        lastSelected = comboSelectPart.getSelectedItem();
    }

    private CheckNode getTreeModel(elementabel element) {
        CheckNode node =  new CheckNode(element);
        if(element instanceof MyFolder) {
            MyFolder folder = (MyFolder) element;
            for(int i = 0; i < folder.getSubelements().size(); i++)
                node.add(getTreeModel(folder.getSubelements().get(i)));
        }
        else {
            allNodes.add(node);
        }
        return node;
    }

    public List<MyFile> getSelectedItems() {
        List<MyFile> selected = new ArrayList<MyFile>();
        
        for(int i = 0; i < allNodes.size(); i++)
            if(allNodes.get(i).isSelected())
                if(allNodes.get(i).getUserObject() instanceof MyFile)
                    selected.add((MyFile) allNodes.get(i).getUserObject());
        
        return selected;
    }
}