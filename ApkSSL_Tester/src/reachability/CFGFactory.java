package reachability;

import soot.SootMethod;

public class CFGFactory {
	/** Just instantiates CFG. Does not perform any special analysis. */
	CFG createCFG(SootMethod m) {
		return new CFG(m);
	}
	/** Required for a CFG after instantiation of all CFGs. */
	void analyze(CFG cfg) {
		cfg.analyze();
	}
}
