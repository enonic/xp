/*global tinymce:true */

tinymce.PluginManager.add('charmap', function (editor) {
    function showDialog() {
        editor.execCommand("openCharMapDialog", editor);
    }

    editor.addButton('charmap', {
        icon: 'charmap',
        tooltip: 'Special character',
        onclick: showDialog
    });
});