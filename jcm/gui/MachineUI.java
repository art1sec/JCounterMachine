package jcm.gui;

import jcm.machine.Machine;
import jcm.machine.Parser;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;

public class MachineUI {

    private JFrame frame;
    private int pointer = 0;
    private JLabel[] pointerLabel;
    private int speed = 1000;
    private JTextField speedField;
    private JTextField[] programField;
    private JTextField[] registerField;
    private JCheckBox singleStep;
    private ToolbarButton run;
    private DisabledButton button;
    private File lastPath;

    private Thread machineThread;

    java.net.URL arrow = getClass().getResource("/jcm/res/icons8-left-arrow-48.png");
    java.net.URL trans = getClass().getResource("/jcm/res/icons8-transparent-48.png");
    ImageIcon arrowIcon = new ImageIcon(arrow);
    ImageIcon transparentIcon = new ImageIcon(trans);

    public void setPointer(int next) {
        pointerLabel[pointer].setIcon(transparentIcon);
        pointerLabel[next].setIcon(arrowIcon);
        pointer = next;
    }

    public int getPointer() {
        return pointer;
    }

    public void labelRunButton(String s) {
        run.setText(s);
    }

    public int getSpeed() {
        return Integer.parseInt(speedField.getText());
    }

    public String getProgramField(int i) {
        return programField[i].getText();
    }

    public String getRegisterField(int i) {
        return registerField[i].getText();
    }

    public void setRegisterField(int number, String value) {
        registerField[number].setText(value);
    }

