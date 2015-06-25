package test;

import ssi.santi_valenti.AnalysisResult;
import ssi.santi_valenti.ApkSslTester;
import vulnerability.Vulnerability;

public class TestProgetto {
	private static final boolean deepAnalysis = false;
	private static final boolean secondRound = true;

	public static void main(String[] args) {

		ApkSslTester.setAndroidPath(args[0]);

		AnalysisResult result = ApkSslTester.analyse(args[2], deepAnalysis);

		String resultString = result.info + "\n";

		resultString += "tc: " + result.loadTime;

		resultString += deepAnalysis ? "\ntr: " + result.reachTime : "" ;

		resultString += "\nta: " + result.analysisTime;

		for (Vulnerability v : result.vulnerabilities) {
			resultString += "\n" + v;
		}

		System.out.println(resultString);


		if (secondRound) {
			result = ApkSslTester.analyse(args[2], deepAnalysis);

			resultString = result.info + "\n";

			resultString += "tc: " + result.loadTime;

			resultString += deepAnalysis ? "\ntr: " + result.reachTime : "" ;

			resultString += "\nta: " + result.analysisTime;

			for (Vulnerability v : result.vulnerabilities) {
				resultString += "\n" + v;
			}

			System.out.println(resultString);
		}
	}

}
