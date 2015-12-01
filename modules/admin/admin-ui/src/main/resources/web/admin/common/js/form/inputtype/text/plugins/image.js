/*global tinymce:true */

tinymce.PluginManager.add('image', function (editor) {

    function showDialog() {
        var selectedNode = editor.selection.getNode(),
            imgEl = null,
            rng = editor.selection.getRng();

        switch (selectedNode.nodeName) {
            case 'IMG':
                imgEl = editor.selection.getNode();
                break;
            case 'FIGURE':
                imgEl = wemjq(selectedNode).children("img")[0];
                break;
            case 'FIGCAPTION':
                imgEl = (wemjq(selectedNode).prev("img") || wemjq(selectedNode).next("img"))[0] ;
                break;
        }

        function setCursorToFigCaption(id) {
            var figCaptionEl = editor.getBody().querySelector('figcaption[id="' + id + '"]');
            if (figCaptionEl && !figCaptionEl.innerHTML) {
                figCaptionEl.scrollIntoView(false);
                editor.selection.placeCaretAt(figCaptionEl.offsetLeft, figCaptionEl.offsetTop + 10);
                figCaptionEl.innerHTML = "";
            }
        }

        editor.execCommand("openImageDialog", {
            editor: editor,
            element: imgEl,
            container: rng.endContainer,
            callback: setCursorToFigCaption
        });
    }


    editor.addButton('image', {
        icon: 'image',
        tooltip: 'Insert/edit image',
        onclick: showDialog,
        stateSelector: 'img:not([data-mce-object],[data-mce-placeholder]), figure, figcaption'
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

