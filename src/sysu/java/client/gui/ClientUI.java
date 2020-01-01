package sysu.java.client.gui;

import sysu.java.Utils;
import sysu.java.client.host.SocketTask;
import sysu.java.server.host.ServerCommands;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.PipedInputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
		return new FileInfo(model.getValueAt(index, 0).toString(),
				Integer.parseInt(model.getValueAt(index, 1).toString()),
				model.getValueAt(index, 2).toString());
	}
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

public class ClientUI extends JFrame {
	private Socket socket;
	private ImageIcon icon;
	private JPanel now;
	private CardLayout cl;
	private JPanel all;
	private int count = 1;
	private FileTable table;
	private JScrollPane pane;
	private String currentPath = "";

	public ClientUI(Socket socket) {
		this.socket = socket;
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("jPan");
		setLayout(null);
		init();
		setVisible(true);
	}

	private void init() {
		table = new FileTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		pane = new JScrollPane(table);
		pane.setBounds(16, 36, 750, 500);
		add(pane, BorderLayout.CENTER);

		listFiles(currentPath);
	}

	private void listFiles(String path) {
		var task = new SocketTask() {
			@Override
			public void completedTask(int length, PipedInputStream input) {
				try {
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
					for (var i : dirs) {
						var fileInfo = i.split(":");
						table.addRow(new FileInfo(fileInfo[2], Integer.parseInt(fileInfo[1]), "文件夹"));
					}
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
