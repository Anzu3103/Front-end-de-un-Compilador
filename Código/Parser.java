/**
*	Fecha de modificación : 12 feb 2021
*	@author Elsa
*	@author Saul
*/


import java.io.IOException;
import java.util.*;

public class Parser{

	public static final int IGUAL = 1001;
	public static final int DIF_IG = 1002;
	public static final int MENOR = 1003;
	public static final int MENOR_O_IG = 1004;
	public static final int MAYOR = 1005;
	public static final int MAYOR_O_IG = 1006;
	public static final int MAS = 1007;
	public static final int MENOS = 1008;
	public static final int DIF = 1009;
	public static final int MUL = 1010;
	public static final int AND = 1011;
	public static final int ENTRE = 1012;
	public static final int MOD = 1013;
	public static final int OR = 1014;
	public static final int INT = 1015;
	public static final int FLOAT = 1016;
	public static final int CHAR = 1017;
	public static final int DOUBLE = 1018;
	public static final int VOID = 1019;
	public static final int FUNC = 1020;
	public static final int IF = 1021;
	public static final int ELSE = 1022;
	public static final int SWITCH = 1023;
	public static final int CASE = 1024;
	public static final int DEFAULT = 1025;
	public static final int DO = 1026;
	public static final int BREAK = 1027;
	public static final int WHILE = 1028;
	public static final int RETURN = 1029;
	public static final int PRINT = 1030;
	public static final int SCAN = 1031;
	public static final int NUM = 1032;
	public static final int CADENA = 1033;
	public static final int BOOL_LIT = 1034;
	public static final int ID = 1035;
	public static final int L_PAR = 1036;
	public static final int R_PAR = 1037;
	public static final int L_COR = 1038;
	public static final int R_COR = 1039;
	public static final int L_LLAVE = 1040;
	public static final int R_LLAVE = 1041;
	public static final int PYC = 1042;
	public static final int DOS_PUNTOS = 1043;
	public static final int COMA = 1044;

	Lex lexer;
	Token t_actual;
	int dir = 0, errores = 0;

	Stack <TablaTipos> pilaTT;
	Stack <TablaSimbolos> pilaTS;
	TablaTipos ttGlobal;
	TablaSimbolos tsGlobal;
	Stack <Integer> pila_dir;
	ArrayList<Integer> listaRetorno;
	ArrayList<String> tablaCadena;
	CodigoIntermedio codigo;

	public Parser(Lex lexer)throws IOException{
		this.lexer = lexer;
		t_actual = lexer.yylex();
		ttGlobal = new TablaTipos();
		tsGlobal = new TablaSimbolos();
		pilaTT = new Stack<TablaTipos>();
		pilaTS = new Stack<TablaSimbolos>();
		pila_dir = new Stack <Integer>();
		tablaCadena = new ArrayList<String>();
		codigo = new CodigoIntermedio();
	}	

	void parse(String nomArch)throws IOException{
		programa(); //Símbolo inicial
		System.out.println("\n\tTablas globales");
		tsGlobal.printTS();  //Para tabla de Símbolos g
		ttGlobal.printTT();  //Para tabla de Tipos g
		codigo.escribir(nomArch);
	}

	void eat(int i) throws IOException{
		if(t_actual.equals(i)){
			t_actual =lexer.yylex();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis (eat)");
			errores +=1;
		}
	}

/*
	Los atributos heredados van a ser argumentos a la funcion
	Los atributos sintetizados son valores de retornado
*/

	void programa()throws IOException{
		pilaTT.push(ttGlobal);
		pilaTS.push(tsGlobal);

		declaraciones();
		funciones();
	
	}

	void declaraciones()throws IOException{
		int t_tipo;
		if(t_actual.equals(INT) || t_actual.equals(FLOAT) || t_actual.equals(CHAR) || t_actual.equals(DOUBLE) || t_actual.equals(VOID)){
			t_tipo=tipO();
			lista_var(t_tipo);
			eat(PYC);
			declaraciones();
		}
		else{ }
	}

	int tipO()throws IOException{
		int tipo;
		if(t_actual.equals(INT) || t_actual.equals(FLOAT) || t_actual.equals(CHAR) || 
			t_actual.equals(DOUBLE) || t_actual.equals(VOID)){

			tipo = basico();
			tipo = compuesto(tipo); //La base/tipo de compuesto es igual al tipo de Basico
			return tipo; //el tipo de Tipo es igual al de Compuesto
		}
		else {
			error("Error Sintactico: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 1");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return -1;
		} 
	}

	int basico()throws IOException{
		if(t_actual.equals(INT)){
			eat(INT);	
			return 0;
		}
		else if(t_actual.equals(FLOAT)){
			eat(FLOAT);
			return 1;
		}
		else if(t_actual.equals(CHAR)){
			eat(CHAR);
			return 2;
		}
		else if(t_actual.equals(DOUBLE)){
			eat(DOUBLE);
			return 3;
		}
		else if(t_actual.equals(VOID)){
			eat(VOID);
			return 4;
		}
		else { 
			error("Error sintactico: tipo invalido: " + t_actual.valor+ " linea: "+t_actual.linea);
			errores +=1;
			t_actual = lexer.yylex();
			error("Error sintactico 2");
			error("Errores: " + errores);
			System.exit(1);
			return -1;
		}
	}

	int compuesto(int base )throws IOException{
		int tipo, id;
		String valor_num;
		if(t_actual.equals(L_COR)){
			eat(L_COR);
			valor_num = t_actual.valor; //recuperando numero
			eat(NUM);
			eat(R_COR);
			tipo=compuesto(base); //heredando a base  compuesto1.tipo=compuesto.tipo
			id = pilaTT.peek().tam();
			int tam = Integer.parseInt(valor_num) * pilaTT.peek().getTam(tipo);
			pilaTT.peek().insertar(id, "array", tam ,valor_num,tipo);
			return id;
		}
		else {   //genera a epsilon compuesto.tipo=compuesto.base 
			return base;
		}
	}

