package jcm.gui;

import java.awt.Font;

import javax.swing.JButton;

class DisabledButton extends JButton {
    DisabledButton(String s) {
        super(s);
        this.setEnabled(false);
        this.setFont(new Font("Sans", Font.BOLD, 12));
    }
}

class ToolbarButton extends JButton {
    ToolbarButton(String s) {
        super(s);
        this.setFont(new Font("Sans", Font.PLAIN, 16));
    }
}
