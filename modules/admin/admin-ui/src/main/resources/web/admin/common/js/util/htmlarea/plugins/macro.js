/*global tinymce:true */

tinymce.PluginManager.add('macro', function (editor) {

    function showDialog() {

    }

    editor.addButton('macro', {
        icon: 'media',
        tooltip: 'Insert macro',
        onclick: showDialog
    });
});
