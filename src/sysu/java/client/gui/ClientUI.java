package sysu.java.client.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class ClientUI extends JFrame {
	private Socket socket = null;
	private ImageIcon icon;
	private JPanel panel;
	private int xp = 0;
	private int yp = 0;
	
	public byte[] intToByte4(int num) {
		byte[] b = new byte[4];
		b[0] = (byte)(num & 0xff);
		b[1] = (byte)((num >> 8) & 0xff);
		b[2] = (byte)((num >> 16) & 0xff);
		b[3] = (byte)((num >> 24) & 0xff);
		return b;
	}
	
	public ClientUI(String title) throws UnknownHostException, IOException {
		setTitle(title);
		
		init();
	}
	
	public void init() throws UnknownHostException, IOException {
		try {        
   		 	for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {    
   		 		if ("Nimbus".equals(info.getName())) {            
   		 			javax.swing.UIManager.setLookAndFeel(info.getClassName());     
   		 			break;        
   		 		}      
   		 	}     
   	 	}catch(Exception e) {      
   	 		System.out.println(e);       
   	 	}
		
		
		JPopupMenu pmenu = new JPopupMenu();
		JMenuItem create;
		
		create = createItem("NewFolder");
		pmenu.add(create);
	
		icon = new ImageIcon(getClass().getResource("../icon/1.png"));
		this.setIconImage(icon.getImage());
		
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBackground(Color.WHITE);
		
		/*
		JLabel disc = new JLabel("我的网盘");
		disc.setBackground(Color.DARK_GRAY);
		disc.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
		disc.setBounds(-1, -1, 1300, 80);
		disc.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		panel.add(disc, BorderLayout.NORTH);
		*/
		
		panel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getButton() == MouseEvent.BUTTON3) {
					pmenu.setVisible(true);
					pmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		create.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fname = JOptionPane.showInputDialog("Input the file name: ");
				createFolder(fname);
			}
		});
		
		add(panel);
	}
	
	public void createFile(String name) {
		JLabel label = new JLabel(name);
		JPopupMenu menu = new JPopupMenu();
		JMenuItem download, delete;
		download = createItem("DownLoad");
		delete = createItem("Delete");
		menu.add(download);
		menu.add(delete);
		
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setSize(15, 15);
		
		label.addMouseListener(new MouseListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getButton() == MouseEvent.BUTTON1) {
					if(e.getClickCount() == 2) {
						
					}
					label.setOpaque(false);
				}
				
				if(e.getButton() == MouseEvent.BUTTON3) {
					menu.setVisible(true);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				label.setOpaque(true);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		download.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY ); 
		        chooser.showDialog(new JLabel(), "选择");
				File in = chooser.getSelectedFile();
				
			}
		});
	
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				label.setVisible(false);
			}
		});
		
		panel.add(label);
		this.validate();
	}
	
	public void createFolder(String name) {
		ImageIcon image = new ImageIcon(getClass().getResource("../icon/3.png"));
		image.setImage(image.getImage().getScaledInstance(100, 80,Image.SCALE_DEFAULT));
		
		JLabel label = new JLabel(name);
		JPopupMenu menu = new JPopupMenu();
		JMenuItem download, delete;
		download = createItem("Open");
		delete = createItem("Delete");
		menu.add(download);
		menu.add(delete);
		
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setIcon(image);
		label.setSize(30, 30);
		
		JPopupMenu menu2 = new JPopupMenu();
		JMenuItem upload = createItem("Upload");
		menu2.add(upload);
		JPanel panel2 = new JPanel();
		panel2.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getButton() == MouseEvent.BUTTON3) {
					menu.setVisible(true);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		panel.add(label);
		this.validate();
	}
	
	public JMenuItem createItem(String name) {
		Font font =new Font(Font.SANS_SERIF, Font.BOLD, 14);
		JMenuItem r = new JMenuItem(name);
		r.setFont(font);
		return r;
	}
	
	public void deleteFile(JLabel d) {
		
	}
	
	public void sendMessage(Socket socket, String m) throws IOException {
		PrintWriter w = null;
		
	}
	
	public void receiveMessage(Socket socketm, String m) {
		
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		JFrame _c = new ClientUI("My Disc");
		_c.setSize(1300, 900);
		_c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_c.setLocationRelativeTo(null);
		_c.setVisible(true);
		_c.setBackground(Color.BLACK);
	}
}
