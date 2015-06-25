package ssi.santi_valenti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import manifestParser.AndroidManifestParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dua.Forensics;
import security.Detective;
import soot.G;
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
	private static boolean analysisSucceded, configured = false;


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


			sootConfig(path, mainClass, deepAnalysis);

			endLoadTime = System.currentTimeMillis();

			log.info("Performing vulnerability detection...");

			analysisTime = System.currentTimeMillis();

			Detective detective = new Detective();
			detective.detect();

			endAnalysisTime = System.currentTimeMillis();

			log.info("Main activity: " + mainClass);

			ar.vulnerabilities = detective.report();

			if (ar.vulnerabilities.size() != 0) {
				if (deepAnalysis) {
					reachTime = System.currentTimeMillis();
					log.info("Performing reachability analysis...");

					Forensics.setEntryPoint(mainClass);

					analysisSucceded = true;
					try {
						soot.Main.main(new String[0]);
					} catch (OutOfMemoryError oome) {
						ar.info = "The analyser has run out of memory during"
								+ "the reachability analysis, wich has not been"
								+ "concluded as well; the vulnerability"
								+ "analysis has succeded, though. You could"
								+ "ask the administrator to allocate more memory"
								+ "to the Java VM with the -Xmx switch.";
						analysisSucceded = false;
					}

					if (analysisSucceded)
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

	private static void sootConfig(String path, String className, boolean deepAnalysis) {
		if (configured) {
			G.reset();
			if (deepAnalysis)
				Forensics.reset();
		}
		
		Options sootOptions = Options.v();
		Scene sootScene = Scene.v();

		sootOptions.set_process_dir(Arrays.asList(path));


		log.info("configuring soot...");

		sootOptions.set_src_prec(Options.src_prec_apk);

		sootOptions.set_output_format(Options.output_format_jimple);
		sootOptions.set_force_android_jar(androidJarPath);

		sootOptions.set_allow_phantom_refs(true);
		sootOptions.set_whole_program(true);
		sootOptions.set_unfriendly_mode(true);



		if (deepAnalysis) {
			Pack wjpp = PackManager.v().getPack("wjtp");
			wjpp.add(new Transform("wjtp.mt", new Forensics()));

			sootScene.loadNecessaryClasses();
			List<SootClass> mainClasses = findMainClasses(className);

			for (SootClass c : mainClasses)
				c.setApplicationClass();

			sootScene.loadNecessaryClasses();
			sootOptions.set_main_class(className);
			List<SootMethod> entryPoints = new ArrayList<SootMethod>();

			for (SootClass c : mainClasses)
				if (c.declaresMethod("void onCreate(android.os.Bundle)"))
					entryPoints.add(c.getMethod("void onCreate(android.os.Bundle)"));

			sootScene.setEntryPoints(entryPoints);
		} else {
			sootScene.loadNecessaryClasses();
		}

		configured = true;
	}

	private static ArrayList<SootClass> findMainClasses(String className) {
		ArrayList<SootClass> res = new ArrayList<SootClass>();

		if (className == null) {
			log.info("Panic Mode: no main activity found in manifest!");

			for (SootClass c : Scene.v().getApplicationClasses()) {
				if (c.declaresMethod("void onCreate(android.os.Bundle)")) {
					log.info("Found Class: " + c);
					res.add(c);
				}
			}

			if (res.isEmpty()) {
				log.info("Cannot find entry points for this application, aborting...");
				return null;
			}
			else
				return res;

		} else if (className.startsWith(".") || !className.contains(".")) {

			for (SootClass c : Scene.v().getApplicationClasses())
				if (c.getName().endsWith(className)) {
					log.info("Main Class: " + c);
					res.add(c);
					return res;
				}
		} else {
			res.add(Scene.v().forceResolve(className, SootClass.BODIES));
			return res;
		}
		return null;
	}

}