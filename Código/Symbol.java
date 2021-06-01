/**
*	Fecha de modificación : 1 enero 2021
*	@author Elsa
*/

import java.util.ArrayList;

public class Symbol {
	int type;
	int dir;
	String id;
	String var;
	ArrayList<Integer> args;

	public Symbol(String id, int dir, int type, String var, ArrayList<Integer> args){
		this.id = id;
		this.dir = dir;
		this.type = type;
		this.var = var;
		this.args = args;
	}
}