	void lista_var(int l_tipo)throws IOException{ //heredado de lista_var.tipo = Tipo.tipo
		
		if(t_actual.equals(ID)){
			if (!pilaTS.peek().buscar(t_actual.valor)){ //insertar
				pilaTS.peek().insertar(t_actual.valor,dir, l_tipo, "var", null);
				dir+= pilaTT.peek().getTam(l_tipo); //llamar a tabla de tipos
			}
			else {
				error("Error semantico: Variable definida anteriormente: "+ t_actual.valor + " linea: "+t_actual.linea);
				errores+=1;
				error("Errores: " + errores);
				System.exit(1);
			}
			eat(ID);
			lista_varP(l_tipo);
		}
		else{
			error("Error sintactico: se esperaba un identificador: " + t_actual.valor+" linea: "+t_actual.linea);
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
	}

	void lista_varP(int lp_tipo)throws IOException{		
		if(t_actual.equals(COMA)){
			eat(COMA);
			if (t_actual.equals(ID)){
				if (!pilaTS.peek().buscar(t_actual.valor)){ //insertar
					pilaTS.peek().insertar(t_actual.valor,dir, lp_tipo, "var", null);
					dir+= pilaTT.peek().getTam(lp_tipo); //llamar a tabla de tipos
				}
				else {
					error("Error semantico: Variable definida anteriormente: "+ t_actual.valor + " linea: "+t_actual.linea);
					error("Errores: " + errores);
					System.exit(1);
				}
			eat(ID);
			lista_varP(lp_tipo);
			}
			else{
				error("Error sintactico: se esperaba un identificador: " + t_actual.valor+ " linea: "+t_actual.linea);
				errores +=1;
				error("Errores: " + errores);
				System.exit(1);
			}
		}
		else{ }
	}

	void funciones()throws IOException{
		int tipo;
		String id = "";
		ArrayList<Integer> args;
		listaRetorno = new ArrayList<Integer>();
		if(t_actual.equals(FUNC)){
			eat(FUNC);
			tipo = tipO();
			if (t_actual.equals(ID)){
				
				int linea = t_actual.linea;
				if (!pilaTS.peek().buscar(t_actual.valor)){ 
					id=t_actual.valor;
					pilaTS.push(new TablaSimbolos());
					pilaTT.push(new TablaTipos());
					pila_dir.push(dir);
					dir = 0;
					codigo.genCod("label",id);
				}
				else { 
					error("Error semantico: Variable definida anteriormente: "+ t_actual.valor + ", linea: "+t_actual.linea);
					errores+=1;
					error("Errores: " + errores);
					System.exit(1);
				}
				eat(ID);
				eat(L_PAR);
				args = argumentos();
				eat(R_PAR);
				bloque();
				
				System.out.println("\n****\nTablas de Func: "+id);
				pilaTS.peek().printTS();  //Para tabla de Símbolos
				pilaTT.peek().printTT();  //Para tabla de Tipos
				System.out.println("****");
				pilaTS.pop();
				pilaTT.pop();
				dir = pila_dir.pop();
				
				if (Semantica.equivalentesLista(listaRetorno, tipo)){
				//	String sig = Semantica.nuevaEtq();
				//	codigo.genCod("label",sig);
				}
				else{
					error("Error semantico: los tipos de retorno no coinciden con el tipo de la funcion '" + id + "'. Tipo: " + pilaTT.peek().getNombreTipo(tipo) + ", linea: " + linea);
					errores += 1;
					error("Errores: " + errores);
					System.exit(1);
				}

				pilaTS.peek().insertar(id,0, tipo, "func", args); //agregar args
				funciones();

			}
			else{
				error("Error sintactico: se esperaba un identificador: " + t_actual.valor + " linea: "+t_actual.linea);
				errores +=1;
				error("Errores: " + errores);
				System.exit(1);
			}
			
		}
		else { }
	}

	ArrayList<Integer> argumentos()throws IOException{
		if(t_actual.equals(INT) || t_actual.equals(FLOAT) || t_actual.equals(CHAR) 
			|| t_actual.equals(DOUBLE) || t_actual.equals(VOID)){
			return lista_args();
		}
		else { 
			return null;
		}
	}

	ArrayList<Integer> lista_args()throws IOException{
		ArrayList<Integer> listaHer = new ArrayList<Integer>();
		int tipo;
		if(t_actual.equals(INT) || t_actual.equals(FLOAT) || t_actual.equals(CHAR) 
			|| t_actual.equals(DOUBLE) || t_actual.equals(VOID)){
			tipo = tipO();
			listaHer.add(tipo);
			if (t_actual.equals(ID)){
				if (!pilaTS.peek().buscar(t_actual.valor)){ //insertar
					pilaTS.peek().insertar(t_actual.valor,dir, tipo, "arg", null); //agregar args
					dir+= pilaTT.peek().getTam(tipo); //llamar a tabla de tipos
				}
				else {
					error("Error semantico: Variable definida anteriormente: "+ t_actual.valor + " linea: "+t_actual.linea);
				}
			}
			else {
				error("Error sintactico: se esperaba un identificador: " + t_actual.valor + " linea: "+t_actual.linea);
				errores +=1;
			}
			eat(ID);
			ArrayList<Integer> lista=lista_argsP(listaHer);
			return lista;
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 4");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return null;
		}
	}

	ArrayList<Integer> lista_argsP(ArrayList<Integer> listaHer)throws IOException{
		int tipo;
		
		if(t_actual.equals(COMA)){
			eat(COMA);
			tipo = tipO();
			listaHer.add(tipo);
			if (t_actual.equals(ID)){
				if (!pilaTS.peek().buscar(t_actual.valor)){ //insertar
					pilaTS.peek().insertar(t_actual.valor,dir, tipo, "arg", null); //agregar args
					dir+= pilaTT.peek().getTam(tipo); //llamar a tabla de tipos
				}
				else {
					error("Error semantico: Variable definida anteriormente: "+ t_actual.valor + " linea: "+t_actual.linea);
				}
			}
			else {
				error("Error sintactico: se esperaba un identificador: " + t_actual.valor + " linea: "+t_actual.linea);
				errores +=1;
				error("Errores: " + errores);
				System.exit(1);
			}
			eat(ID);
			return lista_argsP(listaHer);
		}
		else {	
			return listaHer;
		}
	}

	void bloque()throws IOException{
		
		if(t_actual.equals(L_LLAVE)){
			eat(L_LLAVE);
			declaraciones();
			instrucciones();
			eat(R_LLAVE);
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 5");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		} 
	}

	void instrucciones()throws IOException{
		if (t_actual.equals(ID)|| t_actual.equals(IF) || t_actual.equals(WHILE) 
			|| t_actual.equals(DO) || t_actual.equals(BREAK) || t_actual.equals(L_LLAVE) 
			|| t_actual.equals(RETURN) || t_actual.equals(SWITCH) || t_actual.equals(PRINT) || t_actual.equals(SCAN)){
			String sig = Semantica.nuevaEtq();
			sentencia(sig);
			codigo.genCod("label", sig);
			instruccionesP();	
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 6");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
	}

	void instruccionesP()throws IOException{
		if (t_actual.equals(ID)|| t_actual.equals(IF) || t_actual.equals(WHILE) 
			|| t_actual.equals(DO) || t_actual.equals(BREAK) || t_actual.equals(L_LLAVE) 
			|| t_actual.equals(RETURN) || t_actual.equals(SWITCH)  || t_actual.equals(PRINT) || t_actual.equals(SCAN)){
			
			String sig = Semantica.nuevaEtq();
			sentencia(sig);
			codigo.genCod("label", sig);
			instruccionesP();	
		}
		else{ }
	}

	void sentencia(String sig)throws IOException{
		Dir_tipo auxBool;
		if (t_actual.equals(ID)){
			int linea = t_actual.linea;
			Dir_tipo auxParteIzq = parte_izquierda();
			eat(IGUAL);
			auxBool = bool(Semantica.nuevaEtq(),Semantica.nuevaEtq());
			eat(PYC);
			//error("auxBool es: "+auxBool.tipo+ " y auxParte es "+auxParteIzq.tipo);
			if (Semantica.equivalentes(auxParteIzq.tipo,auxBool.tipo)){
				String d1 = Semantica.reducir(auxBool.dir, auxBool.tipo, auxParteIzq.tipo, codigo, linea);
				codigo.genCod("=",d1,null,auxParteIzq.dir);
			}
			else{
				error("Error semantico: tipos incompatibles. Tipos: "+ pilaTT.peek().getNombreTipo(auxParteIzq.tipo) +
				 " y " +pilaTT.peek().getNombreTipo(auxBool.tipo) +", en linea " + linea);
				errores +=1;
				error("Errores: " + errores);
				System.exit(1);
			}	
		}
		else if(t_actual.equals(IF)){
			eat(IF);
			eat(L_PAR);
			String boolV = Semantica.nuevaEtq();
			String boolF = Semantica.nuevoIndice();
			bool(boolV, boolF);
			eat(R_PAR);
			codigo.genCod("label", boolV);
			sentencia(sig);
			ArrayList <String> listaIndices_sent = Semantica.nuevaListaIndices();
			listaIndices_sent.add(boolF);
			Sent(sig, listaIndices_sent);
		}
		else if(t_actual.equals(WHILE)){
			eat(WHILE);
			eat(L_PAR);
			String sigHer = Semantica.nuevaEtq();
			String boolV = Semantica.nuevaEtq();
			String boolF = sig;
			codigo.genCod("label", sigHer);
			bool(boolV, boolF);
			eat(R_PAR);
			codigo.genCod("label", boolV);
			sentencia(sigHer);
			codigo.genCod("goto", sigHer);
		}
		else if(t_actual.equals(DO)){
			eat(DO);
			String sigHer = Semantica.nuevaEtq();
			String boolV = Semantica.nuevaEtq();
			String boolF = sig;
			codigo.genCod("label", boolV);
			sentencia(sigHer);
			codigo.genCod("label", sigHer);
			eat(WHILE);
			eat(L_PAR);
			bool(boolV, boolF);
			eat(R_PAR);
		}
		else if(t_actual.equals(BREAK)){
			eat(BREAK);
			eat(PYC);
			codigo.genCod("goto", sig);	
		}
		else if(t_actual.equals(L_LLAVE)){
			//Ya no hereda atrib sig
			bloque();
		}
		else if (t_actual.equals(RETURN)){
			eat(RETURN);
			int tipo = Func_ret();
			listaRetorno.add(tipo);
		}
		else if(t_actual.equals(SWITCH)){
			eat(SWITCH);
			eat(L_PAR);
			auxBool = bool(Semantica.nuevaEtq(), Semantica.nuevaEtq());
			String casosEtqPrueba = Semantica.nuevaEtq();
			codigo.genCod("goto", casosEtqPrueba);
			eat(R_PAR);
			eat(L_LLAVE);
			String casosPrueba = casos(sig, auxBool.dir);
			codigo.genCod("label", casosEtqPrueba);
			codigo.genCod(casosPrueba, "");
			eat(R_LLAVE);
		}
		else if(t_actual.equals(PRINT)){
			eat(PRINT);
			Dir_tipo auxExp = exp();
			codigo.genCod("print", auxExp.dir);
			eat(PYC);
		}
		else if(t_actual.equals(SCAN)){
			eat(SCAN);
			Dir_tipo aux = parte_izquierda();
			codigo.genCod("scan", aux.dir);
		}
		else {	
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 7");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
	}

	Dir_tipo parte_izquierda()throws IOException{
		Dir_tipo aux = new Dir_tipo();
		if(t_actual.equals(ID)){
			if (pilaTS.peek().buscar(t_actual.valor)){ 
				String idBase = t_actual.valor;
				eat(ID);
				aux = parte_izquierdaP(idBase, t_actual.linea);
				return aux;
			}
			else {
				error("Error semantico: Variable no declarada: "+ t_actual.valor + " linea: "+t_actual.linea);
				errores+=1;
				error("Errores: " + errores);
				System.exit(1);
				return aux;
			}
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 23");
			errores+=1;
			error("Errores: " + errores);
			System.exit(1);
			return aux;
		}
	}

	Dir_tipo parte_izquierdaP(String idBase, int linea)throws IOException{
		Dir_tipo aux = new Dir_tipo();
		String base = idBase;
		if(t_actual.equals(L_COR)){
			aux = localizacion(base, t_actual.linea);
		}
		/*localizacion.base = parte_izquierdaP.base
			parte_izquierdaP.dir = localizacion.dir
			parte_izquierdaP.tipo = localizacion.tipo
		*/
		else{ 
			if (pilaTS.peek().buscar(base)){ 
				aux.dir = base;
				aux.tipo = pilaTS.peek().buscarTipo(base);
			}
			else{
				error("Error semantico: Variable no declarada: "+ t_actual.valor + " linea: "+t_actual.linea);
				errores+=1;
				error("Errores: " + errores);
				System.exit(1);
			}
		}
		return aux;
	}

	void Sent(String sig, ArrayList<String> listaIndices_sent)throws IOException{

		if(t_actual.equals(ELSE)){
			eat(ELSE);
			codigo.genCod("goto", sig);
			codigo.genCod("label", listaIndices_sent.get(0));
			sentencia(sig);
			Semantica.reemplazarIndices(listaIndices_sent, Semantica.nuevaEtq(), codigo.codigo);
		}
		else {
			Semantica.reemplazarIndices(listaIndices_sent, sig, codigo.codigo);
		}
	}

	int Func_ret()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
			|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
			|| t_actual.equals(BOOL_LIT)){
			Dir_tipo auxExp = exp();
			codigo.genCod("return", auxExp.dir);
			eat(PYC);
			return auxExp.tipo;
		}
		else if(t_actual.equals(PYC)){
			codigo.genCod("return", "");
			eat(PYC);
			return 4; //void
		}
		else {	
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 8");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return -1;
		}
	}

	String casos(String sig, String dir)throws IOException{
		
		if (t_actual.equals(CASE)){
			String casoPrueba = caso(sig, dir);
			String casosPrueba = casos(sig, dir);
			return casoPrueba + " || "+ casosPrueba;
		}
		else if (t_actual.equals(DEFAULT)){
			String predetPrueba = predeterminado(sig);
			return predetPrueba;			
		}
		else {
			return "";
		}
	}

	String caso(String sig, String dir)throws IOException{
		
		if(t_actual.equals(CASE)){
			eat(CASE);
			String val = t_actual.valor;
			eat(NUM);
			eat(DOS_PUNTOS);
			String casoInicio = Semantica.nuevaEtq();
			String casoPrueba = "if "+ dir + "==" +val+ "goto" +casoInicio;
			codigo.genCod("label", casoInicio);
			instrucciones();
			return casoPrueba+"\n";
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 9");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return null;
		}
	}

	String predeterminado(String sig)throws IOException{
		
		if(t_actual.equals(DEFAULT)){
			eat(DEFAULT);
			eat(DOS_PUNTOS);
			String predeterminadoInicio = Semantica.nuevaEtq();
			String predeterminadoPrueba = "goto" + predeterminadoInicio;
			codigo.genCod("label", predeterminadoInicio);
			instrucciones();
			return predeterminadoPrueba+"\n";
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 10 ");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return null;
		}
	}

	Dir_tipo bool(String vddr, String fls)throws IOException{
		Dir_tipo aux = new Dir_tipo();

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT)  ){	
			
			String combVddr = vddr;
			String combFls = Semantica.nuevoIndice();

			Dir_tipo auxComb = comb(combVddr, combFls);

			ArrayList<String> listaIndicesBoolP = Semantica.nuevaListaIndices();
			listaIndicesBoolP.add(combFls);
			codigo.genCod("label",combFls);

			Dir_tipo auxboolP = boolP(auxComb, listaIndicesBoolP, vddr, fls);
			aux.tipo=auxboolP.tipo;
			aux.dir = auxComb.dir;
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 11");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
		return aux;
	}

	Dir_tipo boolP(Dir_tipo her, ArrayList<String> listaIndicesBoolP, String vddr, String fls) throws IOException{
		Dir_tipo aux = new Dir_tipo();
		if(t_actual.equals(OR)){
			int linea = t_actual.linea;
			eat(OR);
			
			String combVddr = vddr;
			String combFls = Semantica.nuevoIndice();
			Dir_tipo auxComb = comb(combVddr, combFls);

			/* equivalencias tipo con Her y comb*/
			if (Semantica.equivalentes(her.tipo, auxComb.tipo)){
				listaIndicesBoolP.add(combFls);
				codigo.genCod("label",combFls);
				Dir_tipo auxboolP = boolP(auxComb, listaIndicesBoolP, vddr, fls);
				aux.tipo=auxboolP.tipo;
				aux.dir = auxComb.dir;
			}
			else{
				error("Error semantico: los tipos son imcompatibles. Expresion OR en linea " + linea);
				errores+=1;
				error("Errores: " + errores);
				System.exit(1);
			}

			return aux;
		}
		else {
			Semantica.reemplazarIndices(listaIndicesBoolP, fls, codigo.codigo);
			aux.tipo = her.tipo; //aux.tipo = 0;
			aux.dir = her.dir;
			return aux;
		}
	}

	Dir_tipo comb(String vddr, String fls)throws IOException{
		Dir_tipo aux = new Dir_tipo();
		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){

			String igVddro = Semantica.nuevoIndice();
			Dir_tipo auxIgualdad = igualdad(igVddro, fls);
			ArrayList <String> lista_indices = Semantica.nuevaListaIndices();
			lista_indices.add(igVddro);
			Dir_tipo auxCombP = combP(auxIgualdad, lista_indices, vddr, fls);
			aux.tipo = auxCombP.tipo;
			aux.dir = auxIgualdad.dir;
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 12 ");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
		return aux;
	}

	Dir_tipo combP(Dir_tipo her, ArrayList<String> lista_indices, String vddr, String fls)throws IOException{
		Dir_tipo aux = new Dir_tipo();
		if(t_actual.equals(AND)){
			int linea = t_actual.linea;
			eat(AND);

			String igVddro = Semantica.nuevoIndice();
			Dir_tipo auxIgualdad = igualdad(igVddro, fls);

			//equivalencias tipo con Her e igualdad
			if (Semantica.equivalentes(her.tipo, auxIgualdad.tipo)){
				
				lista_indices.add(igVddro);
				codigo.genCod("label", igVddro);
				Dir_tipo auxCombP = combP(auxIgualdad, lista_indices, vddr, fls);
				aux.tipo = auxCombP.tipo;
				aux.dir = auxCombP.dir;
			}
			else{
				error("Error semantico: los tipos son imcompatibles. Expresion AND en linea " + linea);
				errores+=1;
				error("Errores: " + errores);
				System.exit(1);
			}
		
		}
		else{
			Semantica.reemplazarIndices(lista_indices, vddr, codigo.codigo);
			aux.tipo = her.tipo; //aux.tipo = 0;
			aux.dir = her.dir;
		}

		return aux;
	}

	Dir_tipo igualdad(String vddr, String fls)throws IOException{
		Dir_tipo aux = new Dir_tipo();

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){

			Dir_tipo auxRel = rel(vddr, fls);
			aux = igualdadP(auxRel, vddr, fls);
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 13");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
		return aux;
	}

	Dir_tipo igualdadP(Dir_tipo her, String vddr, String fls)throws IOException{
		Dir_tipo aux = new Dir_tipo();
		Dir_tipo herP = new Dir_tipo();
		
		if(t_actual.equals(IGUAL)){
			int linea = t_actual.linea;
			eat(IGUAL);
			eat(IGUAL);
			Dir_tipo auxRel = rel(vddr, fls);
			if (Semantica.equivalentes(her.tipo, auxRel.tipo)){
				
				herP.tipo = Semantica.maximo(her.tipo, auxRel.tipo);
				herP.dir = Semantica.nuevaTemp();
				aux = igualdadP(herP, vddr, fls);
				 //String operador, String arg1, String arg2, String res)
				String op = "==";
	  			String d1 = Semantica.ampliar(her.dir, her.tipo, herP.tipo, codigo, linea);
				String d2 = Semantica.ampliar(auxRel.dir, auxRel.tipo, herP.tipo, codigo, linea);
				codigo.genCod ( op, d1, d2, aux.dir);
				
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion IGUAL en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}
			
			return aux;
		}
		else if(t_actual.equals(DIF_IG)){
			eat(DIF_IG);
			int linea = t_actual.linea;
			Dir_tipo auxRel = rel(vddr, fls);
			if (Semantica.equivalentes(her.tipo, auxRel.tipo)){
				herP.tipo = Semantica.maximo(her.tipo, auxRel.tipo);
				herP.dir = Semantica.nuevaTemp();
				aux = igualdadP(herP, vddr, fls);
				 //String operador, String arg1, String arg2, String res)
				String op = "!=";
	  			String d1 = Semantica.ampliar(her.dir, her.tipo, herP.tipo, codigo, linea);
				String d2 = Semantica.ampliar(auxRel.dir, auxRel.tipo, herP.tipo, codigo, linea);
				codigo.genCod ( op, d1, d2, aux.dir);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion '!=' en linea: "+linea);
				errores +=1;
				error("Errores: " + errores);
				System.exit(1);
			}
			
			return aux;
		}
		else{ 
			return her;
		}
	}

	Dir_tipo rel(String vddr, String fls)throws IOException{
		Dir_tipo aux = new Dir_tipo();

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			aux = exp();

			Dir_tipo ret = relP(aux, vddr, fls);

			return ret;
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 14 ");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return aux;
		}
	}

