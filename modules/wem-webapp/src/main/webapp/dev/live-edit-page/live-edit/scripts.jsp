<%
  if ( "true".equals( request.getParameter( "edit" ) ) )
  {
%>

<!-- Libs -->
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery-1.9.1.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery-ui-1.10.3.custom.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.simulate.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.ui.touch-punch.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.ba-resize.min.js"></script>

<!-- It is important that this is loaded right after all jQuery dependencies -->
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.noconflict.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/js/all.js"></script>

<%
  }
%>
