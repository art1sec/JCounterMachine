package jcm.machine;

import jcm.gui.*;

public class Parser {

    private MachineUI machine;
    private int[] register;
    private String[][] program;

    public Parser(MachineUI machine) {
        this.machine = machine;
        register = new int[6];
        program = new String[20][2];
        parseProgram();
    }

    public void parseProgram() {
        for (int i = 0; i < 20; i++) {
            String field = machine.getProgramField(i);
            if (field.equals("Stop")) {
                program[i][0] = "Stop";
                program[i][1] = "";
            } else {
                try {
                    String f[] = field.split(" ", 2);
                    program[i][0] = f[0].toUpperCase();
                    program[i][1] = f[1].toUpperCase();
                } catch (Exception e) {
                    program[i][0] = "";
                    program[i][1] = "";
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (machine.getRegisterField(i).length() > 0) {
                register[i] = Integer.parseInt(machine.getRegisterField(i));
            } else {
                register[i] = 0;
            }
        }
    }

    public String[][] getProgram() {
        return program;
    }

    public int[] getRegister() {
        return register;
    }
}
