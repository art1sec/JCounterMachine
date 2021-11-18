package jcm.machine;

import java.util.concurrent.TimeUnit;

import jcm.gui.*;

public class Machine extends Thread {
    
    private int speed;
    private int pointer;
    private int[] register;
    private String[][] program;
    private MachineUI ui;
    private boolean stop = false;
    private boolean singleStep = false;
    
    public Machine(
            MachineUI ui,
            String[][] program,
            int[] register,
            int pointer,
            int speed,
            boolean singleStep)
    {
        this.ui = ui;
        this.program = program;
        this.register = register;
        this.pointer = pointer;
        this.speed = speed;
        this.singleStep = singleStep;
    }
    
    @Override
    public void run() {
        while (!stop && !Thread.currentThread().isInterrupted()) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("STOP!");
            }
            String com = program[pointer][0];
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
            if (singleStep || com.startsWith("S") || com.equals("")) {
                ui.run.setText("Run");
                stop = true;
                continue;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                stop = true;
                // e.printStackTrace();
            }
        }
    }
    
    private void movePointer(int next) {
        ui.setPointer(pointer, next);
        pointer = next;
    }
}
