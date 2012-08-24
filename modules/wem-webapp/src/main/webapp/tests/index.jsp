<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>

  <title>Tests: Enonic WEM Admin</title>

  <!-- Siesta CSS -->
  <link rel="stylesheet" type="text/css" href="siesta/resources/css/siesta-all.css">

  <!-- CSS used by the application -->
  <link rel="stylesheet" type="text/css" href="../admin/resources/lib/ext/resources/css/ext-all.css">
  <link rel="stylesheet" type="text/css" href="../admin/resources/css/main.css">
  <link rel="stylesheet" type="text/css" href="../admin/resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="../admin/resources/css/user-preview.css">
  <link rel="stylesheet" type="text/css" href="../admin/resources/css/user-preview-panel.css">
  <link rel="stylesheet" type="text/css" href="../admin/resources/css/BoxSelect.css">
  <style type="text/css">
      /* EXT JS .x-grid-cell overrides .x-grid-cell in the framework */
    .x-grid-cell {
      overflow: visible !important
    }
  </style>

  <!-- Ext JS -->
  <script type="text/javascript" src="../admin/resources/lib/ext/ext-all-debug.js"></script>
  <script type="text/javascript" src="../admin/resources/app/view/XTemplates.js"></script>

  <!-- Configuration -->
  <script type="text/javascript" src="../admin/global.config.js"></script>
  <script type="text/javascript">
    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl() %>'
    };
  </script>

  <!-- Third party plugins -->
  <script type="text/javascript" src="../admin/resources/lib/plupload/js/plupload.full.js"></script>
  <script type="text/javascript" src="../admin/resources/lib/jit/jit-yc.js"></script>

  <!-- Siesta -->
  <script type="text/javascript" src="siesta/siesta-all.js"></script>
  <script type="text/javascript" src="AdminTestUtil.js"></script>

  <!-- Harness / Dashboard -->
  <script type="text/javascript" src="harness.setup.js"></script>

</head>
<body>
</body>
</html>
