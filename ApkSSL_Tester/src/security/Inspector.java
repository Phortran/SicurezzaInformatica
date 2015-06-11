package security;

//import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import reachability.myreachability.MyReachability;
//import soot.SootMethod;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import utils.Clues;
import utils.PatternRecognizer;
import utils.Suspect;
import vulnerability.Types.AndroidPermissiveWVC;
import vulnerability.Types.ApachePermissiveHV;
import vulnerability.Types.JavaxPermissiveHV;
import vulnerability.Types.X509NegligentTM;

public final class Inspector extends SafetySquad {
	private static final Logger log = LoggerFactory.getLogger(Inspector.class);
	private boolean sospetto = INFONDATO;
	private static final boolean FONDATO = true;
	private static final boolean INFONDATO = false;

	@Override
	public boolean inspect(X509NegligentTM v) {
		Suspect s = v.getSuspect();
		log.info("Inspector handling negligent X509 Trust Manager suspect");
		
		UnitGraph graph = new ExceptionalUnitGraph(s.getsMethod().retrieveActiveBody());
		if (!PatternRecognizer.anyExitThrowsException(graph, Clues.JAVA_SECURITY_CERTIFICATE_EXCEPTION)) {
			//TODO fare qualcosa, tipo costruire un oggetto VulnerabilityResult,
			//aggiungere informazioni utili... per ora ritorno solo true
			sospetto = FONDATO;
		}
		
		return sospetto;
	}

	@Override
	public boolean inspect(JavaxPermissiveHV v) {
		Suspect s = v.getSuspect();
		log.info("Inspector handling Javax.Net Permissive Hostname Verifier suspect");
		
		UnitGraph graph = new BriefUnitGraph(s.getsMethod().retrieveActiveBody());
		if (PatternRecognizer.allExitsReturnTrue(graph)) {
			//TODO fare qualcosa, tipo costruire un oggetto VulnerabilityResult,
			//aggiungere informazioni utili... per ora ritorno solo true
			sospetto = FONDATO;
		}
		return sospetto;
	}
	
	@Override
	public boolean inspect(ApachePermissiveHV v) {
		Suspect s = v.getSuspect();
		log.info("Inspector handling Apache Permissive Hostname Verifier suspect");
		
		UnitGraph graph = new ExceptionalUnitGraph(s.getsMethod().retrieveActiveBody());
		if (!PatternRecognizer.anyExitThrowsException(graph, Clues.SSL_EXCEPTION)) {
			//TODO fare qualcosa, tipo costruire un oggetto VulnerabilityResult,
			//aggiungere informazioni utili... per ora ritorno solo true
			sospetto = FONDATO;
		}
		
		return sospetto;
	}

	@Override
	public boolean dispatch(AndroidPermissiveWVC v) {
		Suspect s = v.getSuspect();
		log.info("Inspector handling Android Permissive WebView Client suspect");
		sospetto = FONDATO;
		
		//FIXME dbg
		//ArrayList<SootMethod> ls = new ArrayList<SootMethod>();
		//ls.add(s.getsMethod());
		//MyReachability.getInstance().ReachabilityAnalysis(ls);
		
		for (Unit unit : s.getsMethod().retrieveActiveBody().getUnits()) {
			unit.apply(new AbstractStmtSwitch() {
				

				@Override
				public void caseInvokeStmt(InvokeStmt stmt) {
					if (stmt.getInvokeExpr().getMethod().getName().equalsIgnoreCase(Clues.ANDROID_SSL_ERROR_CANCEL.toString())) 
						sospetto = INFONDATO;
				}
				
			});
			
		}

		return sospetto;
	}
	
}
