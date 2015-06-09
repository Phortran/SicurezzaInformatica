package analysis;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.ValueBox;
import utils.Clues;
import vulnerability.Vulnerability;
import vulnerability.Types.JavaxPermissiveHV;
import vulnerability.Types.X509NegligentTM;

public class SecurityAnalyser {
	private static final boolean DEBUG = true;
	private static final Logger log = LoggerFactory.getLogger("Security Analyser");

	//private VulnerabilityFinder vf;
	private Map/*<ValueBox,String>*/ sourcefile_map;
	private Map/*<ValueBox,String>*/ class_map;
	private Map/*<ValueBox,String>*/ method_map;
	private Map/*<ValueBox,Integer>*/ line_map;

	// Make sure we get line numbers
//	static {
//		log.info("Soot Scene configuration");
//		List<String> setting = new ArrayList<String>();
//		setting.add("/home/gabriel/EclipseUnifiedWorkspace/Soot_Survivors_Guide/bin/");
//		soot.options.Options.v().set_process_dir(setting);
//		soot.Scene.v().loadBasicClasses();
//		soot.options.Options.v().parse(new String[] { "-keep-line-number" });
//	}

	/** Performs a foonalysis on the current application classes.
	 * @throws IOException 
	 */
	/*public SecurityAnalyser() {
		vf = new VulnerabilityFinder() {
			
			@Override
			protected void check() {
				// TODO Auto-generated method stub
				
			}
		};
	}*/
	
	public void /*FIXME farle ritornare qualcosa*/ analyse() {
		//debug("Translating classes to intermediate form...");
		//List<SootClass> sClasses = vf.translateApplicationClasses();

		//log.info(sClasses.size() + " methods of interest found.");

//		for (SootClass sClass : sClasses) {
//			log.info("Method: "+ sClass.getName()+":");
//			//Collection<AbstractStatement> stmts = sMethod.getEntry().getSuccs();
//			//printStmts(stmts);
//			log.info("------------------------");
//		}
		
		log.info("Collecting classes...");
		Collection<SootClass> baseClasses = Scene.v().getApplicationClasses();
		
		for(SootClass sClass: baseClasses) {
			
			/*if (sClass.implementsInterface(Clues.X509_TRUST_MANAGER.getClassName())) {
				//classes.add(sClass);
				log.info(sClass.getName());
			}*/
			
			/** TODO qui si fa una serie di if con tutti i casi che si possono desumere dalla
			 * struttura delle classi (il controllo si potrebbe deferire all'oggetto relativo
			 * alla vulnerabilità per permettere la facilità di estensione);
			 *  Dentro l'if, avendo trovato un possibile pattern di vulnerabilità, si inizializza
			 *  l'oggetto relativo a quella vulnerabilità e gli si passa tutto ciò di cui ha
			 *  bisogno per sincerarsi che sia o meno una vulnerabilità.
			 *  Si potrebbe anche usare un solo oggetto dispatcher che distingue l'argomento
			 *   (che potrebbe essere facilmente un VulnerabilityType)
			 *   
			 *   Per ora aggiungere tutti i tipi di errori che estendono IntraproceduralAnalysis
			 *   che non richiedono preparazione preventiva
			 */
			
		}

		debug("Analysis done");
	}

//	private void printStmts(Collection<Statement> stmts) {
//		for (Statement stmt: stmts) {
//			log.info("stmt: "+" "+stmt);
//			printStmts(stmt.getSuccs());
//		}		
//	}

	/** Returns the name of the source file containing the given expression.
	 *  @param box the expression.
	 *  @return the source file name.
	 */
	public final String getSourceFile(ValueBox box) {
		return (String)sourcefile_map.get(box);
	}

	/** Returns the name of the class containing the given expression.
	 *  @param box the expression.
	 *  @return the fully qualified class name.
	 */
	public final String getClassName(ValueBox box) {
		return (String)class_map.get(box);
	}

	/** Returns the name of the method containing the given expression.
	 *  @param box the expression.
	 *  @return the method name.
	 */
	public final String getMethodName(ValueBox box) {
		return (String)method_map.get(box);
	}

	/** Returns the source line number of the given expression.
	 *  @param box the expression.
	 *  @return the line number.
	 */
	public final int getLineNumber(ValueBox box) {
		return ((Integer)line_map.get(box)).intValue();
	}

	/** Loads the named class into the Soot scene,
	 *  marks it as an application class, and generates bodies
	 *  for all of its concrete methods.
	 *  @param name the fully qualified name of the class to be loaded.
	 */
	public static void loadClass(String name) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		Iterator<SootMethod> mi = c.getMethods().iterator();
		while (mi.hasNext()) {
			SootMethod sm = (SootMethod)mi.next();
			if (sm.isConcrete()) {
				sm.retrieveActiveBody();
			}
		}
	}

	private void debug(String s) {
		if (DEBUG) {
			log.info(s);
		}
	}
}
