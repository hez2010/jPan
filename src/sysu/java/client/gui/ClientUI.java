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
	private JPanel now;
	private CardLayout cl;
	private JPanel all;
	private int count = 1;
	
	public ClientUI(Socket socket) throws IOException {
		this.socket = socket;
		setSize(1300, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setBackground(Color.BLACK);
		setTitle("jPan");
		setVisible(true);
		init();
	}
	
	public void init() {
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
		
		cl = new CardLayout();
		all = new JPanel(cl);
		
		JPopupMenu pmenu = new JPopupMenu();
		JMenuItem create, upload;
		
		create = createItem("NewFolder");
		pmenu.add(create);
		
		upload = createItem("Upload");
		pmenu.add(upload);
	
		icon = new ImageIcon(getClass().getResource("../icon/1.png"));
		this.setIconImage(icon.getImage());
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		
		create.addActionListener(e -> {
			// TODO Auto-generated method stub
			String fname = JOptionPane.showInputDialog("Input the file name: ");
			createFolder(fname);
		});
		
		upload.addActionListener(e -> {
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

		});
		
		all.add(panel, count + "");
		count++;
		now = panel;
		add(all);
		cl.show(all, count - 1 + "");
	}
	
	public void createFile(String name) {
		JLabel label = new JLabel(name);
		String suffix = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
		String icon = "";
		if(suffix.equals("txt")) {
			icon = "../icon/t.png";
		} else if(suffix.equals("docx") || suffix.equals("doc")) {
			icon = "../icon/w.png";
		} else if(suffix.equals("pdf")) {
			icon = "../icon/p.png";
		} else if(suffix.equals("mp3")) {
			icon = "../icon/m.png";
		} else {
			icon = "../icon/o.png";
		}
		
		ImageIcon image = new ImageIcon(getClass().getResource(icon));
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
		
		download.addActionListener(e -> {
			// TODO Auto-generated method stub
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.showDialog(new JLabel(), "选择");
			File in = chooser.getSelectedFile();
			if(!Download(in))
				JOptionPane.showMessageDialog(null, "Failed!", "DownLoad", JOptionPane.ERROR_MESSAGE);
		});
	
		delete.addActionListener(e -> {
			// TODO Auto-generated method stub
			label.setVisible(false);
		});
		
		now.add(label);
		this.validate();
	}
	
	public void createFolder(String name) {
		ImageIcon image = new ImageIcon(getClass().getResource("../icon/3.png"));
		image.setImage(image.getImage().getScaledInstance(100, 80,Image.SCALE_DEFAULT));
		JLabel label = new JLabel(name);
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		now.add(label);
		this.validate();
		
		JPopupMenu menu2 = new JPopupMenu();
		JMenuItem nf = createItem("New Folder");
		JMenuItem upload = createItem("Upload");
		JMenuItem back1 = createItem("Back");
		
		back1.addActionListener(e -> {
			// TODO Auto-generated method stub
			cl.previous(all);
			now = all;
		});
		
		nf.addActionListener(e -> {
			// TODO Auto-generated method stub
			String fname = JOptionPane.showInputDialog("Input the file name: ");
			createFolder(fname);
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
					ImageIcon icon2 = new ImageIcon(getClass().getResource("../icon/3.png"));
					icon2.setImage(image.getImage().getScaledInstance(100, 80,Image.SCALE_DEFAULT));
					label.setIcon(icon2);
				}
			}	
		});
		
		menu2.add(nf);
		menu2.add(upload);
		menu2.add(back1);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel2.setBackground(Color.WHITE);
		all.add(panel2, count + "");
		count++;
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem open, delete, back, rename;
		open = createItem("Open");
		delete = createItem("Delete");
		back = createItem("Back");
		rename = createItem("Rename");
		menu.add(open);
		menu.add(rename);
		if(count > 3)
			menu.add(back);
		menu.add(delete);
		
		open.addActionListener(e -> {
			// TODO Auto-generated method stub
			cl.next(all);
			now = panel2;
		});
		
		rename.addActionListener(e -> {
			// TODO Auto-generated method stub
			String nm = JOptionPane.showInputDialog("Input the new name: ");
			label.setText(nm);
		});
		
		delete.addActionListener(e -> {
			// TODO Auto-generated method stub
			label.setVisible(false);
		});
		
		back.addActionListener(e -> {
			// TODO Auto-generated method stub
			cl.previous(all);
			now = all;
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
				
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					cl.next(all);
					now = panel2;
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
}
