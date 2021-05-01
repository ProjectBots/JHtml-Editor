package program;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import include.NEF;

public class AutoComplete {

	private static class Key {
		
		private static int idCount = 0;
		
		private final int id;
		
		private String match = null;
		private int cpos = 0;
		
		private String insert = null;
		
		public Key(String match, int cpos) {
			this.match = match;
			this.cpos = cpos;
			id = idCount++;
		}
		
		public String getInsert() {
			return insert;
		}
		
		public int matches(String in) {
			for(int i=match.length(); i>=0; i--) {
				if(in.endsWith(match.substring(0, i))) {
					insert = match.substring(i);
					return i;
				}
			}
			return -1;
		}
		
		public void reset() {
			insert = null;
		}
		
		public int getOffset() {
			return cpos - match.length() + insert.length();
		}
		
		public int getId() {
			return id;
		}
	}
	
	public static void create(String path) throws IOException {
		String[] lines = NEF.read(path).split("\n");
		Arrays.sort(lines);
		keys = new Key[lines.length];
		int i=0;
		for(String line : lines) {
			int index = line.lastIndexOf(" ");
			String map = line.substring(0, index).replace("\\n", "\n");
			int caret = Integer.parseInt(line.substring(index+1));
			keys[i++] = new Key(map, caret);
		}
	}
	
	
	private static Key[] keys = null;
	
	public static String complete(String text, int pos, List<Integer> banned) {
		StringBuilder sb = new StringBuilder(text);
		
		String match = sb.substring(0, pos);
		AutoComplete.pos = pos;
		int bm = -1;
		String ins = null;
		for(Key key : keys) {
			if(banned.contains(key.getId())) continue;
			int m = key.matches(match);
			if(m > bm) {
				bm = m;
				ins = key.getInsert();
				AutoComplete.pos = pos + key.getOffset();
				id = key.getId();
			}
			key.reset();
		}
		
		if(ins != null) {
			sb.insert(pos, ins);
		}
		
		return sb.toString();
	}

	private static int pos = 0;
	private static int id = 0;
	
	public static int getCaretPos() {
		return pos;
	}
	
	public static int getId() {
		return id;
	}

	
	
}
