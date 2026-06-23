package ui;

import javax.swing.*;

public class Window extends JFrame {

    private int high = 600;
    private int width = 600;

    //private final JButton button = new JButton("Нажать");
    //JPanel buttonsPanel = new JPanel();
    public Window(){
        this.setSize(high, width);
        this.setTitle("TV");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //this.getContentPane().add(BorderLayout.NORTH, buttonsPanel);
        //buttonsPanel.add(button);
    }

    public int getWidth(){
        return width;
    }

    public int getHigh(){
        return high;
    }

    public void press(){

    }



}
