/**
*	Proyecto Final: Compilador 
*	Semestre 2021-1
*	Fecha de modificación : 12 feb 2021
*	@author Elsa
*/

import java.io.*;

public class Main {
	public static void main(String args[]){
		try{
			FileReader fr = new FileReader(new File(args[0]));
			BufferedReader br = new BufferedReader(fr);
			Lex lexer = new Lex(br);
			Parser parser = new Parser(lexer);
		//	Parser0 parser = new Parser0(lexer);
		//	Parser1 parser = new Parser1(lexer);
			parser.parse(args[0]);
			if (parser.t_actual.equals(0) && parser.errores == 0)
			{
				System.out.println("Cadena Aceptada.");
			}
			else{
				System.out.println("La cadena no fue aceptada. Errores: "+  parser.errores);
			}
			br.close();
		}catch(IOException e){
			System.out.println("Error al abrir el archivo.");
		}
		
	}
}