<%
  if ( request.getParameter( "edit" ) == null || !request.getParameter( "edit" ).equals( "false" ) )
  {
%>
<div class="live-edit-loader-splash-container">
  <div class="live-edit-loader-splash-content">
    <img src="../resources/images/enonic-logo-72x72-transparent.png" alt=""/>

    <div>loading...</div>
  </div>
</div>
<%
  }
%>
