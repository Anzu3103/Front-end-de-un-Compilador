/**
*	Fecha de modificación : 12 feb 2021
*	@author Elsa
*/

import java.io.IOException;
import java.util.*;


public class Semantica{

	public static int numTemp = 0;
	public static int numEtq = 0;
	public static int numIndice = 0;

	public static String nuevaTemp(){
		return "t"+numTemp++;
	}

	public static String nuevaEtq(){
		return "Etq"+numEtq++;
	}

	public static String nuevoIndice(){
		return "i"+numIndice++;
	}

	public static ArrayList <String> nuevaListaIndices(){
		return new ArrayList <String>();
	}

	public static void reemplazarIndices(ArrayList <String> sentLista, String etqFinal, ArrayList<Cuadrupla> code){
		String etq;
		int tam = sentLista.size();
		int cont = 0;
		for (String e : sentLista){
			cont+=1;
			if (cont < tam){
				etq = nuevaEtq();
				for (Cuadrupla c: code){
					if (c.res.equals(e)){
						c.res = etq;
					}
				}
			}
			else{
				etq = etqFinal;
				for (Cuadrupla c: code){
					if (c.res.equals(e)){
						c.res = etq;
					}
				}
			}
		}
	}

	public static boolean equivalentes(Integer tipo1, Integer tipo2){
		if (tipo1.intValue()==tipo2.intValue()) return true;
		//int y float
		if (tipo1.intValue()==0 && tipo2.intValue()==1 || tipo1.intValue()==1 && tipo2.intValue()==0) return true;
		//int y double
		if (tipo1.intValue()==0 && tipo2.intValue()==3 || tipo1.intValue()==3 && tipo2.intValue()==0) return true;
		//float y double
		if (tipo1.intValue()==1 && tipo2.intValue()==3 || tipo1.intValue()==3 && tipo2.intValue()==1) return true;
		// char a int
		if ( tipo1.intValue()==2 && tipo2.intValue()==0 || tipo1.intValue()==0 && tipo2.intValue()==2) return true;

		return false;
	}

	public static boolean equivalentesLista(ArrayList<Integer> lista, Integer tipo){
		for (Integer e: lista){
			if(equivalentes(e, tipo));
			else return false;
		}
		return true;
	}


	public static boolean equivalentesListas(ArrayList<Integer> listaArgs, ArrayList<Integer> listaParam){
		if (!(listaArgs.size() == listaParam.size())) return false;
	//	System.out.println("lista Args: "+listaArgs);
	//	System.out.println("lista Param: "+listaParam);
		for (Integer e: listaArgs){
			for (Integer p: listaParam){
				if(e.equals(p)){
					listaParam.remove(0);
					break;
				}
				else return false;
			}
		}
		return true;
	}

	public static int maximo(int tipo1, int tipo2){
		if (tipo1 == tipo2) 
			return tipo1;
		if (tipo1==0 && tipo2==1 || tipo1==1 && tipo2==0) //int y float
			return 1;
		if (tipo1==0 && tipo2==3 || tipo1==3 && tipo2==0) //int y double
			return 3;
		if (tipo1==1 && tipo2==3 || tipo1==3 && tipo2==1) //float y double
			return 3;
		if (tipo1==0 && tipo2==2 || tipo1==2 && tipo2==0) //int y char
			return 0;
		return -1;
	}

	public static String ampliar(String dir, int menor, int mayor, CodigoIntermedio code, int linea){
		String temp;
		if (menor == mayor)
			return dir;
		else if (menor==0 && mayor==1){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("=","(float)"+dir,"",temp));
			return temp;
		}
		else if (menor == 2 && mayor == 0){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("=","(int)"+dir,"",temp));
			return temp;
		}
		else if (menor==0 && mayor==3){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("=","(double)"+dir,"",temp));
			return temp;
		}
		else if (menor == 1 && mayor == 3){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("=","(double)"+dir,"",temp));
			return temp;
		}
		else{
			return dir;
		}
	}
/*
0 - int
1	float
2	char
3	double
*/
	public static String reducir(String dir, int mayor, int menor, CodigoIntermedio code, int linea){
		String temp;
		if (menor == mayor)
			return dir;
		else if (menor==1 && mayor==3){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("","(float)"+dir,"",temp));
			return temp;
		}
		else if (menor==0 && mayor==1){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("","(int)"+dir,"",temp));
			return temp;
		}
		else if (menor==0 && mayor==3){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("","(int)"+dir,"",temp));
			return temp;
		}
		else if (menor==2 && mayor==0){
			temp =nuevaTemp();
			code.codigo.add(new Cuadrupla("","(char)"+dir,"",temp));
			return temp;
		}
		else{
			return dir;
		}
	}
}