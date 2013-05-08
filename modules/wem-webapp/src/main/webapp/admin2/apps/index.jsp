<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>

  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>

  <!-- Styles -->
  <link rel="stylesheet" type="text/css" href="../../admin/resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="../../admin/resources/css/icons-icomoon.css">
  <link rel="stylesheet" type="text/css" href="../../admin/resources/css/icons-metro.css">
  <link rel="stylesheet" type="text/css" href="../../admin/resources/lib/ext/resources/css/admin.css">

  <!-- Libraries -->
  <script type="text/javascript" src="../../admin/resources/lib/ext/ext-all-debug.js"></script>
  <script type="text/javascript" src="../../admin/resources/lib/plupload/js/plupload.full.js"></script>

</head>
<body>

<!-- API -->
<script type="text/javascript" src="../api/js/api.js"></script>

<!-- Configuration -->
<script type="text/javascript">

  API.util.baseUri = '<%= helper.getBaseUrl() %>';

</script>

<%
  String module = request.getParameter( "module" );
  if ( module == null )
  {
%>

<div style="text-align: center; margin: 50px;">

  <div style="font-size: 400%">No module requested</div>
  <div style="font-size: 200%">Please add ?module=<i>name</i> to load specified module.</div>

  <div style="font-size: 150%; margin: 20px;">
    Available modules:
    <ul>
      <li><a href="?module=space-manager">Space Manager</a></li>
    </ul>
  </div>
</div>

<%
}
else
{
%>

<script type="text/javascript" src="<%= module %>/js/all.js"></script>
<%
  }
%>
</body>
</html>
