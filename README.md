# ML-CDS

The following steps must be performed to get this running:

Create/Select the ML database & modules database you want the servlet to connect to.

Create an XDBC server with basic authentication

Create a new dateTime range index on the database you selected:

--namespace uri:  http://marklogic.com/mlcs/cds

--localname: SystemTimeStamp

Create a WebServlet project in Eclipse

Add DashboardServlet.java to the Java source directory

Add marklogic-xcc-8.0.5.jar to the build path

Add web.xml to the generated "WEB-INF" directory

Configure web.xml to connect to the XDBC server you created

Create sample data and sample status documents using the CDS Qconsole workspace

Run the DashboardServlet in Tomcat.  Default view will be displayed (see samples creenshot) 
