<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Enonic WEM Admin</title>

    <!-- Styles -->
    <link rel="stylesheet" type="text/css" href="${baseUrl}/admin/resources/css/icons.css">
    <link rel="stylesheet" type="text/css" href="${baseUrl}/admin/resources/css/icons-icomoon.css">
    <link rel="stylesheet" type="text/css" href="${baseUrl}/admin/resources/css/icons-metro.css">
    <link rel="stylesheet" type="text/css" href="${baseUrl}/admin/resources/lib/ext/resources/css/admin.css">
    <link rel="stylesheet" type="text/css" href="${baseUrl}/admin/resources/lib/ux/toggleslide/css/style.css"/>
    <link rel="stylesheet/less" type="text/css" href="${baseUrl}/admin/resources/less/styles.less">
    <link rel="stylesheet" type="text/css" href="${baseUrl}/admin/resources/lib/slickgrid/slick.grid.css"/>

    <!-- Libraries -->
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/ext/ext-all-debug.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/plupload/js/plupload.full.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/jquery-2.0.2.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/jquery-ui-1.10.3.custom.min.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/jquery.ui.live-draggable.js"></script>
    <script type="text/javascript" charset="UTF-8" src="${baseUrl}/admin/live-edit/lib/jquery.simulate.js"></script>

    <!-- App launcher only! -->
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/signals.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/hasher.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/crossroads.js"></script>

    <!--Slick dependencies -->
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/lib/jquery.event.drag-2.2.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/lib/jquery.event.drop-2.2.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/slick.core.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/slick.grid.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/slick.dataview.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/slick.remotemodel.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/slick.rowselectionmodel.js"></script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/slickgrid/slick.checkboxselectcolumn.js"></script>

    <script type="text/javascript">
        $.noConflict();
    </script>
    <script type="text/javascript" src="${baseUrl}/admin/resources/lib/mousetrap.min.js"></script>

</head>
<body>

<!-- API -->
<script type="text/javascript" src="${baseUrl}/admin/api/js/api.js"></script>

<!-- Configuration -->
<script type="text/javascript">
    api_util.baseUri = '${baseUrl}';
</script>

<script type="text/javascript" src="${baseUrl}/admin/resources/js/less.js"></script>

<!-- App javascript -->
<script type="text/javascript" src="${baseUrl}/admin/apps/${app}/js/all.js"></script>

</body>
</html>