	Dir_tipo relP(Dir_tipo her, String vddr, String fls)throws IOException{
		Dir_tipo aux = new Dir_tipo();
		Dir_tipo ret = new Dir_tipo();
		
		if(t_actual.equals(MENOR_O_IG)){
			int linea = t_actual.linea;
			eat(MENOR_O_IG);
			aux = exp();

			if (Semantica.equivalentes(her.tipo, aux.tipo)){
				int tipoTemp = Semantica.maximo(her.tipo, aux.tipo);
				ret.dir = Semantica.nuevaTemp();
				ret.tipo = 0;
				String op = "<=";
	  			String d1 = Semantica.ampliar(her.dir, her.tipo, tipoTemp, codigo, linea);
				String d2 = Semantica.ampliar(aux.dir, aux.tipo, tipoTemp, codigo, linea);
				codigo.genCod (op, d1, d2, aux.dir);
				codigo.genCod ("if", ret.dir, vddr, "goto");
				codigo.genCod ("goto", fls);

			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion MENOR O IGUAL en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}
			
			return ret;
		}
		else if(t_actual.equals(MAYOR_O_IG)){
			int linea = t_actual.linea;
			eat(MAYOR_O_IG);
			aux = exp();
			if (Semantica.equivalentes(her.tipo, aux.tipo)){
				int tipoTemp = Semantica.maximo(her.tipo, aux.tipo);
				ret.dir = Semantica.nuevaTemp();
				ret.tipo = 0;
				String op = ">=";
	  			String d1 = Semantica.ampliar(her.dir, her.tipo, tipoTemp, codigo, linea);
				String d2 = Semantica.ampliar(aux.dir, aux.tipo, tipoTemp, codigo, linea);
				codigo.genCod (op, d1, d2, aux.dir);
				codigo.genCod ("if", ret.dir, vddr, "goto");
				codigo.genCod ("goto", fls);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion MAYOR O IGUAL en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}
			return ret;
		}
		else if(t_actual.equals(MENOR)){
			int linea = t_actual.linea;
			eat(MENOR);
			aux = exp();
			if (Semantica.equivalentes(her.tipo, aux.tipo)){
				int tipoTemp = Semantica.maximo(her.tipo, aux.tipo);
				ret.dir = Semantica.nuevaTemp();
				ret.tipo = 0;
				String op = "<";
	  			String d1 = Semantica.ampliar(her.dir, her.tipo, tipoTemp, codigo, linea);
				String d2 = Semantica.ampliar(aux.dir, aux.tipo, tipoTemp, codigo, linea);
				codigo.genCod (op, d1, d2, aux.dir);
				codigo.genCod ("if", ret.dir, vddr, "goto");
				codigo.genCod ("goto", fls);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion MENOR en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}
			return ret;
		}
		else if(t_actual.equals(MAYOR)){
			int linea = t_actual.linea;
			eat(MAYOR);
			aux = exp();
			if (Semantica.equivalentes(her.tipo, aux.tipo)){
				int tipoTemp = Semantica.maximo(her.tipo, aux.tipo);
				ret.dir = Semantica.nuevaTemp();
				ret.tipo = 0;
				String op = ">";
	  			String d1 = Semantica.ampliar(her.dir, her.tipo, tipoTemp, codigo, linea);
				String d2 = Semantica.ampliar(aux.dir, aux.tipo, tipoTemp, codigo, linea);
				codigo.genCod (op, d1, d2, aux.dir);
				codigo.genCod ("if", ret.dir, vddr, "goto");
				codigo.genCod ("goto", fls);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion MAYOR en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}
			return ret;
		}
		else{
			return her;
		}
	}

