<%
  if ( !"false".equals( request.getParameter( "edit" ) ) )
  {
%>
<script type="text/javascript" charset="UTF-8" src="../app/lib/jquery-1.8.0.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/lib/jquery-ui-1.8.22.custom.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/lib/jquery.ui.touch-punch.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/jquery.noconflict.js"></script>

<script type="text/javascript" charset="UTF-8" src="../app/js/namespace.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/Init.js"></script>

<script type="text/javascript" charset="UTF-8" src="../app/js/Util.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/HtmlElementReplacer.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/Selection.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/DragDrop.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/PageLeave.js"></script>

<script type="text/javascript" charset="UTF-8" src="../app/js/component/Base.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/component/Page.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/component/Regions.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/component/Windows.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/component/Contents.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/component/Paragraphs.js"></script>

<script type="text/javascript" charset="UTF-8" src="../app/js/view/Base.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/Shader.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/Button.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/Cursor.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/ComponentMenu.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/ParentButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/InsertButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/EditButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/ResetButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/EmptyButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/ViewButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/DragButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/SettingsButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/componentmenu/button/RemoveButton.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/Highlighter.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/ToolTip.js"></script>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/InfoTip.js"></script>

<%
  if ( "true".equals( request.getParameter( "toolbar" ) ) )
  {
%>
<script type="text/javascript" charset="UTF-8" src="../app/js/view/ToolbarTest.js"></script>
<%
  }
%>

<%
  }
%>
