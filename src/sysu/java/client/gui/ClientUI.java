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
	
	public client(String title) throws UnknownHostException, IOException {
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
		JMenuItem create, upload;
		
		create = createItem("NewFolder");
		pmenu.add(create);
		
		upload = createItem("Upload");
		pmenu.add(upload);
	
		icon = new ImageIcon(getClass().getResource("../icon/1.png"));
		this.setIconImage(icon.getImage());
		
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBackground(Color.WHITE);
		
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
		
		upload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
		        chooser.showDialog(new JLabel(), "选择");
				File fi = chooser.getSelectedFile();
				if(!Upload(fi))
					JOptionPane.showMessageDialog(null, "Failed!", "UpLoad", JOptionPane.ERROR_MESSAGE);
				else {
					String name = fi.getName();
					createFile(name);
				}
				
			}	
		});
		
		add(panel);
	}
	
	public void createFile(String name) {
		JLabel label = new JLabel(name);
		ImageIcon image = new ImageIcon(getClass().getResource("../icon/3.png"));
		image.setImage(image.getImage().getScaledInstance(100, 80,Image.SCALE_DEFAULT));
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setIcon(image);
		label.setSize(30, 30);
		
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
				
				if(e.getButton() == MouseEvent.BUTTON3) {
					menu.setVisible(true);
					menu.show(e.getComponent(), e.getX(), e.getY());
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
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		        chooser.showDialog(new JLabel(), "选择");
				File in = chooser.getSelectedFile();
				if(!Download(in))
					JOptionPane.showMessageDialog(null, "Failed!", "DownLoad", JOptionPane.ERROR_MESSAGE);
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
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		
		JPopupMenu menu2 = new JPopupMenu();
		JMenuItem nf = createItem("New Folder");
		nf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fname = JOptionPane.showInputDialog("Input the file name: ");
				createFolder(fname);
			}
		});
		menu2.add(nf);
		
		JPanel panel2 = new JPanel();
		JPopupMenu menu = new JPopupMenu();
		JMenuItem open, delete, back, rename;
		open = createItem("Open");
		delete = createItem("Delete");
		back = createItem("Back");
		rename = createItem("Rename");
		menu.add(open);
		menu.add(rename);
		menu.add(back);
		menu.add(delete);
		
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		rename.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String nm = JOptionPane.showInputDialog("Input the new name: ");
				label.setText(nm);
			}
		});
		
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				label.setVisible(false);
			}
		});
		
		back.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
			
		});
		
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setIcon(image);
		label.setSize(30, 30);
		
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
				if(e.getButton() == MouseEvent.BUTTON3) {
					menu2.setVisible(true);
					menu2.show(e.getComponent(), e.getX(), e.getY());
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
		
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					add(panel2);
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
	
	public boolean Upload(File fi) {
		try {
	        while (true) {
	        	// 选择进行传输的文件
	            System.out.println("文件长度:" + (int) fi.length());
	            DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(fi)));
	            DataOutputStream ps = new DataOutputStream(socket.getOutputStream());
	            //将文件名及长度传给客户端。
	            ps.writeUTF(fi.getName());
	            ps.flush();

	            int bufferSize = 8192;
	            byte[] buf = new byte[bufferSize];
	            while (true) {
	            	int read = 0;
	                if (fis != null) {
	                	read = fis.read(buf);
	                }

	                if (read == -1) {
	                    break;
	                }
	                ps.write(buf, 0, read);
	            }
	            ps.flush();
	                    
	            fis.close();
	            socket.close();                
	            System.out.println("文件传输完成");
	            return true;
	        }
	    } catch (Exception e1) {
	    	e1.printStackTrace();
	    	return false;
	    }  
	}
	
	public boolean Download(File in) {
		try {
		    while(true){  
		         DataInputStream is = new DataInputStream(socket.getInputStream());   
		         //1、得到文件名       
		         String filename = in.getAbsolutePath();
		         filename += is.readUTF();              
		         System.out.println("新生成的文件名为:"+filename);  
		         FileOutputStream fos = new FileOutputStream(filename);  
		         byte[] b = new byte[1024]; 
		         int length = 0;  
		         while((length=is.read(b))!=-1){  
		             //2、把socket输入流写到文件输出流中去  
		        	 fos.write(b, 0, length);  
		         }  
		         fos.flush();  
		         fos.close();               
		         is.close();  
		         socket.close(); 
		         return true;
		    }  
		} catch (IOException e1) {  
		  // TODO Auto-generated catch block  
			e1.printStackTrace();  
			return false;
		}   
	}
	
	public JMenuItem createItem(String name) {
		Font font =new Font(Font.SANS_SERIF, Font.BOLD, 15);
		JMenuItem r = new JMenuItem(name);
		r.setFont(font);
		return r;
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		JFrame _c = new client("My Disc");
		_c.setSize(1300, 900);
		_c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_c.setLocationRelativeTo(null);
		_c.setVisible(true);
		_c.setBackground(Color.BLACK);
	}
}
