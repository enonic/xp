/*global tinymce:true */

tinymce.PluginManager.add('source', function (editor) {
    function showDialog() {
        editor.execCommand("openSourceDialog", editor);
    }

    editor.addCommand("mceCodeEditor", showDialog);

    editor.addButton('code', {
        icon: 'code',
        tooltip: 'Source code',
        onclick: showDialog,
        type: 'button'
    });

    editor.addMenuItem('code', {
        icon: 'code',
        text: 'Source code',
        context: 'tools',
        onclick: showDialog
    });

});