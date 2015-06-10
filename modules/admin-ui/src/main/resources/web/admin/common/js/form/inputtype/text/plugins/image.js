/*global tinymce:true */

tinymce.PluginManager.add('image', function (editor) {

    function showDialog() {
        var imgEl = editor.selection.getNode().nodeName == 'IMG' ? editor.selection.getNode() : null;

        editor.execCommand("openImageDialog", {
            editor: editor,
            element: imgEl
        });
    }


    editor.addButton('image', {
        icon: 'image',
        tooltip: 'Insert/edit image',
        onclick: showDialog,
        stateSelector: 'img:not([data-mce-object],[data-mce-placeholder])'
    });

    editor.addMenuItem('image', {
        icon: 'image',
        text: 'Insert/edit image',
        onclick: showDialog,
        context: 'insert',
        prependToContext: true
    });

    editor.addCommand('mceImage', showDialog);

});

