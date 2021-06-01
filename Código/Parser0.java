/**
*
*	@author Elsa
*	@author Saul
*/


import java.io.IOException;
import java.util.*;

public class Parser0{

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
	ArrayList<Integer> args;

	public Parser0(Lex lexer)throws IOException{
		this.lexer = lexer;
		t_actual = lexer.yylex();
		pilaTT = new Stack<TablaTipos>();
		pilaTS = new Stack<TablaSimbolos>();
		pila_dir = new Stack <Integer>();
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
		args = new ArrayList<Integer>();
		if(t_actual.equals(FUNC)){
			pilaTS.push(new TablaSimbolos());
			pilaTT.push(new TablaTipos());
			pila_dir.push(dir);
			dir = 0;
			eat(FUNC);
			tipo = tipO();
			if (t_actual.equals(ID)){
				if (!pilaTS.peek().buscar(t_actual.valor)){ 
					id=t_actual.valor;
				}
				else {
					error("Error semantico: Variable definida anteriormente: "+ t_actual.valor + " linea: "+t_actual.linea);
				}
				eat(ID);
				eat(L_PAR);
				argumentos();
				eat(R_PAR);
				bloque();
				System.out.println("****\ntablas de Func: "+id);
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

	void argumentos()throws IOException{
		if(t_actual.equals(INT) || t_actual.equals(FLOAT) || t_actual.equals(CHAR) 
			|| t_actual.equals(DOUBLE) || t_actual.equals(VOID)){
			lista_args();
		}
		else { 
			//return null;
		}
	}

	void lista_args()throws IOException{
		int tipo;
		if(t_actual.equals(INT) || t_actual.equals(FLOAT) || t_actual.equals(CHAR) 
			|| t_actual.equals(DOUBLE) || t_actual.equals(VOID)){
			tipo = tipO();
			args.add(tipo);
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
			lista_argsP();
		}
		else {
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 4");
			errores +=1;
		}
	}

	void lista_argsP()throws IOException{
		int tipo;
		if(t_actual.equals(COMA)){
			eat(COMA);
			tipo = tipO();
			args.add(tipo);
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
			lista_argsP();
		}
		else {	}
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

		if(t_actual.equals(ID)){
			eat(ID);
			parte_izquierdaP();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de sintaxis 23");
			errores+=1;
		}
	}

	void parte_izquierdaP()throws IOException{

		if(t_actual.equals(L_COR)){
			localizacion();
		}
		else{ }
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

	void factor()throws IOException{
		
		if(t_actual.equals(L_PAR)){
			eat(L_PAR);
			bool();
			eat(R_PAR);
		}
		else if(t_actual.equals(NUM)){
			eat(NUM);
		}
		else if(t_actual.equals(BOOL_LIT)){
			eat(BOOL_LIT);
		}
		else if(t_actual.equals(ID)){
			eat(ID);
			factorP();
		}
		
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 19 ");
			errores +=1;
		}
	}

	void factorP() throws IOException{

		if(t_actual.equals(L_COR)){
			localizacion();
		}
		else if (t_actual.equals(L_PAR)){
			eat(L_PAR);
			parametros();
			eat(R_PAR);
		}
		else { }
		//cuando es un id sin elementos.. epsilon de localizacion
			//error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			//error("Error de Sintaxis 20 ");
		//	errores +=1;
	}

	void parametros()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT)){
			lista_param();
		}
		else{ }
	}

	void lista_param()throws IOException{

		if(t_actual.equals(DIF) || t_actual.equals(MENOS) || t_actual.equals(L_PAR) 
		|| t_actual.equals(ID) || t_actual.equals(NUM) || t_actual.equals(CADENA) 
		|| t_actual.equals(BOOL_LIT)){
			bool();
			lista_paramP();
		}
		else{
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 21 ");
			errores +=1;
		}
	}

	void lista_paramP()throws IOException{
		
		if(t_actual.equals(COMA)){
			eat(COMA);
			bool();
			lista_paramP();
		}
		else{ }
	}

	void localizacion()throws IOException{
		
		if(t_actual.equals(L_COR)){
			eat(L_COR);
			bool();
			eat(R_COR);
			localizacionP();
		}
		else{ 
			error("Error: "+t_actual.valor+" linea: "+t_actual.linea );
			error("Error de Sintaxis 22");
			errores +=1;
		}
	}

	void localizacionP()throws IOException{
		
		if(t_actual.equals(L_COR)){
			eat(L_COR);
			bool();
			eat(R_COR);
			localizacionP();
		}
		else{ }
	}

	void error(String msg){
		System.out.println(msg);
	}


}

