/**
*
*	@author Elsa
*	@author Saul
*/


import java.io.IOException;
import java.util.*;

public class Parser1{

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
	Stack <Integer> pila_dir;
	ArrayList<Integer> listaRetorno;
	ArrayList<String> tablaCadena;

	public Parser1(Lex lexer)throws IOException{
		this.lexer = lexer;
		t_actual = lexer.yylex();
		pilaTT = new Stack<TablaTipos>();
		pilaTS = new Stack<TablaSimbolos>();
		pila_dir = new Stack <Integer>();
		tablaCadena = new ArrayList<String>();
	}	

	void parse()throws IOException{
		programa(); //Símbolo inicial
		System.out.println("\n\tTablas globales");
		pilaTS.peek().printTS();  //Para tabla de Símbolos g
		pilaTT.peek().printTT();  //Para tabla de Tipos g
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
		pilaTS.push(new TablaSimbolos());
		pilaTT.push(new TablaTipos());
		if(t_actual.equals(INT) || t_actual.equals(FLOAT) || t_actual.equals(CHAR) || t_actual.equals(DOUBLE) || t_actual.equals(VOID)){
			
			declaraciones();
			funciones();
		}
		else{
			error("Error sintactico: tipo invalido: " + t_actual.valor +" linea: "+t_actual.linea );
			errores +=1;
			declaraciones();
			error("Error de Sintaxis 0");
			errores +=1;
		}	
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
			}
			eat(ID);
			lista_varP(l_tipo);
		}
		else{
			error("Error sintactico: se esperaba un identificador: " + t_actual.valor+" linea: "+t_actual.linea);
			errores +=1;
			t_actual =lexer.yylex(); //seguir analisis
			lista_varP(l_tipo);
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
				}
			eat(ID);
			lista_varP(lp_tipo);
			}
			else{
				error("Error sintactico: se esperaba un identificador: " + t_actual.valor+ " linea: "+t_actual.linea);
				errores +=1;
				t_actual =lexer.yylex(); //seguir analisis
				lista_varP(lp_tipo);
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
			pilaTS.push(new TablaSimbolos());
			pilaTT.push(new TablaTipos());
			pila_dir.push(dir);
			dir = 0;
			eat(FUNC);
			tipo = tipO();
			if (t_actual.equals(ID)){
				//agregar condicion retorno
				if (!pilaTS.peek().buscar(t_actual.valor)){ 
					id=t_actual.valor;
				}
				else {
					error("Error semantico: Variable definida anteriormente: "+ t_actual.valor + " linea: "+t_actual.linea);
					errores+=1;
				}
				eat(ID);
				eat(L_PAR);
				args = argumentos();
				eat(R_PAR);
				bloque();
				/*
				if(semantica.equivalentesLista(listaRetorno, tipo)){	}
				else {
					error("Error semantico: Los tipos de retorno no coinciden con el tipo de funcion"+ t_actual.valor + " linea: "+t_actual.linea);
					errores+=1;
				}*/
				System.out.println("\n****\nTablas de Func: "+id);
				pilaTS.peek().printTS();  //Para tabla de Símbolos
				pilaTT.peek().printTT();  //Para tabla de Tipos
				System.out.println("****");
				pilaTS.pop();
				pilaTT.pop();
				dir = pila_dir.pop();
				pilaTS.peek().insertar(id,0, tipo, "func", args); //agregar args
				funciones();

			}
			else{
				error("Error sintactico: se esperaba un identificador: " + t_actual.valor + " linea: "+t_actual.linea);
				errores +=1;
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
		} 
	}

	void instrucciones()throws IOException{
		if (t_actual.equals(ID)|| t_actual.equals(IF) || t_actual.equals(WHILE) 
			|| t_actual.equals(DO) || t_actual.equals(BREAK) || t_actual.equals(L_LLAVE) 
			|| t_actual.equals(RETURN) || t_actual.equals(SWITCH)){
			sentencia();
			instruccionesP();	
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 6");
			errores +=1;
		}
	}

	void instruccionesP()throws IOException{
		if (t_actual.equals(ID)|| t_actual.equals(IF) || t_actual.equals(WHILE) 
			|| t_actual.equals(DO) || t_actual.equals(BREAK) || t_actual.equals(L_LLAVE) 
			|| t_actual.equals(RETURN) || t_actual.equals(SWITCH)){
			
			sentencia();
			instruccionesP();	
		}
		else{ }
	}

	void sentencia()throws IOException{
		
		if (t_actual.equals(ID)){
			parte_izquierda();
			eat(IGUAL);
			bool();
			eat(PYC);		
		}
		else if(t_actual.equals(IF)){
			eat(IF);
			eat(L_PAR);
			bool();
			eat(R_PAR);
			sentencia();
			Sent();
		}
		else if(t_actual.equals(WHILE)){
			eat(WHILE);
			eat(L_PAR);
			bool();
			eat(R_PAR);
			sentencia();
		}
		else if(t_actual.equals(DO)){
			eat(DO);
			sentencia();
			eat(WHILE);
			eat(L_PAR);
			bool();
			eat(R_PAR);
		}
		else if(t_actual.equals(BREAK)){
			eat(BREAK);
			eat(PYC);	
		}
		else if(t_actual.equals(L_LLAVE)){
			bloque();
		}
		else if (t_actual.equals(RETURN)){
			eat(RETURN);
			Func_ret();
		}
		else if(t_actual.equals(SWITCH)){
			eat(SWITCH);
			eat(L_PAR);
			bool();
			eat(R_PAR);
			eat(L_LLAVE);
			casos();
			eat(R_LLAVE);
		}
		else if(t_actual.equals(PRINT)){
			eat(PRINT);
			exp();
			eat(PYC);
		}
		else if(t_actual.equals(SCAN)){
			eat(SCAN);
			parte_izquierda();
		}
		else {	
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 7");
			errores +=1;
		}
	}

	void parte_izquierda()throws IOException{
		Dir_tipo aux = new Dir_tipo();
		if(t_actual.equals(ID)){
			if (pilaTS.peek().buscar(t_actual.valor)){ 
				String idBase = t_actual.valor;
				eat(ID);
				aux = parte_izquierdaP(idBase, t_actual.linea);
				//return aux;
			}
			else {
				error("Error semantico: Variable no declarada: "+ t_actual.valor + " linea: "+t_actual.linea);
				errores+=1;
				String idBase = t_actual.valor;
				eat(ID); //continuar analisis
				aux = parte_izquierdaP(idBase, t_actual.linea);
			}
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 23");
			errores+=1;
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
			}
		}
		return aux;
	}

	void Sent()throws IOException{

		if(t_actual.equals(ELSE)){
			eat(ELSE);
			sentencia();
		}
		else {	}
	}

	void Func_ret()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
			|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
			|| t_actual.equals(BOOL_LIT)){
			exp();
			eat(PYC);
		}
		else if(t_actual.equals(PYC)){
			eat(PYC);
		}
		else {	
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 8");
			errores +=1;
		}
	}

	void casos()throws IOException{
		
		if (t_actual.equals(CASE)){
			caso();
			casos();
		}
		else if (t_actual.equals(DEFAULT)){
			predeterminado();			
		}
		else {	}
	}

	void caso()throws IOException{
		
		if(t_actual.equals(CASE)){
			eat(CASE);
			eat(NUM);
			eat(DOS_PUNTOS);
			instrucciones();
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 9");
			errores +=1;
		}
	}

	void predeterminado()throws IOException{
		
		if(t_actual.equals(DEFAULT)){
			eat(DEFAULT);
			eat(DOS_PUNTOS);
			instrucciones();
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 10 ");
			errores +=1;
		}
	}

	void bool()throws IOException{
		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT)  ){	
			comb();
			boolP();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 11");
			errores +=1;
		}
	}

	void boolP() throws IOException{
		if(t_actual.equals(OR)){
			eat(OR);
			comb();
			boolP();
		}
		else {	}
	}

	void comb()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			igualdad();
			combP();
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 12 ");
			errores +=1;
		}
	}

	void combP()throws IOException{

		if(t_actual.equals(AND)){
			eat(AND);
			igualdad();
			combP();
		}
		else{ }
	}

	void igualdad()throws IOException{
		
		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			rel();
			igualdadP();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 13");
			errores +=1;
		}
	}

	void igualdadP()throws IOException{
		
		if(t_actual.equals(IGUAL)){
			eat(IGUAL);
			eat(IGUAL);
			rel();
			igualdadP();
		}
		else if(t_actual.equals(DIF_IG)){
			eat(DIF_IG);
			rel();
			igualdadP();
		}
		else{ }
	}

	void rel()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			exp();
			relP();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 14 ");
			errores +=1;
		}
	}

	void relP()throws IOException{

		if(t_actual.equals(MENOR_O_IG)){
			eat(MENOR_O_IG);
			exp();
		}
		else if(t_actual.equals(MAYOR_O_IG)){
			eat(MAYOR_O_IG);
			exp();
		}
		else if(t_actual.equals(MENOR)){
			eat(MENOR);
			exp();
		}
		else if(t_actual.equals(MAYOR)){
			eat(MAYOR);
			exp();
		}
		else{ }
	}

	void exp()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			term();
			expP();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 16");
			errores +=1;
		}
	}

	void expP()throws IOException{

		if(t_actual.equals(MAS)){
			eat(MAS);
			term();
			expP();
		}
		else if(t_actual.equals(MENOS)){
			eat(MENOS);
			term();
			expP();
		}
		else{ }
	}

	void term()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT) ){
			unario();
			termP();	
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 17");
			errores +=1;
		}
	}

	void termP()throws IOException{

		if(t_actual.equals(MUL)){
			eat(MUL);
			unario();
			termP();
		} 
		else if(t_actual.equals(ENTRE) ){
			eat(ENTRE);
			unario();
			termP();
		}
		else if(t_actual.equals(MOD)){
			eat(MOD);
			unario();
			termP();
		}
		else{ }
	}

	void unario()throws IOException{
		
		if(t_actual.equals(DIF)){
			eat(DIF);
			unario();
		}
		else if(t_actual.equals(MENOS)){
			eat(MENOS);
			unario();
		}
		else if(t_actual.equals(L_PAR) || t_actual.equals(ID) || t_actual.equals(NUM) 
			|| t_actual.equals(CADENA) || t_actual.equals(BOOL_LIT) ){
			factor();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 18");
			errores +=1;
		}
	}

	Dir_tipo factor()throws IOException{
		Dir_tipo aux = new Dir_tipo();

		if(t_actual.equals(L_PAR)){ //factor.tipo = bool.tipo    factor.dir = bool.dir
			eat(L_PAR);
			bool();
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
			if (pilaTS.peek().buscar(t_actual.valor)){ 
				String idBase = t_actual.valor;
				eat(ID);
				aux = factorP(idBase, t_actual.linea);
				return aux;
			}
			else { 
				error("Error semantico: Variable no declarada: "+ t_actual.valor + " linea: "+t_actual.linea);
				String idBase = t_actual.valor; //tratar error para evitar bucle
				eat(ID);
				aux = factorP(idBase, t_actual.linea);
				errores+=1;
			}
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
			return aux;
		}
	}

	Dir_tipo factorP(String idBase, int linea) throws IOException{

		Dir_tipo aux = new Dir_tipo();
	//	error("idBase es "+ idBase);

		if(t_actual.equals(L_COR)){
			Dir_tipo aux2;
			aux2 = localizacion(idBase, linea);
			aux.dir = Semantica.nuevaTemp();
			aux.tipo = aux2.tipo; //localizacion.tipo
		}
		else if (t_actual.equals(L_PAR)){
			eat(L_PAR);
			ArrayList<Integer> listaParam = parametros();
			eat(R_PAR);

			if (pilaFondoBuscar(pilaTS, idBase)){
				if(pilaFondoObtenerVar(pilaTS, idBase).equals("func")){
					/*
					if (equivalenciasArgs(pilaTS, idBase, listaParam)){
						/*
					factorP.tipo = PilaTS.top().getTipo(id)
           		   	factorP.dir = nuevaTemporal()
            		genCod(factor.dir ’=’ ’call’ id ’,’ parametros.lista.tam )
					
					}
					else {
						error("Error semantico: el num o tipo de parametros no coincide. Funcion " + idBase + " linea: " + linea);
					}
					*/
				}
				else{
					error("Error semantico: el ID no es una funcion: " + idBase + " linea: " + linea);
					errores +=1;
				}
			}
			else{
				error("Error semantico: el ID no esta declarado: " + idBase + " linea: " + linea);
				errores +=1;
			}

		}
		else { 
			aux.dir = idBase;
			aux.tipo = pilaTS.peek().buscarTipo(idBase);
		}
		/*factorP.dir =factorP.base
		factorP.tipo=PilaTS.top().getTipo(factorP.dir)
*/
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
		int tipo = -1;

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT)){
			//tipo = bool();
			bool();
			listaHer.add(tipo);
			ArrayList<Integer> lista = lista_paramP(listaHer);
			return lista;
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 21 ");
			errores +=1;
			return null;
		}
	}

	ArrayList<Integer> lista_paramP(ArrayList<Integer> listaHer)throws IOException{
		int tipo = -1;
		if(t_actual.equals(COMA)){
			eat(COMA);
			//tipo = bool();
			bool();
			listaHer.add(tipo);
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
			bool(); //obtener tipo y dir
			eat(R_COR);
			
			if (pilaTS.peek().buscar(base)){
				int tipoTemp = pilaTS.peek().buscarTipo(base);
				if (pilaTT.peek().getNombreTipo(tipoTemp).equals("array")){
					auxP.tipo = pilaTT.peek().getTipoBase(tipoTemp);
					auxP.dir = Semantica.nuevaTemp();
					int tam = pilaTT.peek().getTam(auxP.tipo);
					//generar code genCod(localizacion.dir ’=’ bool.dir ’*’ localizacion.tam )
					aux = localizacionP(auxP, base);
				}
				else{
					error("Error semantico: el identificador no es un arreglo: " + base+ " linea: " + linea);
					errores+=1;
				}
			}
			else{
				error("Error semantico: el identificador no esta declarado: " + base+ " linea: " + linea);
				errores+=1;
			}
			return aux;
		}
		else{ 
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 22");
			errores +=1;
		}
		return aux;
	}

	Dir_tipo localizacionP(Dir_tipo aux, String base)throws IOException{
		Dir_tipo auxP = new Dir_tipo();
		//evaluar bool.tipo
		if(t_actual.equals(L_COR)){
			eat(L_COR);
			bool();
			eat(R_COR);

			if (pilaTT.peek().getNombreTipo(aux.tipo).equals("array")){
				auxP.tipo = pilaTT.peek().getTipoBase(aux.tipo);
				auxP.dir = Semantica.nuevaTemp();
				int tam = pilaTT.peek().getTam(auxP.tipo);
				//generar code
				//genCod(dirTmp’=’bool.dir’*’ localizacionP1.tam 	genCod(localizacionP1.dir ’=’localizacionP.dir’+’dirTmp )
				aux = localizacionP(auxP, base);
			}
			else{
				error("Error semantico: el identificador no es un arreglo: " + base + "linea: " + t_actual.linea);
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

	boolean pilaFondoBuscar(Stack<TablaSimbolos> pilaTS, String base){
		Stack<TablaSimbolos> pilaCopia = pilaTS;
		TablaSimbolos pilaAux = new TablaSimbolos();
		while (!pilaCopia.empty()){
			pilaAux = pilaCopia.pop();
		}
		return pilaAux.buscar(base);
	}

	String pilaFondoObtenerVar(Stack<TablaSimbolos> pilaTS, String base){
		Stack<TablaSimbolos> pilaCopia = pilaTS;
		TablaSimbolos pilaAux = new TablaSimbolos();
		while (!pilaCopia.empty()){
			pilaAux = pilaCopia.pop();
		}
		
		return pilaAux.buscarVar(base);
	}
	boolean equivalenciasArgs(Stack<TablaSimbolos> pilaTS, String base, ArrayList <Integer> listaParam){
		Stack<TablaSimbolos> pilaCopia = pilaTS;
		TablaSimbolos pilaAux = new TablaSimbolos();
		ArrayList<Integer> args;
		int tipoFunc;
		while (!pilaCopia.empty()){
			pilaAux = pilaCopia.pop();
		}
		args = pilaAux.buscarArgs(base);
		tipoFunc = pilaAux.buscarTipo(base);
		return Semantica.equivalentesListas(args, listaParam);
	}
}