	Dir_tipo exp()throws IOException{
		Dir_tipo aux = new Dir_tipo();
		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			
			Dir_tipo aux2 = term();
			aux = expP(aux2);
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 16");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
		return aux;
	}

	Dir_tipo expP(Dir_tipo her)throws IOException{
		Dir_tipo aux = new Dir_tipo();
		
		if(t_actual.equals(MAS)){
			int linea = t_actual.linea;
			eat(MAS);
			Dir_tipo auxTerm = term();
			if (Semantica.equivalentes(her.tipo, auxTerm.tipo)){
				String op = "+";
				Dir_tipo herP = new Dir_tipo();
				herP.tipo = Semantica.maximo(her.tipo, auxTerm.tipo);
				herP.dir = Semantica.nuevaTemp();
			
				String d1 = Semantica.ampliar(her.dir, her.tipo, herP.tipo, codigo, linea);
				String d2 = Semantica.ampliar(auxTerm.dir, auxTerm.tipo, herP.tipo, codigo, linea);
				codigo.genCod (op, d1, d2, herP.dir);

				aux = expP(herP);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion MAS en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}

			return aux;

		}
		else if(t_actual.equals(MENOS)){
			int linea = t_actual.linea;
			eat(MENOS);
			Dir_tipo auxTerm = term();
			if (Semantica.equivalentes(her.tipo, auxTerm.tipo)){
				String op = "-";
				Dir_tipo herP = new Dir_tipo();
				herP.tipo = Semantica.maximo(her.tipo, auxTerm.tipo);
				herP.dir = Semantica.nuevaTemp();
			
				String d1 = Semantica.ampliar(her.dir, her.tipo, herP.tipo, codigo, linea);
				String d2 = Semantica.ampliar(auxTerm.dir, auxTerm.tipo, herP.tipo, codigo, linea);
				codigo.genCod (op, d1, d2, herP.dir);

				aux = expP(herP);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion MENOS en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}
			
			return aux;
		}
		else{
			return her;
		}
	}

