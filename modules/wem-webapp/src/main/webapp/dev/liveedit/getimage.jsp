<%
  com.enonic.wem.admin.jsp.LiveEditService service = com.enonic.wem.admin.jsp.LiveEditHelper.getService();
  String key = request.getParameter( "key" );

  service.serveImage( key, response );
%>