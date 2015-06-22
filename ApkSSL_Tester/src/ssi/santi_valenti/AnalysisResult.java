package ssi.santi_valenti;

import java.util.ArrayList;

import vulnerability.Vulnerability;

public class AnalysisResult {
	public Double loadTime;
	public Double analysisTime;
	public Double reachTime;
	public String info;
	public ArrayList<Vulnerability> vulnerabilities;
	
	
	public AnalysisResult() {
		this.loadTime = 0.0;
		this.analysisTime = 0.0;
		this.reachTime = 0.0;
		this.info = "";
		this.vulnerabilities = new ArrayList<Vulnerability>();
	}
}
