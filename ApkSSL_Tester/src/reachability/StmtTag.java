package reachability;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.FastHierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;
import soot.toolkits.graph.Block;
import soot.util.NumberedString;

public class StmtTag implements Tag {
	public static String TAG_NAME = "sdf";

	/** Targets of a predicate's true and false branches */
	public static class PredicateTargets {
		Stmt trueTgt;
		Stmt falseTgt;
		public PredicateTargets(Stmt trueTgt, Stmt falseTgt) {
			this.trueTgt = trueTgt;
			this.falseTgt = falseTgt;
		}
		public Stmt getFalseTgt() {
			return falseTgt;
		}
		public Stmt getTrueTgt() {
			return trueTgt;
		}
	}

	// Fields
	private ArrayList<Stmt> predecessorStmts;
	private ArrayList<Stmt> successorStmts;
	private boolean inCatchBlock = true; // initial lattice value

	private CallSite callSite = null; // keeps reference to local call site, if any
	private Location loc;
	private final int sIdx; // index in method's list of stmts
	private Block basicBlock = null;

	private BitSet bsLocalReachedStmts = null; // 1 bit per stmt id within containing method
	private HashSet<SootMethod> localReachedAppMtd = null;

	// Methods
	public boolean hasPredecessorStmts() { return !predecessorStmts.isEmpty(); }
	public ArrayList<Stmt> getPredecessorStmts() { return predecessorStmts; }
	public void addPredecessorStmt(Stmt pred) { predecessorStmts.add(pred); }
	public boolean hasSuccessorStmts() { return !successorStmts.isEmpty(); }
	public ArrayList<Stmt> getSuccessorStmts() { return successorStmts; }
	public void addSuccessorStmt(Stmt succ) { successorStmts.add(succ); }

	public Location getLocation() { return loc; }

	public boolean hasCallSite() { return callSite != null; }
	public CallSite getCallSite() { return callSite; }
	public CallSite getAppCallSite() { return (callSite == null)? null : callSite.hasAppCallees()? callSite : null; }
	public boolean hasAppCallees() { return callSite != null && callSite.hasAppCallees(); }
	public Collection<SootMethod> getAppCallees() { return callSite.getAppCallees(); }

	public boolean hasLibCalls() { return callSite != null && callSite.hasLibCallees(); }

	public int getIdxInMethod() { return sIdx; }

	/** TEMPORAL */
	public void setInCatchBlock(boolean inCatchBlock) { this.inCatchBlock = inCatchBlock; }
	/** TEMPORAL */
	public boolean isInCatchBlock() { return inCatchBlock; }

	public Block getBasicBlock() { return basicBlock; }
	public void setBasicBlock(Block basicBlock) { this.basicBlock = basicBlock; }

	public void initLocalReachedStmtsMtds() { bsLocalReachedStmts = new BitSet(); localReachedAppMtd = new HashSet<SootMethod>(); }
	public void addLocalReachedStmt(int sReachableId) { bsLocalReachedStmts.set(sReachableId); }
	public boolean addLocalReachedStmts(BitSet bsReachable) {
		final int prevCardinality = bsLocalReachedStmts.cardinality();
		bsLocalReachedStmts.or(bsReachable);
		return prevCardinality < bsLocalReachedStmts.cardinality();
	}
	public boolean addLocalReachedAppMtd(Collection<SootMethod> mReachable) { return localReachedAppMtd.addAll(mReachable); }
	public boolean isLocalReachabilityComputed() { return bsLocalReachedStmts != null; }
	public BitSet getLocalReachedStmts() { return bsLocalReachedStmts; }
	public HashSet<SootMethod> getLocalReachedAppMtds() { return localReachedAppMtd; }

	public StmtTag(SootMethod m, Stmt s, int sIdx) {
		predecessorStmts = new ArrayList<Stmt>(); // will be filled later
		successorStmts = new ArrayList<Stmt>(); // will be filled later

		//		genDefEvents = new HashSet<ContextualDefOrUse>(); // will be filled later

		loc = new Location(m, s);
		this.sIdx = sIdx;
	}

