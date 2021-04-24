package include;

public class StringSaveBack {

	public static class SizeException extends Exception {
		private static final long serialVersionUID = -3517763122857714203L;
		public SizeException() {
			super("Out of Memory");
		}
	}
	
	public static class ChangedException extends Exception {
		private static final long serialVersionUID = -3517763122857714203L;
		public ChangedException() {
			super("Text has changed, please save first");
		}
	}
	
	private static class Pos {
		private int max;
		private int min;
		
		private int gpos;
		private int npos;
		
		public Pos(int min, int max, int start) {
			this.min = min;
			this.max = max;
			this.gpos = start;
			this.npos = start;
		}
		
		
		private int next() {
			return npos+1 > max ? min : npos+1;
		}
		
		private int back() {
			return npos-1 < min ? max : npos-1;
		}
		
		private int nextN() throws SizeException {
			//System.out.println("n " + npos + "::" + gpos);
			if(npos == gpos) throw new SizeException();
			npos = next();
			return npos;
		}
		
		private int backN() throws SizeException {
			int n = back();
			//System.out.println("b " + npos + "::" + n + "::" + gpos);
			if(n == gpos) throw new SizeException();
			npos = n;
			return npos;
		}
		
		private int setGPos() {
			gpos = next();
			npos = gpos;
			return npos;
		}
		
		private int getPos() {
			return npos;
		}
	}
	

	private String[] sb = null;
	
	private Pos pos = null;
	
	
	private boolean changed = false;
	
	public StringSaveBack(int size, String start) {
		sb = new String[size];
		sb[0] = start;
		pos = new Pos(0, size-1, 0);
	}
	
	
	public String backward() throws SizeException, ChangedException {
		if(changed) throw new ChangedException();
		if(sb[pos.back()] == null) throw new SizeException();
		return sb[pos.backN()];
	}
	
	public String forward() throws SizeException, ChangedException {
		if(changed) throw new ChangedException();
		return sb[pos.nextN()];
	}
	
	public void save(String text) {
		if(changed) {
			sb[pos.getPos()] = text;
			changed = false;
		} else {
			sb[pos.setGPos()] = text;
		}
	}
	
	public void change() {
		if(!changed) {
			changed = true;
			pos.setGPos();
		}
	}
	

	
	
}
