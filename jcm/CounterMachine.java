package jcm;

import jcm.gui.*;

public class CounterMachine {
    public static void main(String[] args) {
        MachineUI cm = new MachineUI();
        javax.swing.SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        cm.createAndShowGUI();
                    }
                });
    }
}
