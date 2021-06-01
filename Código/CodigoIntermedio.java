/**
*	Fecha de modificación : 12 feb 2021
*	@author Elsa
*/

import java.util.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class CodigoIntermedio{
	FileWriter fw;
	BufferedWriter bw;
	ArrayList <Cuadrupla> codigo;

	public CodigoIntermedio (){
		codigo = new ArrayList <Cuadrupla>();
	}

	public void genCod(String operador, String arg1, String arg2, String res){
		Cuadrupla cuadrupla = new Cuadrupla(operador, arg1, arg2, res);	
		codigo.add(cuadrupla);
	}

	public void genCod(String operador, String res){
		Cuadrupla cuadrupla = new Cuadrupla(operador, "", "", res);	
		codigo.add(cuadrupla);
	}

	public void escribir(String nomArch){
		try{
			String ck = nomArch;
			String nombre = ck.substring(0,(ck.length() - 4));
			File salida = new File(nombre+".ci");
			if (!salida.exists()){
				salida.createNewFile();
			}
			fw = new FileWriter(salida);
			bw = new BufferedWriter(fw);
			//dependiendo de la cuadrupla, escribir el cod de 3 dir en archivo
			for (Cuadrupla c : codigo){
				bw.write(c.toString());

			}
			
			bw.close();
		}
		catch(Exception e){
				System.out.println("Ocurrio un error al intentar escribir el archivo");
		}
	}

}