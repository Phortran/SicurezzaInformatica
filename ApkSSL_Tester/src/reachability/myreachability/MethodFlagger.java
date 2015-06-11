package reachability.myreachability;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Hierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import utils.PatternRecognizer;

public class MethodFlagger extends AbstractJimpleValueSwitch {
	private static final Logger log = LoggerFactory.getLogger(MethodFlagger.class);
	private MyReachability reachabilityAnalyser;
	private Hierarchy sHierarcy;

	public MethodFlagger(MyReachability reachabilityAnalyser) {
		this.reachabilityAnalyser = reachabilityAnalyser;
		sHierarcy = new Hierarchy();
	}

	public void translateCall(ValueBox vBox) {
		Value val = vBox.getValue();
		val.apply(this);
	}
	
	
	private void caseInstanceInvokeExpr(InstanceInvokeExpr expr) {
		List<SootMethod> targets = getTargetsOf(expr);
		if (targets.isEmpty()) {
		    // This is a call to an interface method or abstract method
		    // with no available implementations.
		    // You could use instruction target as target.
			return;
		}
		for (SootMethod target: targets)
			handleCall(expr, target);
	}

	@Override
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
		caseInstanceInvokeExpr(v);
	}

	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr expr) {
		handleCall(expr, expr.getMethod());
	}

	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr expr) {
		handleCall(expr, expr.getMethod());
	}

	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
		caseInstanceInvokeExpr(v);
	}

	@Override
	public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
		//TODO
		log.info("Wutta fuck is dat??? :: " + v.getMethod());
	}

	private void handleCall(InvokeExpr expr, SootMethod target) {
		SootMethod sMethod = expr.getMethod();
		SootClass sClass = sMethod.getDeclaringClass();
		ReachabilityTag tag = (ReachabilityTag) sMethod.getTag(ReachabilityTag.NAME);
		
		
		if (//PatternRecognizer.isAndroidOrJavaClass(sClass) ||
				sClass.isAbstract() ||
				sClass.isInterface())
				//sClass.isPhantomClass()) FIXME si deve fare??
			log.info("Special Invoke: " + sMethod + " non gestita");
		else if (tag == null) {
			sMethod.addTag(new ReachabilityTag());
			//FIXME
			log.info("classe " + sMethod + " taggata");
			reachabilityAnalyser.toVisitMethods.add(sMethod);
		}
	}
	
	public List<SootMethod> getTargetsOf(InvokeExpr expr) {
		if (expr instanceof InstanceInvokeExpr) {
			return getTargetsOf(((InstanceInvokeExpr)expr).getBase(), expr.getMethod());
		}
		ArrayList<SootMethod> targets = new ArrayList<SootMethod>(1);
		targets.add(expr.getMethod());
		return targets;
	}
	
	public List<SootMethod> getTargetsOf(Value v, SootMethod m) {
		SootClass rc;
		Type t = v.getType();
		if (t instanceof ArrayType) {
			rc = Scene.v().getSootClass("java.lang.Object");
		} else {
			rc = ((RefType)v.getType()).getSootClass();
		}
		@SuppressWarnings("unchecked")
		List<SootMethod> targets = sHierarcy.resolveAbstractDispatch(rc, m);
		return targets;
	}

}
