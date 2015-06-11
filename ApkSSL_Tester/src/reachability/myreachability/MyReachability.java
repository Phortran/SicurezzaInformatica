package reachability.myreachability;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeStmt;

public class MyReachability  extends AbstractStmtSwitch {
	private MethodFlagger methodFlagger;
	public Queue<SootMethod> toVisitMethods;
	private boolean analysisDone;
	private static MyReachability singleton = null;

	private MyReachability() {
		toVisitMethods = new LinkedList<SootMethod>();
		methodFlagger = new MethodFlagger(this);
		analysisDone = false;
	}

	public static MyReachability getInstance() {
		if (singleton == null) {
			singleton = new MyReachability();
		}

		return singleton;
	}

	public void ReachabilityAnalysis(List<SootMethod> initMethods) {
		if (this.analysisDone)
			return;
		else {

			toVisitMethods.addAll(initMethods);

			while (!toVisitMethods.isEmpty()) {
				SootMethod mIter = toVisitMethods.poll();

				PatchingChain<Unit> mChain = mIter.retrieveActiveBody().getUnits();

				for (Unit u : mChain) {
					u.apply(this);
				}
			}
		}
	}
	
	

	@Override
	public void caseAssignStmt(AssignStmt stmt) {
		handleAssign(stmt);
	}

	@Override
	public void caseIdentityStmt(IdentityStmt stmt) {
		handleAssign(stmt);
	}

	private void handleAssign(DefinitionStmt stmt) {
		//Value rhs = stmt.getRightOp();
		methodFlagger.translateCall(stmt.getRightOpBox());
	}

	@Override
	public void caseInvokeStmt(InvokeStmt stmt) {
		ValueBox valueBox = stmt.getInvokeExprBox();
		methodFlagger.translateCall(valueBox);
	}


}
