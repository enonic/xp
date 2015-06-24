
/*global tinymce:true */

tinymce.PluginManager.add('anchor', function (editor) {
    function showDialog() {
        var selectedNode = editor.selection.getNode(), anchorElm;

        if (selectedNode.tagName == 'P' && selectedNode.childElementCount == 1 &&
            selectedNode.firstElementChild.tagName == 'A' && !selectedNode.firstElementChild.href) {
            selectedNode = selectedNode.firstElementChild;
        }

        if (selectedNode.tagName == 'A' && selectedNode.id) {
            anchorElm = selectedNode;
        }

        editor.execCommand("openAnchorDialog", {
            editor: editor,
            element: anchorElm
        });
    }

    editor.addButton('anchor', {
        icon: 'anchor',
        tooltip: 'Anchor',
        onclick: showDialog,
        stateSelector: 'a:not([href])'
    });

});