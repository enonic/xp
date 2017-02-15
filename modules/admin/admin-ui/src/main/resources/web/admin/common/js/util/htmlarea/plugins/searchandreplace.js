/*global tinymce:true */

tinymce.PluginManager.add('searchandreplace', function (editor) {
    function showDialog() {
        editor.execCommand("openSearchAndReplaceDialog", editor);
    }

    editor.addShortcut('Meta+F', '', showDialog);

});