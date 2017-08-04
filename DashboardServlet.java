package com.marklogic.crossdomain.dashboard;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.ValueFactory;
import com.marklogic.xcc.types.XName;
import com.marklogic.xcc.types.XdmValue;
import com.marklogic.xcc.types.XdmVariable;



/**
 * Servlet implementation class DashboardServlet
 */
@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String xccURL = null;
	private List<String> xccURLs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashboardServlet() {
		super();

		Context env;
		xccURL = null;

		int urlCount = 0;
		try {
			env = (Context) new InitialContext().lookup("java:comp/env");
			urlCount = (Integer) env.lookup("ml.xcc.urlcount");

			xccURLs = new ArrayList<String>();
			for (int i = 1; i <= urlCount; i++) {
				xccURL = (String) env.lookup("ml.xcc.url" + i);
				xccURLs.add(xccURL);
			}
			for (int i = 0; i < xccURLs.size(); i++) {
				System.out.println("xccURL" + i + ": " + xccURLs.get(i));
			}

		} catch (NamingException e1) {
			e1.printStackTrace();
		}
	}

	public void init(ServletConfig config) throws ServletException {
		Context env;
		int urlCount = 0;
		try {
			env = (Context) new InitialContext().lookup("java:comp/env");
			urlCount = (Integer) env.lookup("ml.xcc.urlcount");
			
			System.out.println("urlCount:" + urlCount);

			xccURLs = new ArrayList<String>();
			for (int i = 1; i <= urlCount; i++) {
				xccURL = (String) env.lookup("ml.xcc.url" + i);
				xccURLs.add(xccURL);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OutputStream out = response.getOutputStream();
		
		String statusTimeStamp = request.getParameter("statusTimeStamp");
		if(statusTimeStamp == null) {
			statusTimeStamp = "";
		}
		
		try {
			out.write(getDashboardCounts(statusTimeStamp).getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private String getDashboardCounts(String statusTimeStamp) throws Exception {
		  Map<String, String> params = new HashMap<String, String>();
		  params.put("statusTimeStamp", statusTimeStamp);
		  String MLoutput = callMLModule("/get-dashboard-counts.xqy", params);
		  return MLoutput;
	}
	
	
	private String callMLModule(String moduleURI, Map<String, String> args)
			throws Exception {
		String mlResponse = null;
		ContentSource cs;

		cs = ContentSourceFactory.newContentSource(new URI(xccURL));
		cs.setAuthenticationPreemptive(true);
		Session session = cs.newSession();
		Request request = session.newModuleInvoke(moduleURI);

		for (Entry<String, String> entry : args.entrySet()) {
			String mapKey = entry.getKey();
			String mapValue = entry.getValue();
			XdmValue value = ValueFactory.newXSString(mapValue);
			XName xname = new XName("", mapKey);
			XdmVariable myVar = ValueFactory.newVariable(xname, value);
			request.setVariable(myVar);
		}

		ResultSequence rs = session.submitRequest(request);
		mlResponse = rs.asString();

		return mlResponse;
	}

}
