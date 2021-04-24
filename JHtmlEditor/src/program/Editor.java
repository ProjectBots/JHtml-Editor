package program;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import include.GUI;
import include.GUI.Label;
import include.GUI.Menu;
import include.GUI.TextArea;
import include.NEF;
import include.StringSaveBack;
import include.StringSaveBack.ChangedException;
import include.StringSaveBack.SizeException;

public class Editor {
	
	private static int width = 800;
	private static int height = 1000;
	
	private static GUI edit = null;
	private static GUI out = null;
	
	private static StringSaveBack ssb = new StringSaveBack(10, "<html>\n\n</html>");
	
	public static void main(String[] args) {
		
		try {
			AutoComplete.create("autoMap.txt");
		} catch (IOException e2) {
			System.err.println("Cant read auto complete map");
			e2.printStackTrace();
			return;
		}
		
		GUI.WinLis winLis = new GUI.WinLis() {
			@Override
			public void onIconfied(WindowEvent e) {}
			@Override
			public void onFocusLost(WindowEvent e) {}
			@Override
			public void onFocusGained(WindowEvent e) {}
			@Override
			public void onDeIconfied(WindowEvent e) {}
			@Override
			public void onClose(WindowEvent e) {
				close();
			}
		};
		
		KeyListener klOut = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(!((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0)) return;
				if(e.getKeyCode() == KeyEvent.VK_S) {
					save();
				}
			}
		};
		
		KeyListener klEdit = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(!((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0)) return;
				if(e.getKeyCode() == KeyEvent.VK_S) {
					save();
				} else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
					autoComplete();
				} else if(e.getKeyCode() == KeyEvent.VK_Z) {
					String text = null;
					for(int i=0; i<2; i++) {
						try {
							text = ssb.backward();
							break;
						} catch (SizeException e1) {
							return;
						} catch (ChangedException e1) {
							saveSSB();
						}
					}
					if(text != null)
						setText(text);
				} else if(e.getKeyCode() == KeyEvent.VK_Y) {
					try {
						setText(ssb.forward());
					} catch (SizeException | ChangedException e1) {
						return;
					}
				} else if(e.getKeyCode() == KeyEvent.VK_V) {
					saveSSB();
				}
			}
		};
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int sw = gd.getDisplayMode().getWidth();
		int sh = gd.getDisplayMode().getHeight();
		
		edit = new GUI("Java Html Editor - Editor", width, height, null, new Point((sw/2)-width, (sh/2)-(height/2)));
		edit.addWinLis(winLis);
		edit.addKeyListener(klOut);
		
		
		
		Menu menu = new Menu("File", "Debug  Save as".split("  "));
		menu.setAL(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO debug
				System.out.println("debug");
			}
		});
		menu.setAL(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		edit.addMenu(menu);
		
		TextArea ta = new TextArea();
		ta.setPos(0, 0);
		ta.setText("<html>\n\n</html>");
		ta.setMargin(new Insets(10, 10, 10, 10));
		ta.setScroll(true);
		ta.setKeyLis(klEdit);
		ta.setTabSize(2);
		ta.setLis(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				update(false);
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				update(false);
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				update(false);
			}
		});
		edit.addTextArea(ta, "editor");
		
		GUI.setPreferredSize("sp::editor", (int) Math.round(width*0.9), (int) Math.round(height*0.9));
		
		
		
		out = new GUI("Java Html Editor - Output", width, height, edit, new Point(width, 0));
		out.addWinLis(winLis);
		out.addKeyListener(klOut);
		
		Label lab = new Label();
		lab.setPos(0, 0);
		lab.setText("");
		out.addLabel(lab, "out");
		
		
		edit.refresh();
		out.refresh();
		
	}
	
	
	private static List<Integer> banned = null;
	private static String lastText = null;
	private static int lastPos = -1;
	private static int lastId = -1;
	private static LocalDateTime ldt = LocalDateTime.now().minusSeconds(3);
	
	
	private static class Timer {
		
		private boolean running = false;
		
		private Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				try {
					Thread.sleep(1000);
					running = false;
					update(true);
				} catch (InterruptedException e) {}
			}
		});
		
		public Timer() {
			t.start();
		}
		
		public void stop() {
			t.interrupt();
		}
		
		public boolean isRunning() {
			return running;
		}
	}
	
	private static Timer t = null;
	
	public static boolean checkTimer() {
		if(t == null) return false;
		if(t.isRunning()) {
			t.stop();
			return true;
		}
		return false;
	}
	
	public static void autoComplete() {
		checkTimer();
		String text;
		int pos;
		if(LocalDateTime.now().isAfter(ldt.plusSeconds(1))) {
			text = GUI.getInputText("editor");
			lastText = text;
			pos = GUI.getCaretPos("editor");
			lastPos = pos;
			banned = new ArrayList<Integer>();
		} else {
			text = lastText;
			pos = lastPos;
			banned.add(lastId);
		}
		
		t = new Timer();
		
		
		setText(AutoComplete.complete(text, pos, banned));
		
		GUI.setCaretPos("editor", AutoComplete.getCaretPos());

		lastId = AutoComplete.getId();
		
		ldt = LocalDateTime.now();
	}
	
	
	public static void save() {
		String path = edit.showFileChooser("Save Html", true);
		if(path != null) {
			try {
				NEF.save(path, GUI.getText("editor"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static int c = 0;
	private static int u = 0;
	
	public static void update(boolean b) {
		if(u == 0) {
			if(checkTimer() || b || ++c == 5) {
				saveSSB();
			} else {
				ssb.change();
			}
		} else {
			u--;
		}
		
		GUI.setText("out", htmlTestImg(GUI.getInputText("editor")));
	}
	
	public static String htmlTestImg(String text) {
		StringBuilder sb = new StringBuilder(text);
		
		int index = 0;
		while(true) {
			index = sb.indexOf("<img src=\"", index) + 10;
			if(index == 9) break;
			
			int lin = sb.indexOf("\"", index);
			
			if(lin != -1) {
				URL src = Editor.class.getResource("/" + sb.substring(index, lin));
				if(src == null) continue;
				sb.replace(index, lin, src.toString());
			}
		}
		
		return sb.toString();
	}
	
	public static void setText(String text) {
		u = 2;
		GUI.setText("editor", text);
		if(u==1) u=0;
	}
	
	public static void saveSSB() {
		ssb.save(GUI.getInputText("editor"));
		c = 0;
	}
	
	public static void close() {
		edit.close();
		out.close();
	}
}
