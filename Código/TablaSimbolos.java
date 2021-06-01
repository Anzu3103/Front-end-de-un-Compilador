/**
*	Fecha de modificación : 10 feb 2021
*	@author Elsa
*/

import java.util.ArrayList;

public class TablaSimbolos{
	ArrayList<Symbol> tablaSimbolos;

	public TablaSimbolos(){
		tablaSimbolos = new ArrayList<Symbol>();

	}

	void insertar(String valor, int dir, int tipo, String var, ArrayList<Integer> args){
		this.tablaSimbolos.add(new Symbol(valor,dir, tipo, var, args ));

	}

	void printTS(){
		System.out.println("Tabla de simbolos");
		System.out.println("num\tid\t\tdir\t# tipo\tvar\targs");

		int i=0;
		for(Symbol s : this.tablaSimbolos){
			System.out.println(""+i+"\t"+s.id+"\t\t"+s.dir+"\t"+s.type+"\t"+s.var+"\t"+s.args);
			i++;
		}    
	}

	boolean buscar(String id){
		for(Symbol s: this.tablaSimbolos){
			if(s.id.equals(id)){
				return true;
			}
		}
		return false;
	}

	int buscarTipo(String id){
		for(Symbol s: this.tablaSimbolos){
			if(s.id.equals(id)){
				return s.type;
			}
		}
		return -1;
	}

	String buscarVar(String id){
		for(Symbol s: this.tablaSimbolos){
			if(s.id.equals(id)){
				return s.var;
			}
		}
		return null;
	}

	ArrayList <Integer> buscarArgs(String id){
		for(Symbol s: this.tablaSimbolos){
			if(s.id.equals(id)){
				return s.args;
			}
		}
		return null;
	}
}