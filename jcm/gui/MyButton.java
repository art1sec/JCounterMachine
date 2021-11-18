package jcm.gui;

import java.awt.Font;

import javax.swing.JButton;

//@SuppressWarnings("serial")
public class MyButton extends JButton {
    MyButton(String s) {
        super(s);
        this.setFont(new Font("Sans", Font.PLAIN, 16));
    }
}
