package utils;

import soot.SootClass;
import soot.SootMethod;

/**
 * FIXME prima bozza dell'oggetto suspect; potrebbe comportarsi più
 * come un Bundle, se le diverse vulnerabilità richiedono tipi di 
 * argomento troppo eterogenei fra loro.
 * 
 * @author Gabriele Santi, Alessandro Valenti
 *
 */
public class Suspect {
	private SootClass sClass;
	private SootMethod sMethod;
	
	public Suspect (SootClass sClass, SootMethod sMethod) {
		this.setsClass(sClass);
		this.setsMethod(sMethod);
	}

	public SootClass getsClass() {
		return sClass;
	}

	public void setsClass(SootClass sClass) {
		this.sClass = sClass;
	}

	public SootMethod getsMethod() {
		return sMethod;
	}

	public void setsMethod(SootMethod sMethod) {
		this.sMethod = sMethod;
	}
	
}
