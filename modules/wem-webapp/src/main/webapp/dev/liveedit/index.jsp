<%@ page import="com.enonic.wem.admin.jsp.LiveEditHelper" %>
<%@ page import="com.enonic.wem.admin.jsp.LiveEditService" %>
<html>
<body>

<%

  LiveEditService service = LiveEditHelper.getService();
  String key = request.getParameter( "key" );


  Object image = service.getImage( key );


%>

Parameter <%= image %>


</body>
</html>
