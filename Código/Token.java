/**
*	Fecha de modificación : 1 enero 2021
*	@author Elsa
*/

public class Token{
	int clase;
	String valor;
	int type;
	int linea;

	public Token(int clase, String valor, int linea){
		this.clase = clase;
		this.valor = valor;
		this.linea = linea;
	}

	public Token(int clase, String valor, int type, int linea){
		this.clase = clase;
		this.valor = valor;
		this.type = type;
		this.linea = linea;
	}

	public Token(){
	}

	public boolean equals(Token x){
		return this.clase == x.clase;
	}

	public boolean equals(int clase){
		return this.clase == clase;
	}

}