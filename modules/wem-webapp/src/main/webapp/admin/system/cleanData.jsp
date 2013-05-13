<%@page import="com.enonic.wem.web.jsp.JspDataTools" %>
<%
  final JspDataTools tools = JspDataTools.create( getServletConfig().getServletContext() );
  tools.cleanData();
  response.sendRedirect( "../index.jsp" );
%>
