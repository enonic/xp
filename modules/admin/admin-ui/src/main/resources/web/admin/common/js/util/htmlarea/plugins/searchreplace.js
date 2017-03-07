/*global tinymce:true */

tinymce.PluginManager.add('searchreplace', function (editor) {
    function showDialog() {
        editor.execCommand("openSearchReplaceDialog", editor);
    }

    editor.addShortcut('Meta+F', '', showDialog);

});