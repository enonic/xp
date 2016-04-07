/*global tinymce:true */

tinymce.PluginManager.add('image', function (editor) {

    function showDialog() {
        var selectedNode = editor.selection.getNode(),
            imgEl = figureEl = null,
            dom = editor.dom,
            rng = editor.selection.getRng();

        switch (selectedNode.nodeName) {
            case 'IMG':
                imgEl = editor.selection.getNode();
                figureEl = wemjq(selectedNode).parent("figure")[0];
                break;
            case 'FIGURE':
                figureEl = editor.selection.getNode();
                imgEl = wemjq(selectedNode).children("img")[0];
                break;
            case 'FIGCAPTION':
                figureEl = wemjq(selectedNode).parent("figure")[0];
                imgEl = (wemjq(selectedNode).prev("img") || wemjq(selectedNode).next("img"))[0];
                break;
        }

        function insertFigureElement(html) {
            if (figureEl) {
                dom.remove(figureEl);
            }

            editor.focus();
            editor.selection.setContent(html);

            var imgElm = dom.get('__mcenew');
            dom.setAttrib(imgElm, 'id', null);

            if (editor.selection) {
                editor.selection.select(imgElm);
                editor.nodeChanged();
            }

            return imgElm;
        }

        editor.execCommand("openImageDialog", {
            editor: editor,
            element: imgEl,
            container: rng.endContainer,
            callback: insertFigureElement
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

