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
            boolean singleStep)
    {
        this.ui = ui;
        this.program = program;
        this.register = register;
        this.pointer = ui.getPointer();
        this.speed = ui.getSpeed();
        this.singleStep = singleStep;
    }
    
    @Override
    public void run() {
        while (!stop) {
            String com = program[pointer][0];
            if (Thread.currentThread().isInterrupted()) {
                com = "Stop";
            }
            if (com.equals("+")) {
                char c = program[pointer][1].charAt(0);
                int r = ((int) c - 65);
                register[r] += 1;
                ui.setRegisterField(r, String.valueOf(register[r]));
                movePointer(pointer+1);
            }
            if (com.equals("-")) {
                char c = program[pointer][1].charAt(0);
                int r = ((int) c - 65);
                register[r] -= 1;
                ui.setRegisterField(r, String.valueOf(register[r]));
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
                ui.labelRunButton("Run");
                stop = true;
                continue;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                stop = true;
            }
        }
    }
    
    private void movePointer(int next) {
        pointer = next;
        ui.setPointer(pointer);
    }
}
