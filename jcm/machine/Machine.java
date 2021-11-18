package de.msitc.cmachine.cm;

import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import de.msitc.cmachine.gui.*;

public class Machine extends Thread {
    
    private int pointer;
    private int[] register;
    private String[][] program;
    private MachineUI ui;
    private boolean stop = false;
    
    public Machine(MachineUI ui, String[][] pr, int[] r, int p) {
        this.program = pr;
        this.register = r;
        this.pointer = p;
        this.ui = ui;
    }
    
    @Override
    public void run() {
        while (!stop) {
            String com = program[pointer][0];
            if (com.startsWith("S")) {
                stop = true;
                continue;
            }
            if (com.equals("+")) {
                char c = program[pointer][1].charAt(0);
                int r = ((int) c - 65);
                register[r] += 1;
                ui.reg[r].setText(String.valueOf(register[r]));
                movePointer(pointer+1);
            }
            if (com.equals("-")) {
                char c = program[pointer][1].charAt(0);
                int r = ((int) c - 65);
                register[r] -= 1;
                ui.reg[r].setText(String.valueOf(register[r]));
                movePointer(pointer+1);
            }
            if (com.equals("J")) {
                int i = Integer.parseInt(program[pointer][1]);
                movePointer(i-1);
            }
            if (com.equals("0")) {
                char c = program[pointer][1].charAt(0);
                int r = ((int) c - 65);
                if (register[r] == 0) {
                    movePointer(pointer+2);
                } else {
                    movePointer(pointer+1);
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void movePointer(int next) {
        ui.setPointer(pointer, next);
        pointer = next;
    }
}
