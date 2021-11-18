package jcm.gui;

import jcm.CounterMachine;
import jcm.machine.Machine;
import jcm.machine.Parser;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

@SuppressWarnings("serial")
class MyButton extends JButton {
    MyButton(String s) {
        super(s);
        this.setFont(new Font("Sans", Font.PLAIN, 16));
    }
}

public class MachineUI {

  static final boolean shouldFill = true;
  static final boolean shouldWeightX = true;

  public int pointer = 0;
  public JFrame frame;
  private JButton button;

  public JTextField[] prog;
  public JTextField[] reg;
  public JLabel[] point;

  private void fillGrid(Container pane) {
    pane.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    if (shouldFill) {
      // natural height; maximum width
      c.fill = GridBagConstraints.HORIZONTAL;
    }

    prog = new JTextField[20];
    point = new JLabel[20];
    for (int i = 1; i <= 20; i++) {
      c.gridx = 0;
      c.gridy = i;
      c.ipady = 10;
      button = new JButton(Integer.toString(i));
      pane.add(button, c);
      c.gridx = 1;
      c.gridy = i;
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
    setPointer(0,0);
  }

  private void fillToolBar(Container pane) {
    MyButton load = new MyButton("Load");
    MyButton save = new MyButton("Save");
    MyButton reset = new MyButton("Reset");
    MyButton run = new MyButton("Run");
    pane.add(load);
    pane.add(save);
    pane.add(reset);
    pane.add(run);
    save.addActionListener(e -> {
        Parser p = new Parser(this);
        File myfile = new File("cmprogram.cmp");
        JFileChooser saveDialog = new JFileChooser();
        int returnValue = saveDialog.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            myfile = saveDialog.getSelectedFile();
            System.out.println(myfile.getAbsoluteFile());
            System.out.println(myfile.getName());
        } else {
            System.out.println("canceled");
        }
    });
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
//      ClassLoader cl = this.getClass().getClassLoader();
//      URL url = cl.getResource("/de/msitc/cmachine/res/icons8-left-arrow-48.png");
//      URL url = getClass().getResource("/de/msitc/cmachine/res/icons8-left-arrow-48.png");
//      Image img = new ImageIcon(url).getImage();
//      ImageIcon ico = new ImageIcon(img);
//      point[next].setIcon(ico);
      //point[next].setIcon(new ImageIcon("images/icons8-left-arrow-48.png"));
      java.net.URL url = getClass().getResource("../res/icons8-left-arrow-48.png");
      ImageIcon ico = new ImageIcon(url, "<----");
      point[next].setIcon(ico);
      System.out.println(url);
      pointer = next;
  }

  class RunProgram implements ActionListener {
      private MachineUI machine;
      RunProgram(MachineUI m) {
          this.machine = m;
      }
    @Override
    public void actionPerformed(ActionEvent e) {
      Parser p = new Parser(machine);
      Thread m = new Machine(machine, p.getProgram(), p.getRegister(), machine.pointer);
      m.start();
    }
  }
  
//  class SaveProgram implements ActionListener {
      
//  }
}
