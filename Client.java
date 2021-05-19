package com.google.api.services.samples.youtube.cmdline.youtube_cmdline_search_sample;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.net.*;
import java.io.*;


public class Client implements ActionListener{

	static String userInput;
	static JTextField tf;
	static PrintWriter out;
	static DataInputStream in;
	static BufferedReader br;
	
	public Client(){
		JFrame frame = new JFrame("Music Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 400);

		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter Text");
		tf = new JTextField(50);
		JButton search = new JButton("Search");
		panel.add(label);
		panel.add(tf);
		panel.add(search); 
		search.addActionListener(this);

		frame.getContentPane().add(BorderLayout.NORTH, panel);

		frame.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, SocketTimeoutException{

		new Client();
		Socket s = null;
        out = null;

        try {
            s = new Socket("localhost", 4999);
            out = new PrintWriter(s.getOutputStream(), true);
            
        } catch (UnknownHostException e) {
            System.err.println("Unknown error");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O error");
            System.exit(1);
        }
        
	}

	public void actionPerformed(ActionEvent e) {
		userInput = tf.getText();
		String cmd = e.getActionCommand();
		if(cmd=="Search")
	        out.println(userInput);
   }
}
