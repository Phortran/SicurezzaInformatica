package manifestParser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xmlpull.v1.XmlPullParser;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;


public class AndroidManifestParser {
	private static final String DEFAULT_XML = "AndroidManifest.xml";

	private static final String ATTRIBUTE_NAMESPACE = "android";
	private static final String ATTRIBUTE_NAME = "name";
	
	private static final String PERMISSION_ELEMENT = "uses-permission";
	private static final String ANDROID_INTERNET_PERMISSION = "android.permission.INTERNET";

	private static final String APPLICATION_ELEMENT = "application";
	private static final String ACTIVITY_ELEMENT = "activity";
	private static final String ACTIVITY_INTENT_FILTER = "intent-filter";
	private static final String ACTIVITY_INTENT_FILTER_ACTION = "action";
	private static final String ACTIVITY_INTENT_FILTER_CATEGORY = "category";
	private static final String ACTIVITY_MAIN = "android.intent.action.MAIN";
	private static final String ACTIVITY_LAUNCHER = "android.intent.category.LAUNCHER";

	private String apkPath;
	private String XmlManifest;

	public AndroidManifestParser(String apkPath) {
		this.apkPath = apkPath;
		this.XmlManifest = null;
	}

	public boolean requiresInternetPermission () {
		if (XmlManifest == null)
			XmlManifest = getXMLManifest(apkPath);

		SAXBuilder saxBuilder = new SAXBuilder();
		boolean ans = false;
		Attribute iter;

		try {
			Document doc = saxBuilder.build(new StringReader(XmlManifest));
			List<Element> permissionRequests = doc.getRootElement().getChildren(PERMISSION_ELEMENT);
			//List<Element> permissions = permissionsParent.getChildren();

			for (Element request : permissionRequests) {
				iter = request.getAttribute(ATTRIBUTE_NAME,
						request.getNamespace(ATTRIBUTE_NAMESPACE));
				if (iter.getValue().equals(ANDROID_INTERNET_PERMISSION)) {
					ans = true;
					break;
				}
			}


		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ans;
	}

	public String getMainActivityClass() {
		if (XmlManifest == null)
			XmlManifest = getXMLManifest(apkPath);

		SAXBuilder saxBuilder = new SAXBuilder();
		Element intentFilter, intentFilterAction, intentFilterCategory;
		Attribute iterMain, iterLauncher;
		String res = null;
		
		try {
			Document doc = saxBuilder.build(new StringReader(XmlManifest));
			List<Element> activities = doc.getRootElement().getChild(APPLICATION_ELEMENT).getChildren(ACTIVITY_ELEMENT);

			for (Element activity : activities) {
				intentFilter = activity.getChild(ACTIVITY_INTENT_FILTER);
				if (intentFilter == null)
					continue;
				
				intentFilterAction = intentFilter.getChild(ACTIVITY_INTENT_FILTER_ACTION);
				if (intentFilterAction == null)
					continue;
				intentFilterCategory = activity.getChild(ACTIVITY_INTENT_FILTER).getChild(ACTIVITY_INTENT_FILTER_CATEGORY);
				
				if (intentFilterCategory == null)
					continue;
				
				iterMain = intentFilterAction.getAttribute(ATTRIBUTE_NAME,
						intentFilterAction.getNamespace(ATTRIBUTE_NAMESPACE));
				iterLauncher = intentFilterCategory.getAttribute(ATTRIBUTE_NAME,
						intentFilterCategory.getNamespace(ATTRIBUTE_NAMESPACE));
				
				if (iterMain.getValue().equals(ACTIVITY_MAIN)
						&& iterLauncher.getValue().equals(ACTIVITY_LAUNCHER)) {
					res = activity.getAttribute(ATTRIBUTE_NAME,
							activity.getNamespace(ATTRIBUTE_NAMESPACE))
							.getValue();
					break;
				}
			}


		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return res;
	}

	private String getXMLManifest(String apkPath) {
		ZipFile file = null;
		StringBuilder xmlSb = new StringBuilder(100);
		try {
			File apkFile = new File(apkPath);
			file = new ZipFile(apkFile, ZipFile.OPEN_READ);
			ZipEntry entry = file.getEntry(DEFAULT_XML);

			AXmlResourceParser parser = new AXmlResourceParser();
			parser.open(file.getInputStream(entry));

			StringBuilder sb=new StringBuilder(10);
			final String indentStep="	";

			int type;
			while ((type=parser.next()) != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
				{
					log(xmlSb,"<?xml version=\"1.0\" encoding=\"utf-8\"?>");
					break;
				}
				case XmlPullParser.START_TAG:
				{
					log(false,xmlSb,"%s<%s%s",sb,
							getNamespacePrefix(parser.getPrefix()),parser.getName());
					sb.append(indentStep);

					int namespaceCountBefore=parser.getNamespaceCount(parser.getDepth()-1);
					int namespaceCount=parser.getNamespaceCount(parser.getDepth());

					for (int i=namespaceCountBefore;i!=namespaceCount;++i) {
						log(xmlSb,"%sxmlns:%s=\"%s\"",
								i==namespaceCountBefore?"  ":sb,
										parser.getNamespacePrefix(i),
										parser.getNamespaceUri(i));
					}

					for (int i=0,size=parser.getAttributeCount();i!=size;++i) {
						log(false,xmlSb, "%s%s%s=\"%s\""," ",
								getNamespacePrefix(parser.getAttributePrefix(i)),
								parser.getAttributeName(i),
								getAttributeValue(parser,i));
					}
					log(xmlSb,">");
					break;
				}
				case XmlPullParser.END_TAG:
				{
					sb.setLength(sb.length() - indentStep.length());
					log(xmlSb,"%s</%s%s>",sb,
							getNamespacePrefix(parser.getPrefix()),
							parser.getName());
					break;
				}
				case XmlPullParser.TEXT:
				{
					log(xmlSb,"%s%s",sb,parser.getText());
					break;
				}
				}
			}
			parser.close();
			file.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return xmlSb.toString();
	}

	private String getNamespacePrefix(String prefix) {
		if (prefix==null || prefix.length()==0) {
			return "";
		}
		return prefix+":";
	}

	private String getAttributeValue(AXmlResourceParser parser,int index) {
		int type = parser.getAttributeValueType(index);
		int data = parser.getAttributeValueData(index);
		if (type == TypedValue.TYPE_STRING) {
			return parser.getAttributeValue(index);
		}
		if (type == TypedValue.TYPE_ATTRIBUTE) {
			return String.format("?%s%08X",getPackage(data),data);
		}
		if (type == TypedValue.TYPE_REFERENCE) {
			return String.format("@%s%08X",getPackage(data),data);
		}
		if (type == TypedValue.TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (type == TypedValue.TYPE_INT_HEX) {
			return String.format("0x%08X",data);
		}
		if (type == TypedValue.TYPE_INT_BOOLEAN) {
			return data != 0 ? "true" : "false";
		}
		if (type == TypedValue.TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data)) +
					DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type == TypedValue.TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))+
					FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
			return String.format("#%08X",data);
		}
		if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>",data,type);
	}

	private String getPackage(int id) {
		if (id >>> 24 == 1) {
			return "android:";
		}
		return "";
	}

	private void log(StringBuilder xmlSb,String format,Object...arguments) {
		log(true,xmlSb, format, arguments);
	}

	private void log(boolean newLine,StringBuilder xmlSb,String format,Object...arguments) {
		xmlSb.append(String.format(format, arguments));
		if(newLine) xmlSb.append("\n");
	}


	public static float complexToFloat(int complex) {
		return (float)(complex & 0xFFFFFF00)*RADIX_MULTS[(complex>>4) & 3];
	}

	private static final float RADIX_MULTS[]={
		0.00390625F,3.051758E-005F,1.192093E-007F,4.656613E-010F
	};
	private static final String DIMENSION_UNITS[]={
		"px","dip","sp","pt","in","mm","",""
	};
	private static final String FRACTION_UNITS[]={
		"%","%p","","","","","",""
	};
}