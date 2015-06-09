package security;

import vulnerability.Types.AndroidPermissiveWVC;
import vulnerability.Types.ApachePermissiveHV;
import vulnerability.Types.JavaxPermissiveHV;
import vulnerability.Types.X509NegligentTM;

public abstract class SafetySquad {

	public boolean dispatch(X509NegligentTM v) { 
		/*T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);*/
		boolean res;
		//eventuale PRE
		res = inspect(v);
		//eventuale POST
		
		return res;
	}
	
	public boolean dispatch(JavaxPermissiveHV v) { 
		/*T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);*/
		boolean res;
		//eventuale PRE
		res = inspect(v);
		//eventuale POST
		
		return res;
	}
	
	public boolean dispatch(ApachePermissiveHV v) { 
		/*T t = pre(s);
		if (t == null)
			t = process(s);
		post(s, t);*/
		boolean res;
		//eventuale PRE
		res = inspect(v);
		//eventuale POST
		
		return res;
	}
	
	public abstract boolean inspect(X509NegligentTM v);
	
	public abstract boolean inspect(JavaxPermissiveHV v);
	
	public abstract boolean inspect(ApachePermissiveHV v);

	public abstract boolean dispatch(AndroidPermissiveWVC v);
}
