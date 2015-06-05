/*global tinymce:true */

tinymce.PluginManager.add('image', function (editor) {

    function showDialog() {
        editor.execCommand("openImageDialog", {
            editor: editor,
            element: editor.selection.getNode()
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

