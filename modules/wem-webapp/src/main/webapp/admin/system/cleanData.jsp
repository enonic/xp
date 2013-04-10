<%@page import="com.enonic.wem.taglib.JspDataTools" %>
<%
  final JspDataTools tools = JspDataTools.create( getServletConfig().getServletContext() );
  tools.cleanData();
  response.sendRedirect( "../index.jsp" );
%>
