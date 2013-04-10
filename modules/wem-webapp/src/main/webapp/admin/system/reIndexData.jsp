<%@page import="com.enonic.wem.taglib.JspDataTools" %>
<%
  final JspDataTools tools = JspDataTools.create( getServletConfig().getServletContext() );
  tools.reindexData();
  response.sendRedirect( "../index.jsp" );
%>
