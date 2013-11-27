<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Enonic &middot; CMS</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <script type="text/javascript">
        var CONFIG = {
            baseUri: '/'
        };
    </script>
    <script type="text/javascript" src="../../admin/common/lib/_all.js"></script>
    <script type="text/javascript" charset="UTF-8" src="../../admin/live-edit/js/_all.js"></script>
    <script type="text/javascript">
        $(function() {
            var uploaderConfig = {
                multiSelection: true,
                buttonsVisible: false,
                imageVisible: false
            };
            var imageUploader = new api_ui.ImageUploader("image-selector-upload-dialog", api_util.getRestUri("upload"), uploaderConfig);
            $("body").append(imageUploader.getHTMLElement());
        });
    </script>
</head>
<body>



</body>
</html>