	/**
	 * @return call site describing call performed in this stmt; null if this stmt is not a call
	 */
	public CallSite initCallSite() {
		// compute initial (local) reaching ctx calls, if this unit is an instance or static call
		Stmt sThis = loc.getStmt();
		if (sThis.containsInvokeExpr()) {
			InvokeExpr invokeExpr = sThis.getInvokeExpr();
			if (invokeExpr instanceof VirtualInvokeExpr || invokeExpr instanceof InterfaceInvokeExpr) {
				// dynamic dispatch; note that SpecialInvokeExpr is handled separately as direct call in else block below
				InstanceInvokeExpr instInvExpr = ((InstanceInvokeExpr) invokeExpr);

				// find all application targets
				Set<SootMethod> mAppTgts = new HashSet<SootMethod>();
				Set<SootMethod> mLibTgts = new HashSet<SootMethod>();
				getConcreteCallTargets(instInvExpr, mAppTgts, mLibTgts);

				if (mAppTgts.isEmpty() && mLibTgts.isEmpty()) {
					// DEBUG
					getConcreteCallTargets(instInvExpr, mAppTgts, mLibTgts);

					// TODO: see why this happens
					System.out.println("WARNING: no app or lib calls found for call site: " + loc);
					//					return null;
				}

				// create call site, storing it in field
				List<SootMethod> sortedAppTgts = new ArrayList<SootMethod>(mAppTgts);
				Collections.sort(sortedAppTgts, new StringBasedComparator<SootMethod>());//MethodComparator());
				List<SootMethod> sortedLibTgts = new ArrayList<SootMethod>(mLibTgts);
				Collections.sort(sortedLibTgts, new StringBasedComparator<SootMethod>());//MethodComparator());
				callSite = new CallSite(loc, sortedAppTgts, sortedLibTgts);
				return callSite;
			}
			else { // static or special-instance (<init>) call
				SootMethod mCallee = invokeExpr.getMethodRef().resolve(); // should return method at declaring class or superclass
				assert mCallee != null;
				MethodTag mCalleeTag = (MethodTag) mCallee.getTag(MethodTag.TAG_NAME);

				// create call site, storing it in field
				List<SootMethod> singleMethodList = new ArrayList<SootMethod>();
				singleMethodList.add(mCallee);
				if (mCalleeTag != null)
					callSite = new CallSite(loc, singleMethodList, new ArrayList<SootMethod>());
				else
					callSite = new CallSite(loc, new ArrayList<SootMethod>(), singleMethodList);

				return callSite;
			}
		}

		return null; // no call in this stmt
	}

	private boolean getConcreteCallTargets(InvokeExpr instInvExpr, /*OUT*/ Set<SootMethod> appTargets, /*OUT*/ Set<SootMethod> libTargets) {
		// get class of method ref; we start searching from this class
		SootMethodRef mref = instInvExpr.getMethodRef();
		SootClass cls = mref.declaringClass(); // starting class
		final NumberedString subsignature = mref.getSubSignature(); // signature to search for

		// CASE 1: object is of declared class type or inherited from some superclass
		//         find first superclass, starting from current cls, that declares method; there HAS to be such a class
		// note that if cls is interface, superclass if java.lang.Object
		// note that we don't check if there is indeed an interface declaring the method; we assume this is the case if no superclass declares it
		while (!cls.declaresMethod(subsignature) && cls.hasSuperclass())
			cls = cls.getSuperclass(); // never an interface
		// now, method might not be in superclass, or might be abstract; in that case, it's not a target
		SootMethod m;
		if (cls.declaresMethod(subsignature)) {
			m = cls.getMethod(subsignature);
			if (!m.isAbstract()) {
				if (cls.hasTag(ClassTag.TAG_NAME))
					appTargets.add(m); // add app method
				else
					libTargets.add(m); // add lib method
			}
		}

		// (only for virtual/interface calls)
		// CASE 2: object's actual type is a subclass; any subclass declaring the method is a possible target
		//         we have to check all superclasses of implementers, because starting cls might be interface
		if (instInvExpr instanceof VirtualInvokeExpr || instInvExpr instanceof InterfaceInvokeExpr) {
			cls = mref.declaringClass(); // start again from declaring class
			List<SootClass> allSubclasses = getAllSubtypes(cls);
			for (SootClass subCls : allSubclasses) {
				m = getMethodInClassOrSuperclass(subCls, subsignature);
				if (m != null && !m.isAbstract()) {
					if (m.getDeclaringClass().hasTag(ClassTag.TAG_NAME))
						appTargets.add(m); // add app method
					else
						libTargets.add(m); // add lib method
				}
			}
		}

		return !libTargets.isEmpty();
	}

	private SootMethod getMethodInClassOrSuperclass(SootClass subCls, NumberedString subsignature) {
		if (subCls.declaresMethod(subsignature))
			return subCls.getMethod(subsignature);
		if (subCls.hasSuperclass())
			return getMethodInClassOrSuperclass(subCls.getSuperclass(), subsignature);
		return null;
	}
	private List<SootClass> getAllSubtypes(SootClass cls) {
		// TODO store (cache) all subclasses in Class Tag
		List<SootClass> subclasses = new ArrayList<SootClass>();
		FastHierarchy hierarchy = Scene.v().getOrMakeFastHierarchy();
		for (Iterator<SootClass> itSubCls = hierarchy.getSubclassesOf(cls).iterator(); itSubCls.hasNext(); ) {
			SootClass subCls = (SootClass) itSubCls.next();
			subclasses.add(subCls);
			subclasses.addAll(getAllSubtypes(subCls));
		}
		for (Iterator<SootClass> itSubCls = hierarchy.getAllImplementersOfInterface(cls).iterator(); itSubCls.hasNext(); ) {
			SootClass subCls = (SootClass) itSubCls.next();
			subclasses.add(subCls);
			subclasses.addAll(getAllSubtypes(subCls));
		}
		return subclasses;
	}
	// Tag interface implementation
	public String getName() { return TAG_NAME; }
	public byte[] getValue() throws AttributeValueException { return null; }
}
