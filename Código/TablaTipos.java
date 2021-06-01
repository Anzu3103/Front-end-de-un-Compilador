/**
*	Fecha de modificación : 10 feb 2021
*	@author Elsa
*/

import java.util.ArrayList;

public class TablaTipos{
	ArrayList<Type> tablaTipos;

	public TablaTipos(){
		this.tablaTipos = new ArrayList<Type>();
		this.tablaTipos.add(new Type(0, "int", 4, -1, -1));
		this.tablaTipos.add(new Type(1, "float", 4, -1, -1));
		this.tablaTipos.add(new Type(2, "char", 1, -1, -1));
		this.tablaTipos.add(new Type(3, "double", 8, -1, -1));
		this.tablaTipos.add(new Type(4, "void", 0, -1, -1));
		this.tablaTipos.add(new Type(5, "cadena", -1, -1, 2));
	}

	void insertar(int id, String nombre, int tam, String valor_num, int tipo){
		this.tablaTipos.add(new Type(id, nombre, tam ,Integer.parseInt(valor_num),tipo));
	}

	int tam(){
		return this.tablaTipos.size();
	}


	int getTam(int id){
		for(Type t : this.tablaTipos){
			if(id== t.id){
				return t.tam;
			}
		}
		return -1;
	}

	int getTipoBase(int id){
		for(Type t : this.tablaTipos){
			if(id== t.id){
				return t.tipoBase;
			}
		}
		return -1;
	}

	String getNombreTipo(int tipo){
		for(Type t : this.tablaTipos){
			if(tipo == t.id){
				return t.type;
			}
		}
		return null;
	}


	void printTT(){
		System.out.println("\nTabla de tipos");
		System.out.println("id\ttipo\ttam\t# elem\ttipoBase");

		for(Type t : this.tablaTipos){
			System.out.println(t.id+"\t"+t.type+"\t"+t.tam+"\t"+t.elem+"\t"+t.tipoBase);
		}
	}
}