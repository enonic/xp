<%@page import="com.enonic.wem.web.jsp.JspDataTools" %>
<%
  final JspDataTools tools = JspDataTools.create( getServletConfig().getServletContext() );
  tools.reindexData();
  response.sendRedirect( "../index.jsp" );
%>