	Dir_tipo term()throws IOException{
		Dir_tipo aux = new Dir_tipo();
		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			Dir_tipo aux2 = unario();
			aux = termP(aux2);	
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 17");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
		return aux;
	}

	Dir_tipo termP(Dir_tipo aux)throws IOException{
		Dir_tipo aux2 = new Dir_tipo();
		Dir_tipo ret = new Dir_tipo();
		
		if(t_actual.equals(MUL)){
			int linea = t_actual.linea;
			eat(MUL);
			Dir_tipo auxUnario = unario();
			if (Semantica.equivalentes(aux.tipo, auxUnario.tipo)){
				String op = "*";
				
				aux2.tipo = Semantica.maximo(aux.tipo, auxUnario.tipo);
				aux2.dir = Semantica.nuevaTemp();
			
				String d1 = Semantica.ampliar(aux.dir, aux.tipo, aux2.tipo, codigo, linea);
				String d2 = Semantica.ampliar(auxUnario.dir, auxUnario.tipo, aux2.tipo, codigo, linea);
				codigo.genCod (op, d1, d2, aux2.dir);

				ret = termP(aux2);

			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion (*) en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}
			
			return ret;
		} 
		else if(t_actual.equals(ENTRE) ){
			int linea = t_actual.linea;
			eat(ENTRE);
			Dir_tipo auxUnario = unario();
			if (Semantica.equivalentes(aux.tipo, auxUnario.tipo)){
				String op = "*";
				
				aux2.tipo = Semantica.maximo(aux.tipo, auxUnario.tipo);
				aux2.dir = Semantica.nuevaTemp();
			
				String d1 = Semantica.ampliar(aux.dir, aux.tipo, aux2.tipo, codigo, linea);
				String d2 = Semantica.ampliar(auxUnario.dir, auxUnario.tipo, aux2.tipo, codigo, linea);
				codigo.genCod (op, d1, d2, aux2.dir);

				ret = termP(aux2);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion (/) en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}		
			return ret;
		}
		else if(t_actual.equals(MOD)){
			int linea = t_actual.linea;
			eat(MOD);
			Dir_tipo auxUnario = unario();
			String op = "%";
			if (aux.tipo==0 && auxUnario.tipo==0){
				aux2.tipo = 0;
				aux2.dir = Semantica.nuevaTemp();
				ret = termP(aux2);
				//ret = termP(auxUnario);
				codigo.genCod (op, auxUnario.dir, aux.dir, aux2.dir);
			}
			else{
				error("Error semantico: los tipos son incompatibles. Expresion (%) en linea: "+linea);
				errores += 1;
				error("Errores: " + errores);
				System.exit(1);
			}	
			return ret;
		}
		else{ 
			return aux;
		}
	}

