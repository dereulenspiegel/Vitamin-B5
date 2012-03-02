package de.akuz.android.utmumrechner.utils;

import java.io.PrintWriter;
import java.io.Writer;

public class CSVWriter {
	
	private PrintWriter pw;
	private char seperator;
	private char quotechar;
	private char escapechar;
	private String lineEnd;
	
	public static final char DEFAULT_ESCAPE_CHAR = '"';
	public static final char DEFAULT_SEPERATOR = ',';
	public static final char DEFAULT_QUOTE_CHAR = '"';
	public static final char NO_QUOTE_CHAR = '\u0000';
	public static final char NO_ESCAPE_CHAR = '\u0000';
	
	public static final String DEFAULT_LINE_END = "\n";

	public CSVWriter(Writer writer) {
		this(writer,DEFAULT_SEPERATOR,DEFAULT_QUOTE_CHAR,DEFAULT_ESCAPE_CHAR,DEFAULT_LINE_END);
	}
	
	public CSVWriter(Writer writer, char seperator, char quotechar, char escapechar, String lineEnd){
		this.pw = new PrintWriter(writer);
		this.seperator = seperator;
		this.quotechar = quotechar;
		this.escapechar = escapechar;
		this.lineEnd = lineEnd;
	}
	
	public void writeNext(String[] nextLine){
		if(nextLine == null){
			return;
		}
		StringBuffer sb = new StringBuffer();
		
		for(int i=0;i<nextLine.length;i++){
			if(i != 0){
				sb.append(seperator);
			}
			String nextElement = nextLine[i];
			if(nextElement == null){
				continue;
			}
			if(quotechar != NO_QUOTE_CHAR){
				sb.append(quotechar);
			}
			for(int j=0;j<nextElement.length();j++){
				char nextChar = nextElement.charAt(j);
				if(escapechar != NO_ESCAPE_CHAR && nextChar == quotechar){
					sb.append(escapechar).append(nextChar);
				} else if(escapechar != NO_ESCAPE_CHAR && nextChar == escapechar){
					sb.append(escapechar).append(nextChar);
				} else {
					sb.append(nextChar);
				}
			}
			if(quotechar != NO_QUOTE_CHAR){
				sb.append(quotechar);
			}
		}
		sb.append(lineEnd);
		pw.write(sb.toString());
	}
	
	public void flush(){
		pw.flush();
	}
	
	public void close(){
		pw.flush();
		pw.close();
	}

}
