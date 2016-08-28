import move.MoveSystem;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class MoveApplet extends JApplet implements ActionListener, Runnable{
    private JTextArea inputArea;
    private JTextArea outputArea;

	PipedOutputStream pOut;
	BufferedReader reader;

	private boolean programRunning;

    public void init() {

    	setBackground(Color.lightGray);

        JPanel panel = new JPanel(new GridBagLayout());

        inputArea = new JTextArea(5, 20);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setPreferredSize(new Dimension(250,500));

        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setPreferredSize(new Dimension(250,500));

        JButton runButton = new JButton("Run");
        runButton.setActionCommand("run");
        runButton.addActionListener(this);

        JButton clearButton = new JButton("Clear");
        clearButton.setActionCommand("clear");
        clearButton.addActionListener(this);

        JButton killButton = new JButton("Kill");
        killButton.setActionCommand("kill");
        killButton.addActionListener(this);


        JLabel  inLabel   = new JLabel("Program Text");

        JLabel  outLabel  = new JLabel("Program Output");

        //input area
        GridBagConstraints inputConstraints = new GridBagConstraints();
        inputConstraints.gridx = 0;
        inputConstraints.gridy = 1;
        inputConstraints.gridwidth = 3;
        inputConstraints.gridheight = 9;
        inputConstraints.weightx = 0.7;
        inputConstraints.weighty = 0.5;
        inputConstraints.fill = GridBagConstraints.BOTH;
		panel.add(inputScrollPane, inputConstraints);

		//output area
		GridBagConstraints outputConstraints = new GridBagConstraints();
        outputConstraints.gridx = 3;
        outputConstraints.gridy = 1;
        outputConstraints.gridwidth = 3;
        outputConstraints.gridheight = 9;
        outputConstraints.weightx = 0.5;
        outputConstraints.weighty = 0.5;
        outputConstraints.fill = GridBagConstraints.BOTH;
		panel.add(outputScrollPane, outputConstraints);

		//run button
		GridBagConstraints runConstraints = new GridBagConstraints();
        runConstraints.gridx = 1;
        runConstraints.gridy = 10;
        runConstraints.gridwidth = 1;
        runConstraints.gridheight = 1;
        runConstraints.weightx = 0.5;
        runConstraints.weighty = 0.5;
        runConstraints.fill = GridBagConstraints.NONE;
		panel.add(runButton, runConstraints);

		//clear button
		GridBagConstraints clearConstraints = new GridBagConstraints();
        clearConstraints.gridx = 3;
        clearConstraints.gridy = 10;
        clearConstraints.gridwidth = 1;
        clearConstraints.gridheight = 1;
        clearConstraints.weightx = 0.5;
        clearConstraints.weighty = 0.5;
        clearConstraints.fill = GridBagConstraints.NONE;
		panel.add(clearButton, clearConstraints);


		//kill button
		GridBagConstraints killConstraints = new GridBagConstraints();
        killConstraints.gridx = 5;
        killConstraints.gridy = 10;
        killConstraints.gridwidth = 1;
        killConstraints.gridheight = 1;
        killConstraints.weightx = 0.5;
        killConstraints.weighty = 0.5;
        killConstraints.fill = GridBagConstraints.NONE;
		panel.add(killButton, killConstraints);

		//Input Label
		GridBagConstraints iLabelConstraints = new GridBagConstraints();
        iLabelConstraints.gridx = 0;
        iLabelConstraints.gridy = 0;
        iLabelConstraints.gridwidth = 3;
        iLabelConstraints.gridheight = 1;
        iLabelConstraints.weightx = 0.5;
        iLabelConstraints.weighty = 0.5;
        iLabelConstraints.fill = GridBagConstraints.NONE;
		panel.add(inLabel, iLabelConstraints);

		//Output Label
		GridBagConstraints oLabelConstraints = new GridBagConstraints();
        oLabelConstraints.gridx = 3;
        oLabelConstraints.gridy = 0;
        oLabelConstraints.gridwidth = 3;
        oLabelConstraints.gridheight = 1;
        oLabelConstraints.weightx = 0.5;
        oLabelConstraints.weighty = 0.5;
        oLabelConstraints.fill = GridBagConstraints.NONE;
		panel.add(outLabel, oLabelConstraints);

        add(panel);
    }

    public void actionPerformed(ActionEvent e){
    	if(e.getActionCommand().equals("run")){
    		if(!programRunning){
    			Thread moveProgram = new Thread(this);
    			moveProgram.start();
    		}
    	}else if(e.getActionCommand().equals("kill")){
			if(MoveSystem.isEnabled()){
				MoveSystem.stop();
			}
    	}else if(e.getActionCommand().equals("clear")){
    		outputArea.setText("");
    	}
    }
    public void run(){
		programRunning = true;
		MoveSystem.appletMain(inputArea.getText(), outputArea);
		outputArea.setText(outputArea.getText() + "\n");
		programRunning = false;
    }
}