	Dir_tipo unario()throws IOException{
		Dir_tipo aux = new Dir_tipo();

		if(t_actual.equals(DIF)){
			eat(DIF);
			String op = "!";
			aux.dir = Semantica.nuevaTemp();
			Dir_tipo aux2 = unario();
			aux.tipo = aux2.tipo;
			codigo.genCod (op, "", aux2.dir, aux.dir);
		}
		else if(t_actual.equals(MENOS)){
			eat(MENOS);
			String op = "-";
			aux.dir = Semantica.nuevaTemp();
			Dir_tipo aux2 = unario();
			aux.tipo = aux2.tipo;
			codigo.genCod (op, "", aux2.dir, aux.dir);
		}
		else if(t_actual.equals(L_PAR) || t_actual.equals(ID) || t_actual.equals(NUM) 
			|| t_actual.equals(CADENA) || t_actual.equals(BOOL_LIT) ){
			aux = factor();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 18");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
		return aux;
	}

	Dir_tipo factor()throws IOException{
		Dir_tipo aux = new Dir_tipo();

		if(t_actual.equals(L_PAR)){ //factor.tipo = bool.tipo    factor.dir = bool.dir
			eat(L_PAR);
			Dir_tipo aux2 = bool(Semantica.nuevaEtq(),Semantica.nuevaEtq());
			aux.tipo = aux2.tipo;
			aux.dir = aux2.dir;
			eat(R_PAR);
			return aux;
		}
		else if(t_actual.equals(NUM)){
			if(t_actual.type == 0){
				aux.dir = t_actual.valor;
				aux.tipo = t_actual.type;
			} //entero
			else if(t_actual.type == 1){
				aux.dir = t_actual.valor;
				aux.tipo = t_actual.type;
			} //decimal
			eat(NUM);
			return aux;
		}
		else if(t_actual.equals(BOOL_LIT)){
			if(t_actual.valor.equals("true")){
				aux.dir = t_actual.valor;
				aux.tipo = 0;
			}
			else if(t_actual.valor.equals("false")){
				aux.dir = t_actual.valor;
				aux.tipo = 0;
			}
			eat(BOOL_LIT);
			return aux;
		}
		else if(t_actual.equals(ID)){
			String idBase = t_actual.valor;
			eat(ID);
			aux = factorP(idBase, t_actual.linea);
		
			return aux;
		}
		else if (t_actual.equals(CADENA)){
			if (t_actual.type == 0){ //caracter
				aux.dir = t_actual.valor;
				aux.tipo = 2;
			}
			else if (t_actual.type == 1){ //cadena
				tablaCadena.add(t_actual.valor);
				aux.dir = t_actual.valor;
				aux.tipo = 5;
			}			
			eat(CADENA);
			return aux;
		}
		
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 19 ");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return aux;
		}
	}

