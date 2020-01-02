package sysu.java.client.gui;

import sysu.java.Utils;
import sysu.java.client.host.SocketTask;
import sysu.java.server.host.ServerCommands;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

interface RemoveMethod {
	void remove(String path);
}

class FileInfo {
	private String fileName;
	private int length;
	private String type;

	public FileInfo(String fileName, int length, String type) {
		this.fileName = fileName;
		this.length = length;
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

interface DownloadMethod {
	void download(String path, String target);
}

class FileTable extends JTable {
	public FileTable() {
		super(new DefaultTableModel(new Object[][]{}, new String[]{"文件名", "大小", "类型"}));
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void addRow(FileInfo info) {
		var model = (DefaultTableModel) getModel();
		model.addRow(new Object[]{info.getFileName(), info.getLength() + " 字节", info.getType()});
	}

	public void removeRow(int index) {
		var model = (DefaultTableModel) getModel();
		model.removeRow(index);
	}

	public void clearAllRows() {
		var model = (DefaultTableModel) getModel();
		model.setRowCount(0);
	}

	public FileInfo getCurrentSelection() {
		var index = this.getSelectedRow();
		var model = (DefaultTableModel) getModel();
		if (index == -1) return null;
		return new FileInfo(model.getValueAt(index, 0).toString(),
				Integer.parseInt(model.getValueAt(index, 1).toString().replaceAll("\\s+字节", "")),
				model.getValueAt(index, 2).toString());
	}
}

class DeleteActionListener implements ActionListener {
	private String path;
	private RemoveMethod method;

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			method.remove(path);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMethod(RemoveMethod method) {
		this.method = method;
	}
}

class DownloadActionListener implements ActionListener {
	private String path;
	private DownloadMethod method;

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (method == null) {
				JOptionPane.showMessageDialog(null, "不支持下载文件夹");
				return;
			}
			var chooser = new JFileChooser();
			chooser.setDialogTitle("选择下载位置");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showDialog(null, "确定") != JFileChooser.APPROVE_OPTION) return;
			var file = chooser.getSelectedFile();
			if (file == null) return;
			if (!file.exists()) return;
			method.download(path, file.getPath() + "/" + Path.of(path).getFileName().toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMethod(DownloadMethod method) {
		this.method = method;
	}
}

public class ClientUI extends JFrame {
	private Socket socket;
	private FileTable table;
	private String currentPath = "";
	private final JLabel currentPathLabel = new JLabel();

	public ClientUI(Socket socket) {
		this.socket = socket;
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("jPan");
		setLayout(null);
		initUI();
		setVisible(true);
		listFiles(currentPath);
	}

	private void initUI() {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JPopupMenu menu = new JPopupMenu();
		JMenuItem download = new JMenuItem("下载");
		JMenuItem delete = new JMenuItem("删除");
		var deleteAction = new DeleteActionListener();
		delete.addActionListener(deleteAction);
		var downloadAction = new DownloadActionListener();
		download.addActionListener(downloadAction);

		table = new FileTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var currentSelection = table.getCurrentSelection();
				if (currentSelection == null) return;
				var path = currentPath.isBlank() ?
						currentSelection.getFileName() : currentPath + "/" + currentSelection.getFileName();
				if (currentSelection.getType().equals("文件夹") && e.getClickCount() == 2) {
					currentPath = path;
					updateCurrentPathLabel();
					listFiles(currentPath);
				} else if (!currentSelection.getType().equals("文件夹")) {
					deleteAction.setPath(path);
					downloadAction.setPath(path);
					downloadAction.setMethod(ClientUI.this::downloadFile);
					try {
						deleteAction.setMethod(ClientUI.this::removeFile);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (e.getButton() == MouseEvent.BUTTON3) {
						menu.setVisible(true);
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				} else if (currentSelection.getType().equals("文件夹")) {
					deleteAction.setPath(path);
					downloadAction.setMethod(null);
					try {
						deleteAction.setMethod(ClientUI.this::removeFolder);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (e.getButton() == MouseEvent.BUTTON3) {
						menu.setVisible(true);
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

		menu.add(download);
		menu.add(delete);

		currentPathLabel.setBounds(16, 16, 600, 20);
		updateCurrentPathLabel();
		add(currentPathLabel);
		JScrollPane pane = new JScrollPane(table);
		pane.setBounds(16, 36, 750, 500);
		add(pane, BorderLayout.CENTER);

		var nf = new JButton("新建文件夹");
		nf.setBounds(426, 16, 100, 20);
		nf.addActionListener(e -> {
			var nf1 = JOptionPane.showInputDialog("输入文件夹名称：");
			if (nf1 == null) return;
			var folder = currentPath + "/" + nf1;
			createFolder(folder);
			updateCurrentPathLabel();
		});
		add(nf);

		var upload = new JButton("上传");
		upload.setBounds(540, 16, 66, 20);
		upload.addActionListener(e -> {
			var chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle("选择上传文件");
			if (chooser.showDialog(null, "选择") != JFileChooser.APPROVE_OPTION) return;
			var file = chooser.getSelectedFile();
			if (file == null) return;
			if (!file.exists()) return;
			var filename = file.getName();
			var target = currentPath.isBlank() ? filename : currentPath + "/" + filename;

			uploadFile(file.getPath(), target);
		});
		add(upload);

		var back = new JButton();
		back.setBounds(620, 16, 66, 20);
		back.setText("上层");
		back.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (currentPath.isBlank()) return;
				var index = currentPath.lastIndexOf("/");
				currentPath = index == -1 ? "" : currentPath.substring(0, index);
				updateCurrentPathLabel();
				listFiles(currentPath);
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		add(back);

		var refresh = new JButton();
		refresh.setBounds(700, 16, 66, 20);
		refresh.setText("刷新");
		refresh.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listFiles(currentPath);
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

		add(refresh);
	}

	private void updateCurrentPathLabel() {
		currentPathLabel.setText("当前目录: /" + currentPath);
	}

	// 删除服务器上 path 文件夹和该文件夹下的所有文件
	private void removeFolder(String path) {
		var task = new SocketTask() {
			@Override
			public void completedTask(int length, int status, PipedInputStream input) {
				try {
					String[] result = Utils.getResult(input, length);
					if (!result[0].equals("success"))
						JOptionPane.showMessageDialog(null, result[1]);
					else
						JOptionPane.showMessageDialog(null, "成功");
					listFiles(currentPath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		try {
			var data = new ByteArrayOutputStream();
			data.write(Utils.intToByte4(ServerCommands.RemoveFolder.ordinal()));
			data.write(path.getBytes(StandardCharsets.UTF_8));
			task.postTask(socket, data.toByteArray());
			data.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 在服务器上创建 path 路径的文件夹
	private void createFolder(String path) {
		var task = new SocketTask() {
			@Override
			public void completedTask(int length, int status, PipedInputStream input) {
				try {
					String[] result = Utils.getResult(input, length);
					if (!result[0].equals("success"))
						JOptionPane.showMessageDialog(null, result[1]);
					else
						JOptionPane.showMessageDialog(null, "成功");
					listFiles(currentPath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		try {
			var data = new ByteArrayOutputStream();
			data.write(Utils.intToByte4(ServerCommands.CreateFolder.ordinal()));
			data.write(path.getBytes(StandardCharsets.UTF_8));
			task.postTask(socket, data.toByteArray());
			data.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 将服务器上 path 路径的文件删除
	private void removeFile(String path) {
		var task = new SocketTask() {
			@Override
			public void completedTask(int length, int status, PipedInputStream input) {
				try {
					String[] result = Utils.getResult(input, length);
					if (!result[0].equals("success"))
						JOptionPane.showMessageDialog(null, result[1]);
					else
						JOptionPane.showMessageDialog(null, "成功");
					listFiles(currentPath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		try {
			var data = new ByteArrayOutputStream();
			data.write(Utils.intToByte4(ServerCommands.RemoveFile.ordinal()));
			data.write(path.getBytes(StandardCharsets.UTF_8));
			task.postTask(socket, data.toByteArray());
			data.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 将服务器上 path 路径的文件下载到 target
	private void downloadFile(String path, String target) {
		var file = new File(target);
		if (file.exists()) {
			// 文件已存在
			JOptionPane.showMessageDialog(null, "文件已存在");
			return;
		}
		var task = new SocketTask() {

			@Override
			public void completedTask(int length, int status, PipedInputStream input) {
				try {
					if (status != 0) { // 标志位为不成功
						String[] result = Utils.getResult(input, length);
						if (!result[0].equals("success"))
							JOptionPane.showMessageDialog(null, result[1]);
						else
							JOptionPane.showMessageDialog(null, "成功");
						return;
					}
					// 否则
					OutputStream fileStream;
					try {
						if (!file.exists())
							fileStream = new FileOutputStream(file);
						else fileStream = new ByteArrayOutputStream();
					} catch (Exception ex) {
						fileStream = new ByteArrayOutputStream();
					}
					Utils.transferWithLength(length, input, fileStream);
					fileStream.close();
					JOptionPane.showMessageDialog(null, "成功");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		try {
			var data = new ByteArrayOutputStream();
			data.write(Utils.intToByte4(ServerCommands.DownloadFile.ordinal()));
			data.write(path.getBytes(StandardCharsets.UTF_8));
			task.postTask(socket, data.toByteArray());
			data.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 将本机的 path 路径的文件上传至服务器的 target
	private void uploadFile(String path, String target) {
		var task = new SocketTask() {
			@Override
			public void completedTask(int length, int status, PipedInputStream input) {
				try {
					String[] result = Utils.getResult(input, length);
					if (!result[0].equals("success"))
						JOptionPane.showMessageDialog(null, result[1]);
					else
						JOptionPane.showMessageDialog(null, "成功");
					listFiles(currentPath);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		var file = new File(path);
		if (!file.exists()) {
			// 本机路径为 path 的文件不存在
			JOptionPane.showMessageDialog(null, "文件不存在");
			return;
		}
		var fileLen = (int) file.length();
		try {
			var fileStream = new FileInputStream(file);
			var output = new PipedOutputStream();
			var input = new PipedInputStream(output);
			var targetPath = target.getBytes(StandardCharsets.UTF_8);
			new Thread(() -> {
				try {
					output.write(Utils.intToByte4(ServerCommands.UploadFile.ordinal()));
					output.write(targetPath);
					output.write(Utils.messageSplitter);
					Utils.transferWithLength(fileLen, fileStream, output);
					fileStream.close();
					output.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}).start();
			task.postTask(socket,
					targetPath.length + 4 + Utils.messageSplitter.length + fileLen, input);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 列出服务器 path 目录下的所有文件
	private void listFiles(String path) {
		table.clearAllRows();
		var task = new SocketTask() {
			@Override
			public void completedTask(int length, int status, PipedInputStream input) {
				try {
					if (status != 0) { // 标志位为不成功
						String[] result = Utils.getResult(input, length);
						JOptionPane.showMessageDialog(null, result[1]);
						return;
					}
					// 否则
					byte[] data;
					var files = new ArrayList<String>();
					var dirs = new ArrayList<String>();
					do {
						data = Utils.seekSplitter(new DataInputStream(input));
						if (data.length > 0) {
							var name = new String(data, StandardCharsets.UTF_8);
							if (name.startsWith("d")) dirs.add(name);
							else files.add(name);
						}
					} while (data.length > 0);
					// 文件夹
					for (var i : dirs) {
						var fileInfo = i.split(":");
						table.addRow(new FileInfo(fileInfo[2], Integer.parseInt(fileInfo[1]), "文件夹"));
					}
					// 文件
					for (var i : files) {
						var fileInfo = i.split(":");
						table.addRow(new FileInfo(fileInfo[2], Integer.parseInt(fileInfo[1]), "文件"));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		try {
			var data = new ByteArrayOutputStream();
			data.write(Utils.intToByte4(ServerCommands.ListFiles.ordinal()));
			data.write(path.getBytes(StandardCharsets.UTF_8));
			task.postTask(socket, data.toByteArray());
			data.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
