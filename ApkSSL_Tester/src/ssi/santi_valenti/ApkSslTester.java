package ssi.santi_valenti;

//import java.util.ArrayList;
import java.util.Arrays;
//import java.util.List;

import manifestParser.AndroidManifestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import reachability.myreachability.MyReachability;
import security.Detective;
import soot.Scene;
//import soot.SootClass;
//import soot.SootMethod;
import soot.options.Options;

public class ApkSslTester {
	private static final Logger log = LoggerFactory.getLogger(ApkSslTester.class);
	//FIXME dbg
	//private static final boolean flag = false;

	public static AnalysisResult analyse(String path) {

		//		Pack wjpp = PackManager.v().getPack("wjpp");
		//		wjpp.add(new Transform("wjpp.activity_entry_transformer", new MethodsFlagger()));

		//FIXME
		//assert (args.length != 0);
		AnalysisResult ar = new AnalysisResult();

		AndroidManifestParser amp = new AndroidManifestParser(path);

		if (amp.requiresInternetPermission()) {



			Options sootOptions = Options.v();
			Scene sootScene = Scene.v();

			sootOptions.set_src_prec(Options.src_prec_apk);
			sootOptions.set_process_dir(Arrays.asList(path));
			sootOptions.set_output_format(Options.output_format_jimple);
			sootOptions.set_force_android_jar("/home/gabriel/Android/Sdk/platforms/android-22/android.jar");
			//FIXME vediamo se funziona con o senza phantom references
			sootOptions.set_allow_phantom_refs(true);
			sootOptions.set_whole_program(true);
			sootOptions.set_unfriendly_mode(true);

			long loadTime = System.currentTimeMillis(),
					endLoadTime,
					//reachTime,
					//endReachTime,
					analysisTime,
					endAnalysisTime;
			

			sootScene.loadNecessaryClasses();
			
			endLoadTime = System.currentTimeMillis();

			/*if (flag) {
				reachTime = System.currentTimeMillis();
				log.info("Performing reachability analysis...");

				List<SootMethod> sMethods = new ArrayList<SootMethod>();
				//TODO gestire caso in cui il nome della classe è relativo (inizia col '.')
				sMethods.addAll(sootScene.getSootClass(amp.getMainActivityClass())
						.getMethods());
				MyReachability.getInstance().ReachabilityAnalysis(sMethods);
				endReachTime = System.currentTimeMillis();
			}*/

			log.info("Performing vulnerability detection...");

			analysisTime = System.currentTimeMillis();

			Detective detective = new Detective();


			detective.detect();

			endAnalysisTime = System.currentTimeMillis();

			ar.vulnerabilities = detective.report();
			//System.out.println(detective.report().toString());


			ar.loadTime = ((endLoadTime - loadTime) / 1000.0);
			ar.analysisTime = ((endAnalysisTime - analysisTime) / 1000.0);
			
			/*String timeResult = "Time for:\nloading classes............. "
					+ (endLoadTime - loadTime) / 1000.0
					+ " seconds,";
			if (flag)
				timeResult += "\nreachability analysis....... "
						+ (endReachTime - reachTime) / 1000.0
						+ " seconds,";
			timeResult += "\nvulnerability analysis...... "
					+ (endAnalysisTime - analysisTime) / 1000.0
					+ " seconds.";
			
			log.info(timeResult);*/


		} else {
			ar.info = "The applications doesn't require permission"
					+ "to connect to the internet, hence is NOT vulnerable.";
			//System.out.println("The applications doesn't require permission"
					//+ "to connect to the internet, hence is NOT vulnerable.");
		}
		
		return ar;

	}

}