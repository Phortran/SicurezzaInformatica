package ssi.santi_valenti;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import security.Detective;
import soot.Scene;
import soot.options.Options;

public class ApkSslTester {
	private static final Logger log = LoggerFactory.getLogger(ApkSslTester.class);

	public static void main(String[] args) {

//		Pack wjpp = PackManager.v().getPack("wjpp");
//		wjpp.add(new Transform("wjpp.activity_entry_transformer", new MethodsFlagger()));
		
		//FIXME
		assert (args.length != 0);
		
		
		Options sootOptions = Options.v();
		Scene sootScene = Scene.v();
		
		sootOptions.set_src_prec(Options.src_prec_apk);
		sootOptions.set_process_dir(Arrays.asList(args));
		sootOptions.set_output_format(Options.output_format_jimple);
		sootOptions.set_force_android_jar("/home/gabriel/Android/Sdk/platforms/android-22/android.jar");
		//FIXME vediamo se funziona con o senza phantom references
		sootOptions.set_allow_phantom_refs(true);
		sootOptions.set_whole_program(true);
		sootOptions.set_unfriendly_mode(true);
		
		long endTime, startTime = System.currentTimeMillis();
		
		sootScene.loadNecessaryClasses();
		
		//SecurityAnalyser sa = new SecurityAnalyser();
		//sa.analyse();
		
		Detective detective = new Detective();
		
		
		detective.detect();
		
		endTime = System.currentTimeMillis();
		
		System.out.println(detective.report().toString());
		
		
		log.info("Time for loading classes and analysis: "
				+ (endTime - startTime) / 1000.0
				+" seconds.");
		
	}

}
