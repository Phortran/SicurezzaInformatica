package ssi.santi_valenti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import manifestParser.AndroidManifestParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






//FIXME switchare tra i due
import dua.Forensics;
//import reachability.Forensics;

import security.Detective;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.options.Options;

public class ApkSslTester {
	private static final Logger log = LoggerFactory.getLogger(ApkSslTester.class);
	private static String androidJarPath = null;


	public static void setAndroidPath(String androidJarPath) {
		ApkSslTester.androidJarPath = androidJarPath;
	}

	public static AnalysisResult analyse(String path, boolean deepAnalysis) {

		AnalysisResult ar = new AnalysisResult();
		String mainClass;

		if (androidJarPath == null) {
			ar.info = "No Android JAR path specified!";

			return ar;
		}

		AndroidManifestParser amp = new AndroidManifestParser(path);

		if (amp.requiresInternetPermission()) {
			mainClass = amp.getMainActivityClass();

			long loadTime = System.currentTimeMillis(),
					endLoadTime,
					reachTime = 0,
					endReachTime = 0,
					analysisTime,
					endAnalysisTime;


			sootConfig(path, mainClass);
			//Scene.v().loadNecessaryClasses();

			endLoadTime = System.currentTimeMillis();

			log.info("Performing vulnerability detection...");

			analysisTime = System.currentTimeMillis();

			Detective detective = new Detective();
			detective.detect();

			endAnalysisTime = System.currentTimeMillis();

			//FIXME dbg
			log.info("Main activity: " + mainClass);

			ar.vulnerabilities = detective.report();

			if (ar.vulnerabilities.size() != 0) {
				if (deepAnalysis) {
					reachTime = System.currentTimeMillis();
					log.info("Performing reachability analysis...");

					Forensics.setEntryPoint(mainClass);

					soot.Main.main(new String[0]);

					detective.testReachability();
					endReachTime = System.currentTimeMillis();
				}
			} else {
				log.info("There are no suspect so far, so skipping reachability analysis...");
			}



			ar.loadTime = ((endLoadTime - loadTime) / 1000.0);
			ar.analysisTime = ((endAnalysisTime - analysisTime) / 1000.0);
			if (deepAnalysis)
				ar.reachTime = ((endReachTime - reachTime) / 1000.0);


		} else {
			ar.info = "The applications doesn't require permission"
					+ " to connect to the internet, hence is NOT vulnerable.";
		}

		return ar;

	}

	private static void sootConfig(String path, String className) {

		Options sootOptions = Options.v();

		sootOptions.set_src_prec(Options.src_prec_apk);
		sootOptions.set_process_dir(Arrays.asList(path));
		sootOptions.set_output_format(Options.output_format_jimple);
		sootOptions.set_force_android_jar(androidJarPath);



		//FIXME vediamo se funziona con o senza phantom references
		sootOptions.set_allow_phantom_refs(true);
		sootOptions.set_whole_program(true);
		sootOptions.set_unfriendly_mode(true);

		Pack wjpp = PackManager.v().getPack("wjtp");
		wjpp.add(new Transform("wjtp.mt", new Forensics()));

		Scene.v().loadBasicClasses();
		Scene.v().loadNecessaryClasses();
		SootClass c = findMainClass(className);
		//SootClass c = Scene.v().forceResolve(className, SootClass.BODIES);
		//FIXME dbg
		log.info(c.toString());
		c.setApplicationClass();
		Scene.v().loadNecessaryClasses();
		Scene.v().loadBasicClasses();
		//SootMethod m = c.getMethods().get(0);
		//FIXME dbg
		//log.info(m.toString());
		sootOptions.set_main_class(className);
		List<SootMethod> entryPoints = new ArrayList<SootMethod>();
		if (c.declaresMethod("void onCreate(android.os.Bundle)"))
			entryPoints.add(c.getMethod("void onCreate(android.os.Bundle)"));
		Scene.v().setEntryPoints(entryPoints);

	}

	private static SootClass findMainClass(String className) {
		if (className.startsWith(".") || !className.contains(".")) {
			for (SootClass c : Scene.v().getApplicationClasses())
				if (c.getName().endsWith(className)) {
					log.info("Main Class: " + c);
					return c;
				}
		} else {
			return Scene.v().forceResolve(className, SootClass.BODIES);
		}
		return null;
	}

}