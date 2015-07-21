
public class element {
	public String Apkclass;
	public String ApkMethod;
	public int Apkid;
	public String ApkVulnerability;
	public double TimeLoad;
	public double TimeReachability;
	public double TimeAnalysis;
	public String result;
	
	
	public element(String Apkclass, String ApkMethod, int Apkid,double TimeLoad, double TimeReachability, double TimeAnalysis,String ApkVulnerability,String result) {
		this.Apkclass = Apkclass;
		this.ApkMethod = ApkMethod;
		this.Apkid = Apkid;
		this.TimeLoad=TimeLoad;
		this.TimeReachability=TimeReachability;
		this.TimeAnalysis=TimeAnalysis;
		this.ApkVulnerability=ApkVulnerability;
		this.result=result;
	}
	public String getclass(){
		return Apkclass;
	}
	public String getmethod(){
		return ApkMethod;
	}
	public Integer getid(){
		return Apkid;
	}
	public String getApkVulnerabilty(){
		return ApkVulnerability;
	}
	 public double getTimeLoad(){
		 return TimeLoad;
	 }
	 
	 public double getTimeReachability(){
		 return TimeReachability;
	 }
	 
	 public double getTimeAnalysis(){
		 return TimeAnalysis;
	 }
	 
	 public String getResult(){
		 return result;
	 }
	 
	 public String getColorResult(){
		 String color = "red";
		 if(result.compareTo("VULNERABLE")==0){
			 color="#FF0000";
		 } else  if(result.compareTo("NOT_VULNERABLE")==0){
			 color="#18bc9c";
		 } else  if(result.compareTo("PROBABLY_VULNERABLE")==0){
			 color="#E6B800";
		 } else  if(result.compareTo("POTENTIAL")==0){
			 color="#E6B800";
		 } 
		 
		 
		 return "<span style=\"color:"+color+";\">"+getResult()+"</span>";
	 }
	 
	 

}