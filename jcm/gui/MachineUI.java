package jcm.gui;

import jcm.machine.Machine;
import jcm.machine.Parser;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
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
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;

public class MachineUI {

    static final boolean shouldFill = true;
    static final boolean shouldWeightX = true;

    private int speed = 1000;
    private JTextField speedField;
    public int pointer = 0;
    public JFrame frame;
    private JButton button;
    public MyButton run;

    public JTextField[] prog;
    public JTextField[] reg;
    public JLabel[] point;
    private Thread machineThread;

    java.net.URL url = getClass().getResource("/jcm/res/icons8-left-arrow-48.png");
    ImageIcon ico = new ImageIcon(url, "<<---<");

    private void fillGrid(Container pane) {
        System.out.println(url);
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        if (shouldFill) {
            // natural height; maximum width
            c.fill = GridBagConstraints.HORIZONTAL;
        }

        prog = new JTextField[20];
        point = new JLabel[20];
        InsetsUIResource with = new InsetsUIResource(0, 8, 0, 0);
        InsetsUIResource without = new InsetsUIResource(0, 0, 0, 0);
        for (int i = 1; i <= 20; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.ipady = 10;
            c.insets = with;
            button = new JButton(Integer.toString(i));
            pane.add(button, c);
            c.gridx = 1;
            c.gridy = i;
            c.insets = without;
            prog[i - 1] = new JTextField(5);
            pane.add(prog[i - 1], c);
            c.gridx = 2;
            c.gridy = i;
            point[i - 1] = new JLabel("     ");
            pane.add(point[i - 1], c);
        }
        reg = new JTextField[6];
        for (int i = 1; i <= 6; i++) {
            c.gridx = 3;
            c.gridy = i;
            button = new JButton(Character.toString((char) (64 + i)));
            pane.add(button, c);
            c.gridx = 4;
            c.gridy = i;
            reg[i - 1] = new JTextField(5);
            pane.add(reg[i - 1], c);
        }
        setPointer(0, 0);
        java.net.URL helpURL = getClass().getResource("/jcm/res/helptext.html");
        JEditorPane ePane = new JEditorPane();
        ePane.setEditable(false);
        ePane.setPreferredSize(new DimensionUIResource(300, 300));
        try {
            ePane.setPage(helpURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        c.gridx = 3;
        c.gridy = 8;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.ipadx = 0;
        c.ipady = 0;
        c.insets = without;
        speedField = new JTextField(5);
        speedField.setBorder(BorderFactory.createTitledBorder("Speed (ms):"));
        speedField.setText(String.valueOf(speed));
        pane.add(speedField,c );
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

    private void fillToolBar(Container pane) {
        MyButton load = new MyButton("Load");
        MyButton save = new MyButton("Save");
        MyButton reset = new MyButton("Reset");
        run = new MyButton("Run");
        pane.add(load);
        pane.add(save);
        pane.add(reset);
        pane.add(run);
        load.addActionListener(new LoadProgram());
        save.addActionListener(new SaveProgram(this));
        reset.addActionListener(e -> {
            setPointer(pointer, 0);
        });
        run.addActionListener(new RunProgram(this));
    }

    public void createAndShowGUI() {
        // if (System.getProperty("os.name").startsWith("Linux")) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }

        frame = new JFrame("Simple Counter Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel head = new JLabel("Simple Counter Machine");
        head.setFont(new Font("Sans", Font.PLAIN, 20));
        head.setHorizontalAlignment(JLabel.CENTER);
        head.setBorder(BorderFactory.createEmptyBorder(8, 0, 16, 0));
        frame.add(head, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        fillGrid(panel);
        frame.add(panel);

        JPanel toolbar = new JPanel();
        fillToolBar(toolbar);
        toolbar.setFont(new Font("Sans", Font.PLAIN, 16));
        frame.add(toolbar, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public void setPointer(int old, int next) {
        point[pointer].setIcon(null);
        point[next].setIcon(ico);
        // System.out.println(url);
        pointer = next;
    }

    class RunProgram implements ActionListener {
        private MachineUI machine;

        RunProgram(MachineUI m) {
            this.machine = m;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (run.getText().equals("Run")) {
                run.setText("Stop");
                Parser p = new Parser(machine);
                speed = Integer.parseInt(speedField.getText());
                machineThread = new Machine(machine, p.getProgram(), p.getRegister(), pointer, speed);
                machineThread.start();    
            } else {
                machineThread.interrupt();
                run.setText("Run");
            }
        }
    }

    class SaveProgram implements ActionListener {
        private MachineUI machine;

        SaveProgram(MachineUI m) {
            this.machine = m;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File myfile = new File("cmprogram.cmp");
            JFileChooser saveDialog = new JFileChooser();
            int returnValue = saveDialog.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                myfile = saveDialog.getSelectedFile();
                System.out.println(myfile.getAbsoluteFile());
                try {
                    FileOutputStream fos = new FileOutputStream(myfile);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    Parser p = new Parser(machine);
                    oos.writeObject(p.getProgram());
                    oos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else {
                System.out.println("canceled");
            }
        }
    }

    class LoadProgram implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser loadDialog = new JFileChooser();
            int returnValue = loadDialog.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File myfile = loadDialog.getSelectedFile();
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
                    prog[i].setText(program[i][0]+" "+program[i][1]);
                }
            }
        }
    }
}
