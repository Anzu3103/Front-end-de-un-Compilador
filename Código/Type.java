/**
*	Fecha de modificación : 1 enero 2021
*	@author Elsa
*/

public class Type {
	int id;
	String type;
	int tam;
	int elem;
	int tipoBase;

	public Type(int id, String type, int tam, int elem, int tipoBase){
		this.id = id; //num del tipo
		this.type = type; //nombre del tipo
		this.tam = tam; // tam en bytes
		this.elem = elem; //num de elementos en caso de ser arreglo
		this.tipoBase = tipoBase; //aplicable para arreglos
	}
	
	
}
