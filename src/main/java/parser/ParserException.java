package parser;
/**
 * 
 * @author lischkls
 *	CTex_ParserException if something is wrong in string to parse
 */
public class ParserException extends Exception {
	private static final long serialVersionUID = 1L;

	public ParserException(String s){
		super(s);
	}

	public ParserException(){
		super();
	}
}

