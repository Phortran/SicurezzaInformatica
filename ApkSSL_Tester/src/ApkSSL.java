import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ssi.santi_valenti.AnalysisResult;
import ssi.santi_valenti.ApkSslTester;
import vulnerability.Vulnerability;

/**
 * Servlet implementation class ApkSSL
 */
@WebServlet
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 100, maxFileSize = 1024 * 1024 * 500, maxRequestSize = 1024 * 1024 * 500)
public class ApkSSL extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIR = "uploads";
	private double TimeLoad;
	private double TImeAnalysis;
	private double TimeReachability;
	private String manifest = null;
	private String path_pdf = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApkSSL() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// uploadFile(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			OutOfMemoryError {
		// TODO Auto-generated method stub
		

		String simple = request.getParameter("simple");
		String complex = request.getParameter("complex");

		if (simple != null) {
			
			
			String applicationPath = request.getServletContext()
					.getRealPath("");
			System.out.println("request.getServletContext().getRealPath(): "
					+ applicationPath);
			// constructs path of the directory to save uploaded file
			String uploadFilePath = applicationPath + File.separator
					+ UPLOAD_DIR;
		
			// creates the save directory if it does not exists
			File fileSaveDir = new File(uploadFilePath);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
			}
			System.out.println("Upload File Directory= "
					+ fileSaveDir.getAbsolutePath());
			String fileName = null;
			// Get all the parts from request and write it to the file on server

			Part part = request.getPart("sender");
			fileName = getFileName(part);
			System.out.println("fileName: " + fileName);
			part.write(uploadFilePath + File.separator + fileName);

			String path = fileSaveDir.getAbsolutePath() + File.separator
					+ fileName;
			
			List<element> Apk_list = new ArrayList<element>();
			Apk_list = table_apk(path, false);
			GeneratePdf(Apk_list, path, fileName);

			String pdf = path + ".pdf";

			String path_pdf = pdf.substring(pdf.indexOf("\\ApkSSL_Tester\\"));

			for (element i : Apk_list) {
				System.out.println("id: " + i.Apkid + " class: " + i.Apkclass
						+ " method: " + i.ApkMethod + " timeload : "
						+ i.getTimeLoad() + " timeanalysis: "
						+ i.getTimeAnalysis() + " Type Vulnerability: "
						+ i.ApkVulnerability + " timeReachability:"
						+ i.getTimeReachability() + "Result: " + i.result);
				TimeLoad = i.getTimeLoad();
				TImeAnalysis = i.getTimeAnalysis();
				TimeReachability = i.getTimeReachability();

			}

			
			if (Apk_list.isEmpty()) {
				request.setAttribute("manifest", manifest);

				request.getRequestDispatcher("/result2.jsp").forward(request,
						response);
			} else {
				request.setAttribute("manifest", manifest);
				request.setAttribute("table", Apk_list);
				request.setAttribute("timeload", TimeLoad);
				request.setAttribute("timereachability", TimeReachability);
				request.setAttribute("timeanalysis", TImeAnalysis);
				request.setAttribute("pdf", path_pdf);
				request.getRequestDispatcher("/result.jsp").forward(request,
						response);
			}

		} else if (complex != null) {
			System.out.println("complex");

			String applicationPath = request.getServletContext()
					.getRealPath("");
			
			// constructs path of the directory to save uploaded file
			String uploadFilePath = applicationPath + File.separator
					+ UPLOAD_DIR;
			
			// creates the save directory if it does not exists
			File fileSaveDir = new File(uploadFilePath);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
			}
			System.out.println("Upload File Directory= "
					+ fileSaveDir.getAbsolutePath());
			String fileName = null;
			// Get all the parts from request and write it to the file on server

			Part part = request.getPart("sender");
			fileName = getFileName(part);
			
			part.write(uploadFilePath + File.separator + fileName);

			List<element> Apk_list = new ArrayList<element>();
			try {
				Apk_list = table_apk(fileSaveDir.getAbsolutePath()
						+ File.separator + fileName, true);
				GeneratePdf(Apk_list, fileSaveDir.getAbsolutePath()
						+ File.separator + fileName, fileName);
				String temp = fileSaveDir.getAbsolutePath() + File.separator
						+ fileName + ".pdf";
				path_pdf = temp.substring(temp.indexOf("\\ApkSSL_Tester\\"));
				for (element i : Apk_list) {
					System.out.println("id: " + i.Apkid + " class: "
							+ i.Apkclass + " method: " + i.ApkMethod
							+ " timeload : " + i.getTimeLoad()
							+ " timeanalysis: " + i.getTimeAnalysis()
							+ " Type Vulnerability: " + i.ApkVulnerability
							+ " timeReachability:" + i.getTimeReachability()
							+ "Result: " + i.result);
					TimeLoad = i.getTimeLoad();
					TImeAnalysis = i.getTimeAnalysis();
					TimeReachability = i.getTimeReachability();

				}
			} catch (OutOfMemoryError e) {

				request.setAttribute("manifest", manifest);
				request.setAttribute("table", Apk_list);
				request.setAttribute("timeload", TimeLoad);
				request.setAttribute("timereachability", TimeReachability);
				request.setAttribute("pdf", path_pdf);
				request.setAttribute("timeanalysis", TImeAnalysis);
				request.getRequestDispatcher("/result.jsp").forward(request,
						response);

			}
			if (Apk_list.isEmpty()) {
				request.setAttribute("manifest", manifest);

				request.getRequestDispatcher("/result2.jsp").forward(request,
						response);
			} else {
				request.setAttribute("manifest", manifest);
				request.setAttribute("table", Apk_list);
				request.setAttribute("timeload", TimeLoad);
				request.setAttribute("timereachability", TimeReachability);
				request.setAttribute("pdf", path_pdf);
				request.setAttribute("timeanalysis", TImeAnalysis);
				request.getRequestDispatcher("/result.jsp").forward(request,
						response);
			}

		} else if (complex == null && simple == null) {

			request.getRequestDispatcher("/SearchVulnerabilities.jsp").forward(
					request, response);
		}

	}

	private List<element> table_apk(String fileSaveDir, boolean raggiungibilità) {
		List<element> list = new ArrayList<element>();
		int i = 1;

		ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/WEB-INF/lib/android.jar");
		
		ApkSslTester.setAndroidPath(fullPath);

		AnalysisResult result = ApkSslTester.analyse(fileSaveDir,
				raggiungibilità);
		manifest = result.info;
		for (Vulnerability v : result.vulnerabilities) {

			list.add(new element(v.getSuspect().getsClass().getName(), v
					.getSuspect().getsMethod().getName(), i, result.loadTime,
					result.reachTime, result.analysisTime, v.getType(), v
							.getStatus().toString()));

			i++;
		}

		return list;
	}

	private String getFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		System.out.println("content-disposition header= " + contentDisp);
		String[] tokens = contentDisp.split(";");
		for (String token : tokens) {
			if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") + 2,
						token.length() - 1);
			}
		}
		return "";
	}

	public void GeneratePdf(List<element> test, String path, String name) {

		ServletContext context = getServletContext();
		String fullPath = context.getRealPath("/img/android.png");

		Document document = new Document();
		Font redFont = FontFactory.getFont(FontFactory.COURIER, 14, Font.BOLD,
				new CMYKColor(255, 0, 0, 0));
		Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 12,
				new CMYKColor(255, 0, 0, 0));
		try {
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(path + ".pdf"));
			document.open();
			Image logo = Image.getInstance(fullPath);
			logo.scaleAbsolute(60, 60);
			PdfPTable index = new PdfPTable(2);
			float[] column = { 0.5f, 2f };
			index.setWidths(column);
			PdfPCell logo_cell = new PdfPCell(logo, false);
			logo_cell.setPadding(10);
			logo_cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			logo_cell.setBorderColor(BaseColor.WHITE);
			PdfPCell developer = new PdfPCell(new Phrase("ApkSSLTester\n\n"
					+ "Developer: Santi Gabriele & Valenti Alessandro"));
			developer.setBorderColor(BaseColor.WHITE);
			index.addCell(logo_cell);
			index.addCell(developer);

			document.add(index);

			Paragraph title = new Paragraph(name + " vulnerability result.",
					redFont);
			title.setPaddingTop(15f);
			document.add(title);
			Paragraph space = new Paragraph(" ");
			document.add(space);
			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100);
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);

			// Set Column widths
			float[] columnWidths = { 0.5f, 2f, 3f, 3f, 2f };
			table.setWidths(columnWidths);

			PdfPCell cell1 = new PdfPCell(new Paragraph("#", tableFont));
			cell1.setBorderColor(BaseColor.BLUE);
			cell1.setPaddingLeft(10);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cell2 = new PdfPCell(new Paragraph("Vulnerability",
					tableFont));
			cell2.setBorderColor(BaseColor.BLUE);
			cell2.setPaddingLeft(10);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cell3 = new PdfPCell(new Paragraph(
					"Vulnerability Location Class", tableFont));
			cell3.setBorderColor(BaseColor.BLUE);
			cell3.setPaddingLeft(10);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cell4 = new PdfPCell(new Paragraph(
					"Vulnerability Location Method", tableFont));
			cell4.setBorderColor(BaseColor.BLUE);
			cell4.setPaddingLeft(10);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cell5 = new PdfPCell(new Paragraph("Result", tableFont));
			cell5.setBorderColor(BaseColor.BLUE);
			cell5.setPaddingLeft(10);
			cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

			table.addCell(cell1);
			table.addCell(cell2);
			table.addCell(cell3);
			table.addCell(cell4);
			table.addCell(cell5);

			for (element i : test) {

				PdfPCell id = new PdfPCell(new Paragraph("" + i.Apkid));
				id.setBorderColor(BaseColor.BLUE);
				id.setPaddingLeft(10);
				id.setHorizontalAlignment(Element.ALIGN_CENTER);
				id.setVerticalAlignment(Element.ALIGN_MIDDLE);
				PdfPCell vulnerabilità = new PdfPCell(new Paragraph(
						i.ApkVulnerability));
				vulnerabilità.setBorderColor(BaseColor.BLUE);
				vulnerabilità.setPaddingLeft(10);
				vulnerabilità.setHorizontalAlignment(Element.ALIGN_CENTER);
				vulnerabilità.setVerticalAlignment(Element.ALIGN_MIDDLE);
				PdfPCell apkclass = new PdfPCell(new Paragraph(i.Apkclass));
				apkclass.setBorderColor(BaseColor.BLUE);
				apkclass.setPaddingLeft(10);
				apkclass.setHorizontalAlignment(Element.ALIGN_CENTER);
				apkclass.setVerticalAlignment(Element.ALIGN_MIDDLE);
				PdfPCell apkmethod = new PdfPCell(new Paragraph(i.ApkMethod));
				apkmethod.setBorderColor(BaseColor.BLUE);
				apkmethod.setPaddingLeft(10);
				apkmethod.setHorizontalAlignment(Element.ALIGN_CENTER);
				apkmethod.setVerticalAlignment(Element.ALIGN_MIDDLE);
				PdfPCell result = new PdfPCell(new Paragraph(i.result));
				result.setBorderColor(BaseColor.BLUE);
				result.setPaddingLeft(10);
				result.setHorizontalAlignment(Element.ALIGN_CENTER);
				result.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(id);
				table.addCell(vulnerabilità);
				table.addCell(apkclass);
				table.addCell(apkmethod);
				table.addCell(result);

			}
			document.add(table);

			document.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