    /**
     * The central gui method that fills the given pane with the grid
     * that holds nearly all elements (besides the buttons at the buttom).
     * @param pane
     */
    private void fillGrid(Container pane) {
        // create grid bag layout
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
    
        // fill grid with 20 buttons and 20 text fields
        // for the program memory prog[] and 20 empty labels
        // in which the pointer (point|arrow) will move:
        programField = new JTextField[20];
        pointerLabel = new JLabel[20];
        InsetsUIResource with = new InsetsUIResource(0, 8, 0, 0);
        InsetsUIResource without = new InsetsUIResource(0, 0, 0, 0);
        for (int i = 1; i <= 20; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.ipady = 10;
            c.insets = with;
            button = new DisabledButton(Integer.toString(i));
            pane.add(button, c);
            c.gridx = 1;
            c.gridy = i;
            c.insets = without;
            programField[i - 1] = new JTextField(5);
            pane.add(programField[i - 1], c);
            c.gridx = 2;
            c.gridy = i;
            pointerLabel[i - 1] = new JLabel();
            pointerLabel[i - 1].setPreferredSize(new DimensionUIResource(64, 0));
            pane.add(pointerLabel[i - 1], c);
        }

        // fill the right part of the grid with 6 buttons
        // and six text fields for the registers reg[]:
        registerField = new JTextField[6];
        for (int i = 1; i <= 6; i++) {
            c.gridx = 3;
            c.gridy = i;
            button = new DisabledButton(Character.toString((char) (64 + i)));
            pane.add(button, c);
            c.gridx = 4;
            c.gridy = i;
            registerField[i - 1] = new JTextField(5);
            pane.add(registerField[i - 1], c);
        }

        // set the pointer initially to memory address 1:
        setPointer(0);

        // put a text field for the speed on the grid:
        c.gridx = 3;
        c.gridy = 18;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.ipadx = 0;
        c.ipady = 0;
        c.insets = without;
        speedField = new JTextField(5);
        speedField.setBorder(BorderFactory.createTitledBorder("Speed (ms):"));
        speedField.setText(String.valueOf(speed));
        pane.add(speedField, c);

        // put a checkbox for single step on the grid:
        c.gridx = 3;
        c.gridy = 20;
        c.gridwidth = 2;
        c.gridheight = 1;
        singleStep = new JCheckBox("Single step", false);
        pane.add(singleStep, c);

        // create an editor pane at the very right of the frame
        // which holds the html description:
        java.net.URL helpURL = getClass().getResource("/jcm/res/helptext.html");
        JEditorPane ePane = new JEditorPane();
        ePane.setEditable(false);
        ePane.setPreferredSize(new DimensionUIResource(300, 300));
        try {
            ePane.setPage(helpURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // put the editor pane on the grid:
        c.gridx = 5;
        c.gridy = 1;
        c.gridheight = 20;
        c.gridwidth = 2;
        c.ipadx = 8;
        c.ipady = 8;
        c.insets = new InsetsUIResource(4, 12, 4, 12);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        pane.add(ePane, c);
    }

    /**
     * populates the buttom of the frame with buttons
     * @param pane
     */
    private void fillToolBar(Container pane) {
        // create buttons
        ToolbarButton load = new ToolbarButton("Load");
        ToolbarButton save = new ToolbarButton("Save");
        ToolbarButton clear = new ToolbarButton("Clear");
        ToolbarButton reset = new ToolbarButton("Reset");
        run = new ToolbarButton("Run");
        // add buttons to the panel
        pane.add(load);
        pane.add(save);
        pane.add(clear);
        pane.add(reset);
        pane.add(run);
        // add action listeners to the buttons
        load.addActionListener(new LoadProgram());
        save.addActionListener(new SaveProgram());
        clear.addActionListener(e -> {
            for (int i = 0; i < 20; i++) {
                programField[i].setText("");
            }
        });
        reset.addActionListener(e -> { setPointer(0); });
        run.addActionListener(new RunProgram());
    }

    private JMenuBar fillMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open");
        fileMenu.add(openMenuItem);
        openMenuItem.addActionListener(new LoadProgram());
        JMenuItem saveMenuItem = new JMenuItem("Save");
        fileMenu.add(saveMenuItem);
        saveMenuItem.addActionListener(new SaveProgram());
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(e -> {
            if (machineThread != null) { machineThread.interrupt(); }
            System.exit(0);
        });
        menuBar.add(fileMenu);

        JMenu aboutMenu = new JMenu("About");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenu.add(aboutMenuItem);
        aboutMenuItem.addActionListener(e -> { showAboutDialog(); });
        menuBar.add(aboutMenu);

        return menuBar;
    }

    private void showAboutDialog() {
        JDialog dialog = new JDialog(frame, "About", true);
        java.net.URL aboutURL = getClass().getResource("/jcm/res/about.html");
        JEditorPane aboutPane = new JEditorPane();
        aboutPane.setEditable(false);
        try {
            aboutPane.setPage(aboutURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.add(aboutPane);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> { dialog.dispose(); });
        dialog.add(closeButton, BorderLayout.SOUTH);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    /**
     * this is the gui starter method invoked to initialize the gui
     */
    public void createAndShowGUI() {
        // set look and feel to nimbus
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create frame
        frame = new JFrame("Simple Counter Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setJMenuBar(fillMenuBar());

        // create a panel which holds the central grid layout
        JPanel panel = new JPanel();
        fillGrid(panel);

        // add the panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(panel);

        // create a panel with buttons for the buttom toolbar
        JPanel toolbar = new JPanel();
        fillToolBar(toolbar);

        // add scroll pane and button toolbar to the frame
        frame.add(scrollPane);
        frame.add(toolbar, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    class RunProgram implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (run.getText().equals("Run")) {
                run.setText("Stop");
                Parser p = new Parser(MachineUI.this);
                speed = Integer.parseInt(speedField.getText());
                machineThread = new Machine(
                    MachineUI.this,
                    p.getProgram(),
                    p.getRegister(),
                    singleStep.isSelected());
                machineThread.start();    
            } else {
                machineThread.interrupt();
                run.setText("Run");
            }
        }
    }

    class SaveProgram implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            File myfile = new File("cmprogram.cmp");
            JFileChooser saveDialog = new JFileChooser();
            if (lastPath != null) { saveDialog.setCurrentDirectory(lastPath); }
            int returnValue = saveDialog.showSaveDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                myfile = saveDialog.getSelectedFile();
                lastPath = new File(myfile.getParent());
                try {
                    FileOutputStream fos = new FileOutputStream(myfile);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    Parser p = new Parser(MachineUI.this);
                    oos.writeObject(p.getProgram());
                    oos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    class LoadProgram implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser loadDialog = new JFileChooser();
            if (lastPath != null) { loadDialog.setCurrentDirectory(lastPath); }
            int returnValue = loadDialog.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File myfile = loadDialog.getSelectedFile();
                lastPath = new File(myfile.getParent());
                String[][] program = new String[20][2];
                try {
                    FileInputStream fis = new FileInputStream(myfile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    program = (String[][]) ois.readObject();
                    ois.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                }
                for (int i = 0; i < 20; i++) {
                    programField[i].setText(program[i][0]+" "+program[i][1]);
                }
            }
        }
    }
}