	Dir_tipo factorP(String idBase, int linea) throws IOException{

		Dir_tipo aux = new Dir_tipo();

		if(t_actual.equals(L_COR)){
			Dir_tipo aux2;
			aux2 = localizacion(idBase, linea);
			aux.dir = Semantica.nuevaTemp();
			aux.tipo = aux2.tipo; //localizacion.tipo
			codigo.genCod("",idBase,"["+aux2.dir+"]",aux.dir);
		}
		else if (t_actual.equals(L_PAR)){
			eat(L_PAR);
			ArrayList<Integer> listaParam = parametros();
			eat(R_PAR);

			if (tsGlobal.buscar(idBase)){
				if(tsGlobal.buscarVar(idBase).equals("func")){
					
					if (equivalenciasArgs(idBase, listaParam)){
					aux.dir = Semantica.nuevaTemp();
					aux.tipo = tsGlobal.buscarTipo(idBase);
					codigo.genCod(idBase+", ","call",Integer.toString(listaParam.size()),aux.dir);
					}
					else {
						error("Error semantico: el num o tipo de parametros no coincide. Funcion " + idBase + " linea: " + linea);
						errores +=1;
						error("Errores: " + errores);
						System.exit(1);
					}
					
				}
				else{
					error("Error semantico: el ID no es una funcion: " + idBase + " linea: " + linea);
					errores +=1;
					error("Errores: " + errores);
					System.exit(1);
				}
			}
			else{
				error("Error semantico: el ID no esta declarado: " + idBase + " linea: " + linea);
				errores +=1;
				error("Errores: " + errores);
				System.exit(1);
			}

		}
		else { 

			aux.dir = idBase;
			if (pilaTS.peek().buscar(idBase)){
				aux.tipo = pilaTS.peek().buscarTipo(idBase);
			}
			else{
				aux.tipo = tsGlobal.buscarTipo(idBase);
			}
		}
		
		return aux;
	}

