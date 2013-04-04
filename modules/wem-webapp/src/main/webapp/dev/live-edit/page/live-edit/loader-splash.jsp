<%
  if ( "true".equals( request.getParameter( "edit" ) ) )
  {
%>
<div class="live-edit-loader-splash-container">
  <div class="live-edit-loader-splash-content">
    <!--img src="../app/images/enonic-logo-72x72-transparent.png" alt=""/-->
    <img src="../app/images/spinner.png" alt=""/>

    <div>loading page...</div>
  </div>
</div>
<%
  }
%>
