package security;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.BooleanType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import utils.Clues;
import utils.PatternRecognizer;
import utils.Suspect;
import vulnerability.Vulnerability;
import vulnerability.Types.AndroidPermissiveWVC;
import vulnerability.Types.ApachePermissiveHV;
import vulnerability.Types.JavaxPermissiveHV;
import vulnerability.Types.X509NegligentTM;

public class Detective {
	private static final Logger log = LoggerFactory.getLogger(Detective.class);
	private Report report;

	public class Report extends ArrayList<Vulnerability> {
		private static final long serialVersionUID = 7712156207697784936L;

		@Override
		public String toString() {
			int size = this.size();
			String res = "";

			if (size == 0) {
				res += "No vulnerabilities found!";
			} else {
				String cr = System.lineSeparator();
				res += "Report contains "
						+ size
						+ " result(s):"+ cr;
				for (Vulnerability v : this) {
					res += v.toString() + cr;
				}
			}

			return res;
		}
	}

	public Detective() {
		this.report = new Report();
	}

	public void /*FIXME (magari) farle ritornare qualcosa*/detect() {
		log.info("Collecting classes...");
		Collection<SootClass> baseClasses = Scene.v().getApplicationClasses();
		for (SootClass sClass : baseClasses) {
			SootClass superClass = sClass.getSuperclass();
			RefType superClassType = superClass.getType();

			//FIXME per ora cascata if else, magari switch
			if (superClassType.equals(Clues.APACHE_ABST_HOSTNAME_VERIFIER)) {

				for (SootMethod sMethod : sClass.getMethods()) {
					if (PatternRecognizer.methodSignature(sMethod,
							VoidType.v(), "verify",
							Clues.STRING, Clues.STRING_ARRAY,
							Clues.STRING_ARRAY)) {
						Vulnerability apachePermissiveHV = new ApachePermissiveHV(new Suspect(sClass, sMethod));
						if (apachePermissiveHV.proceedInspection()) {
							this.report.add(apachePermissiveHV);
						}
					}
				}

			} else if (sClass.implementsInterface(Clues.JAVAX_NET_HOSTNAME_VERIFIER.getClassName())) {
				for (SootMethod sMethod : sClass.getMethods()) {
					if (PatternRecognizer.methodSignature(sMethod,
							BooleanType.v(), "verify",
							Clues.STRING, Clues.SSL_SESSION)) {
						Vulnerability javaxPermissiveHV = new JavaxPermissiveHV(new Suspect(sClass, sMethod));
						if (javaxPermissiveHV.proceedInspection()) {
							this.report.add(javaxPermissiveHV);
						}
					}
				}

			} else if (sClass.implementsInterface(Clues.JAVAX_X509_TRUST_MANAGER.getClassName())) {

				for (SootMethod sMethod : sClass.getMethods()) {
					if (PatternRecognizer.methodSignature(sMethod,
							VoidType.v(), "checkServerTrusted",
							Clues.JAVAX_SECURITY_X509_CERTIFICATE_ARRAY, Clues.STRING)) {
						Vulnerability x509negligentTM = new X509NegligentTM(new Suspect(sClass, sMethod));
						if (x509negligentTM.proceedInspection()) {
							this.report.add(x509negligentTM);
						}
					}
				}
			} else if (sClass.getSuperclass().getName().equals(Clues.ANDROID_WEBVIEW_CLIENT.getClassName())) {
				
				for (SootMethod sMethod : sClass.getMethods()) {
					if (PatternRecognizer.methodSignature(sMethod,
							VoidType.v(), "onReceivedSslError",
							Clues.ANDROID_WEBVIEV, Clues.ANDROID_SSL_ERROR_HANDLER, Clues.ANDROID_NET_SSL_ERROR)) {
						Vulnerability androidPermissiveWVC = new AndroidPermissiveWVC(new Suspect(sClass, sMethod));
						if (androidPermissiveWVC.proceedInspection()) {
							this.report.add(androidPermissiveWVC);
						}
					}
				}
			}
		}
	}

	public Report report() {
		return this.report;
	}
}
