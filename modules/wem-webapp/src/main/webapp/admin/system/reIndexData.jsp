<%@page import="com.enonic.wem.web.jsp.JspDataTools" %>
<%
  final JspDataTools tools = JspDataTools.get();
  tools.reindexData();
  response.sendRedirect( "../index.jsp" );
%>
