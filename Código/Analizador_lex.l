/** Analizador lexico
*	FECHA DE MODIFICACION: 13 enero 2021
*
*	PROGRAMADO POR:
*		@author	Garcia Reyes Saul Michel
*		@author	Santillan Godinez Alan Alejandro
*		@author	Suxo Pacheco Elsa Guadalupe
*/

//Bibliotecas
import java.lang.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


%%

%line //Variable para contar linea, la usamos para cuando haya algun error y ubicarlo
%column
%unicode
%type Token 
%class Lex //Modificamos el nombre de clase Yylex a Lex


%{
	public Token nextToken(String lex, int num, int linea){
		return new Token(num, lex, linea );
	}

	public Token nextToken(String lex, int num, int type, int linea){
	 	return new Token(num, lex, type, linea);
	}
%}


%eofval{
	return nextToken("", 0, yyline+1);
%eofval}


//Expresiones regulares que conforman al analizador lexico

digito=[0-9]
entero={digito}+(\_{digito}+)*                      //expresion final 
decimal={entero}*"."{entero}|{entero}"."{entero}*
expo=[Ee][+-]?{entero}
dec=({decimal}|{entero}){expo}?                         //expresion final
letra=[a-zA-Z]
letrag={letra}|"_"
id={letrag}({letrag}|{digito})*							//expresion final
imag= ({entero}|{dec})i 								//expresion final
cadena=(\"(\\\"|[^\"])*\")					    		//expresion final
caracter=('([^\'])')									//expresion final
bool_lit = (true|false)									//expresion final
espacio=[ \t\n\r]
esps={espacio}+
comentariosBloque=<\*((\*)*[^\*\>]|>)*(\*)+>
comentariosSimple=--[^\n]*(\n)?
%%

{esps}     {    }
{comentariosBloque} {		}
{comentariosSimple} {		}
"="    { return nextToken("=",  1001, yyline+1);}
"!="   { return nextToken("!=",  1002, yyline+1);}
"<="   { return nextToken("<=",  1004, yyline+1);}
"<"    { return nextToken("<",  1003, yyline+1);}
">="   { return nextToken(">=",  1006, yyline+1);}
">"    { return nextToken(">",  1005, yyline+1);}
"+"    { return nextToken("+",  1007, yyline+1);}
"-"    { return nextToken("-",  1008, yyline+1);}
"!"    { return nextToken("!",  1009, yyline+1);}
"*"    { return nextToken("*",  1010, yyline+1);}
"&&"   { return nextToken("&&",  1011, yyline+1);}
"/"    { return nextToken("/",  1012, yyline+1);}
"%"    { return nextToken("%",  1013, yyline+1);}
"||"    { return nextToken("||",  1014, yyline+1);} 	//Fin operadores
"int"   { return nextToken("int",  1015, yyline+1);}
"float" { return nextToken("float",  1016, yyline+1);}
"char"  { return nextToken("char",  1017, yyline+1);}
"double" { return nextToken("double",  1018, yyline+1);}
"void"   { return nextToken("void",  1019, yyline+1);}
"func"   { return nextToken("func",  1020, yyline+1);}
"if"   	 { return nextToken("if",  1021, yyline+1);}
"else"   { return nextToken("else",  1022, yyline+1);}
"switch" { return nextToken("switch",  1023, yyline+1);}
"case"   	{ return nextToken("case",  1024, yyline+1);}
"default"   { return nextToken("default",  1025, yyline+1);}
"do"		{ return nextToken("do",  1026, yyline+1);}
"break"   	{ return nextToken("break",  1027, yyline+1);}
"while"		{ return nextToken("while",  1028, yyline+1);}
"return"	{ return nextToken("return",  1029, yyline+1);}
"print"		{return nextToken("print",  1030, yyline+1);}
"scan"		{return nextToken("scan",  1031, yyline+1);}

 //Fin palabras reservadas

{entero} 	{ return nextToken(yytext(), 1032, 0, yyline+1);}
{dec}   	{ return nextToken(yytext(),  1032, 1, yyline+1);}
{imag}   	{ return nextToken(yytext(),  1032, yyline+1);}
{caracter}    { return nextToken(yytext(),  1033, 0, yyline+1);}
{cadena}    { return nextToken(yytext(),  1033, 1, yyline+1);}
{bool_lit}  { return nextToken(yytext(),  1034, yyline+1);}
{id}		{ return nextToken(yytext(),  1035, yyline+1);}
"("			{ return nextToken("(",  1036, yyline+1);}
")"			{ return nextToken(")",  1037, yyline+1);}
"["			{ return nextToken("[",  1038, yyline+1);}
"]"			{ return nextToken("]",  1039, yyline+1);}
"{"			{ return nextToken("{",  1040, yyline+1);}
"}"			{ return nextToken("}",  1041, yyline+1);}
";"			{ return nextToken(";",  1042, yyline+1);}
":"			{ return nextToken(":",  1043, yyline+1);}
","			{  return nextToken(",",  1044, yyline+1);}


. {System.out.println("Ha ocurrido un error lexico: "+ yytext() + " linea: " + (yyline+1));}
