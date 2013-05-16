<%@page import="com.enonic.wem.web.jsp.JspDataTools" %>
<%
  final JspDataTools tools = JspDataTools.get();
  tools.cleanData();
  response.sendRedirect( "../index.jsp" );
%>
