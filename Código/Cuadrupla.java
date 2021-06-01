/**
*	Fecha de modificación : 12 feb 2021
*	@author Elsa
*/

public class Cuadrupla{
	public String operador;
	public String arg1;
	public String arg2;
	public String res;

	public Cuadrupla (String operador, String arg1, String arg2, String res){
		this.operador = operador;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.res = res;
	}

	@Override
	public String toString(){
		if(this.operador.equals("label")){
			return this.res+": " + "\n";
		}
		else if (this.operador.equals("if")){
			return "\t"+this.operador+" "+this.arg1+" "+this.res+" "+this.arg2+"\n";
		}
		else if (this.arg2 == null){
			return "\t"+this.res+this.operador+this.arg1+ "\n";

		}
		else if (this.operador.equals("goto") || this.operador.equals("print") ||  this.operador.equals("param") || 
			this.operador.equals("scan") || this.operador.equals("return") || this.res.equals("")){
			return "\t"+this.operador+" " + this.res + "\n";
		}
		else{
			return "\t"+this.res+"="+this.arg1+this.operador+this.arg2+ "\n";
		}
	}
}