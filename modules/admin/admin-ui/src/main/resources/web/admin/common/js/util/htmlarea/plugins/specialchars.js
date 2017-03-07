/*global tinymce:true */

tinymce.PluginManager.add('specialchars', function (editor) {
    function showDialog() {
        editor.execCommand("openSpecialCharsDialog", editor);
    }

    editor.addButton('specialchars', {
        icon: 'charmap',
        tooltip: 'Special character',
        onclick: showDialog
    });
});