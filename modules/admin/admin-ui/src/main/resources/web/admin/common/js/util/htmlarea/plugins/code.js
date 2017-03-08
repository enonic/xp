/*global tinymce:true */

tinymce.PluginManager.add('code', function (editor) {
    function showDialog() {
        editor.execCommand("openCodeDialog", editor);
    }

    editor.addButton('code', {
        icon: 'code',
        tooltip: 'Source code',
        onclick: showDialog,
        type: 'button'
    });
});