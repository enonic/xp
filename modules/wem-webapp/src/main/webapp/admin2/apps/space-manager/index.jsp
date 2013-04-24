<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>

  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>

  <!-- Styles -->
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/css/icons-icomoon.css">
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/css/icons-metro.css">
  <link rel="stylesheet" type="text/css" href="../../../admin/resources/lib/ext/resources/css/admin.css">

  <!-- ExtJS -->
  <script type="text/javascript" src="../../../admin/resources/lib/ext/ext-all-debug.js"></script>


  <!-- Configuration -->
  <script type="text/javascript">

    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl() %>'
    };

    Ext.Loader.setConfig({
      enabled: false,
      disableCaching: false
    });

    Ext.override(Ext.LoadMask, {
      floating: {
        shadow: false
      },
      msg: undefined,
      cls: 'admin-load-mask',
      msgCls: 'admin-load-text',
      maskCls: 'admin-mask-white'
    });

  </script>


  <!-- TODO: Inline everything. -->
  <script type="text/javascript" src="../../../admin/resources/app/view/XTemplates.js"></script>

  <!-- Third party plugins -->
  <script type="text/javascript" src="../../../admin/resources/lib/plupload/js/plupload.full.js"></script>


  <script type="text/javascript" src="js/lib/Ping.js"></script>

  <script type="text/javascript" src="js/lib/UriHelper.js"></script>
  <script type="text/javascript" src="js/lib/JsonRpcProvider.js"></script>
  <script type="text/javascript" src="js/lib/RemoteService.js"></script>

  <script type="text/javascript" src="js/MessageBus.js"></script>
  <script type="text/javascript" src="js/NotificationManager.js"></script>

  <script type="text/javascript" src="js/plugin/PersistentGridSelectionPlugin.js"></script>
  <script type="text/javascript" src="js/plugin/GridToolbarPlugin.js"></script>
  <script type="text/javascript" src="js/plugin/fileupload/FileUploadGrid.js"></script>
  <script type="text/javascript" src="js/plugin/fileupload/PhotoUploadButton.js"></script>
  <script type="text/javascript" src="js/plugin/fileupload/PhotoUploadWindow.js"></script>

  <script type="text/javascript" src="js/model/SpaceModel.js"></script>

  <script type="text/javascript" src="js/store/SpaceStore.js"></script>

  <script type="text/javascript" src="js/view/WizardLayout.js"></script>
  <script type="text/javascript" src="js/view/WizardHeader.js"></script>
  <script type="text/javascript" src="js/view/WizardPanel.js"></script>

  <script type="text/javascript" src="js/view/BaseContextMenu.js"></script>
  <script type="text/javascript" src="js/view/DropDownButton.js"></script>
  <script type="text/javascript" src="js/view/BaseDetailPanel.js"></script>
  <script type="text/javascript" src="js/view/DetailToolbar.js"></script>
  <script type="text/javascript" src="js/view/DetailPanel.js"></script>

  <script type="text/javascript" src="js/view/BaseDialogWindow.js"></script>
  <script type="text/javascript" src="js/view/DeleteSpaceWindow.js"></script>

  <script type="text/javascript" src="js/view/BaseTreeGridPanel.js"></script>
  <script type="text/javascript" src="js/view/TreeGridPanel.js"></script>

  <script type="text/javascript" src="js/view/ContextMenu.js"></script>

  <script type="text/javascript" src="js/view/wizard/Toolbar.js"></script>
  <script type="text/javascript" src="js/view/wizard/SpaceStepPanel.js"></script>
  <script type="text/javascript" src="js/view/wizard/WizardPanel.js"></script>

  <script type="text/javascript" src="js/view/AdminImageButton.js"></script>
  <script type="text/javascript" src="js/view/TopBarMenuItem.js"></script>
  <script type="text/javascript" src="js/view/TopBarMenu.js"></script>
  <script type="text/javascript" src="js/view/TopBar.js"></script>
  <script type="text/javascript" src="js/view/TabPanel.js"></script>
  <script type="text/javascript" src="js/view/BaseFilterPanel.js"></script>
  <script type="text/javascript" src="js/view/FilterPanel.js"></script>
  <script type="text/javascript" src="js/view/BrowseToolbar.js"></script>

  <script type="text/javascript" src="js/controller/Controller.js"></script>
  <script type="text/javascript" src="js/controller/SpaceController.js"></script>

  <script type="text/javascript" src="js/controller/FilterPanelController.js"></script>
  <script type="text/javascript" src="js/controller/GridPanelController.js"></script>
  <script type="text/javascript" src="js/controller/BrowseToolbarController.js"></script>
  <script type="text/javascript" src="js/controller/DetailPanelController.js"></script>
  <script type="text/javascript" src="js/controller/DetailToolbarController.js"></script>
  <script type="text/javascript" src="js/controller/DialogWindowController.js"></script>
  <script type="text/javascript" src="js/controller/WizardController.js"></script>

  <!-- Application -->
  <script type="text/javascript" src="js/main.js"></script>

</head>
<body>
</body>
</html>
