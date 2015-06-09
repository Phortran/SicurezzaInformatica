package utils;

import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.IntConstant;
import soot.jimple.ReturnStmt;
import soot.toolkits.exceptions.ThrowableSet;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.graph.UnitGraph;

public final class PatternRecognizer {
	private PatternRecognizer () {}
	
	public static boolean methodSignature(SootMethod sMethod, RefType sClassType, Type returnType, String name, Type... parameterTypes) {
		if (!sMethod.getDeclaringClass().getType().equals(sClassType))
			return false;

		return methodSignature(sMethod, returnType, name, parameterTypes);
	}

	public static boolean methodSignature(SootMethod sMethod, Type returnType, String name, Type... parameterTypes) {
		if (!sMethod.getName().equals(name))
			return false;

		return methodSignature(sMethod, returnType, parameterTypes);
	}

	public static boolean methodSignature(SootMethod sMethod, Type returnType, Type... parameterTypes) {
		if (!sMethod.getReturnType().equals(returnType))
			return false;

		if (sMethod.getParameterCount() != parameterTypes.length)
			return false;

		for (int i = 0; i < parameterTypes.length; i++) {
			if (!sMethod.getParameterType(i).equals(parameterTypes[i]))
				return false;
		}

		return true;
	}
	
	public static boolean anyExitThrowsException(UnitGraph graph, RefType exceptionType) {
		for (Unit unit : graph.getTails()) {
			ThrowableSet set = UnitThrowAnalysis.v().mightThrow(unit);
			if (set.catchableAs(exceptionType)) {
				return true;
			}
		}

		return false;
	}

	public static boolean allExitsReturnTrue(UnitGraph graph) {
		for (Unit unit : graph.getTails()) {
			if (unit instanceof ReturnStmt) {
				ReturnStmt stmt = (ReturnStmt) unit;
				if (!stmt.getOp().equals(IntConstant.v(1))) {
					return false;
				}
			}
		}

		return true;
	}
}
