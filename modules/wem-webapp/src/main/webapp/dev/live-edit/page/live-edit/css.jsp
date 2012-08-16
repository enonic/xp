<%
  if ( request.getParameter( "edit" ) == null || !request.getParameter( "edit" ).equals( "false" ) )
  {
%>
<link rel="stylesheet" href="../resources/css/live-edit.css" type="text/css"/>
<%
  }
%>