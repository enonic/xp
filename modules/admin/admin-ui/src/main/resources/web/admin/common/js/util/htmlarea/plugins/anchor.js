/*global tinymce:true */

tinymce.PluginManager.add('anchor', function (editor) {
    function showDialog() {
        editor.execCommand("openAnchorDialog", editor);
    }

    editor.addButton('anchor', {
        icon: 'anchor',
        tooltip: 'Anchor',
        onclick: showDialog
    });

});