	ArrayList<Integer> parametros()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT)){
			return lista_param();
		}
		else{ 
			return null;
		}
	}

	ArrayList<Integer> lista_param()throws IOException{
		ArrayList<Integer> listaHer = new ArrayList<Integer>();

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT)){
		
			Dir_tipo auxBool = bool(Semantica.nuevaEtq(),Semantica.nuevaEtq());
			codigo.genCod("param", auxBool.dir); //(String operador, String res)
			listaHer.add(auxBool.tipo);
			ArrayList<Integer> lista = lista_paramP(listaHer);
			return lista;
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 21 ");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
			return null;
		}
	}

	ArrayList<Integer> lista_paramP(ArrayList<Integer> listaHer)throws IOException{
		if(t_actual.equals(COMA)){
			eat(COMA);
			Dir_tipo auxBool = bool(Semantica.nuevaEtq(),Semantica.nuevaEtq());
			codigo.genCod("param", auxBool.dir); //(String operador, String res)
			listaHer.add(auxBool.tipo);
			return lista_paramP(listaHer);

		}
		else{ 
			return listaHer;
		}
	}

	Dir_tipo localizacion(String idBase, int linea)throws IOException{
		String base = idBase;
		//error("idBase es "+ idBase);
		Dir_tipo auxP = new Dir_tipo();
		Dir_tipo aux = new Dir_tipo();
			
		if(t_actual.equals(L_COR)){
			eat(L_COR);
			Dir_tipo auxBool = bool(Semantica.nuevaEtq(),Semantica.nuevaEtq());
			eat(R_COR);
			
			if (pilaTS.peek().buscar(base)){
				if(auxBool.tipo == 0){
					int tipoTemp = pilaTS.peek().buscarTipo(base);
					if (pilaTT.peek().getNombreTipo(tipoTemp).equals("array")){
						auxP.tipo = pilaTT.peek().getTipoBase(tipoTemp);
						auxP.dir = Semantica.nuevaTemp();
						int tam = pilaTT.peek().getTam(auxP.tipo);
						codigo.genCod("*",auxBool.dir,Integer.toString(tam),auxP.dir);
						//generar code genCod(localizacion.dir ’=’ bool.dir ’*’ localizacion.tam )
						aux = localizacionP(auxP, base);
					}
					else{
						error("Error semantico: el identificador no es un arreglo: " + base+ ", linea: " + linea);
						errores+=1;
						error("Errores: " + errores);
						System.exit(1);
					}
				}
				else{
					error("Error semantico: el indice del arreglo debe ser un entero. Error en ID: " + base+ ", linea: " + linea);
					errores+=1;
					error("Errores: " + errores);
					System.exit(1);
				}
			}
			else{
				error("Error semantico: el identificador no esta declarado: " + base+ ", linea: " + linea);
				errores+=1;
				error("Errores: " + errores);
				System.exit(1);
			}
			return aux;
		}
		else{ 
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 22");
			errores +=1;
			error("Errores: " + errores);
			System.exit(1);
		}
		return aux;
	}

	Dir_tipo localizacionP(Dir_tipo aux, String base)throws IOException{
		Dir_tipo auxP = new Dir_tipo();
		//evaluar bool.tipo
		if(t_actual.equals(L_COR)){
			eat(L_COR);
			Dir_tipo auxBool = bool(Semantica.nuevaEtq(),Semantica.nuevaEtq());
			eat(R_COR);
			if(auxBool.tipo == 0){
				if (pilaTT.peek().getNombreTipo(aux.tipo).equals("array")){
					auxP.tipo = pilaTT.peek().getTipoBase(aux.tipo);
					auxP.dir = Semantica.nuevaTemp();
					String dirTmp = Semantica.nuevaTemp();
					int tam = pilaTT.peek().getTam(auxP.tipo);
					//generar code
					//genCod(dirTmp’=’bool.dir’*’ localizacionP1.tam 	
					//genCod(localizacionP1.dir ’=’localizacionP.dir’+’dirTmp )
					codigo.genCod("*",auxBool.dir,Integer.toString(tam),dirTmp);
					codigo.genCod("+",aux.dir,dirTmp,auxP.dir);

					//para retornar
					aux = localizacionP(auxP, base);
				}
				else{
					error("Error semantico: el identificador no es un arreglo: " + base + "linea: " + t_actual.linea);
					errores += 1;
					error("Errores: " + errores);
					System.exit(1);
				}
			}
			else {
				error("Error semantico: el indice del arreglo debe ser un entero. Error en ID: " + base+ ", linea: " + t_actual.linea);
				errores+=1;
				error("Errores: " + errores);
				System.exit(1);
			}
			return aux;
		}
		else{ 
			return aux;
		}
	}

	void error(String msg){
		System.out.println(msg);
	}


	boolean equivalenciasArgs(String base, ArrayList <Integer> listaParam){
		ArrayList<Integer> args;
		args = tsGlobal.buscarArgs(base);
		return Semantica.equivalentesListas(args, listaParam);
	}
}

