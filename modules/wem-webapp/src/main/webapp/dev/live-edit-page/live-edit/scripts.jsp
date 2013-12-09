<%
  if ( "true".equals( request.getParameter( "edit" ) ) )
  {
%>

<script type="text/javascript">
    var CONFIG = {
        baseUri: 'http://localhost:8080'
    };
</script>

<!-- Libs -->
<script type="text/javascript" src="../../admin/common/lib/_all.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin/live-edit/lib/jquery.ui.touch-punch.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin/live-edit/lib/jquery.ba-resize.min.js"></script>

<!-- It is important that this is loaded right after all jQuery dependencies -->
<script type="text/javascript" charset="UTF-8" src="../../admin/live-edit/lib/jquery.noconflict.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin/live-edit/js/_all.js"></script>

<script type="text/javascript">
    $(function() {
        var componentType = new LiveEdit.component.ComponentType(LiveEdit.component.Type.IMAGE);
        componentType.setName("image");
        var component = new LiveEdit.component.Component();
        component.setComponentType(componentType);

        var emptyImageComponent = LiveEdit.component.dragdropsort.EmptyComponent.createEmptyComponentHtml(component);
        $("#main").prepend(emptyImageComponent.getHTMLElement());
        emptyImageComponent.init();
    });
</script>




<%
  }
%>
