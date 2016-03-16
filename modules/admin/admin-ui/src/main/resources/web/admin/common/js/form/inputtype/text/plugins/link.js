/*global tinymce:true */

tinymce.PluginManager.add('link', function (editor) {

    function showDialog() {
        function isOnlyTextSelected(anchorElm) {
            var html = editor.selection.getContent();

            // Partial html and not a fully selected anchor element
            if (/</.test(html) && (!/^<a [^>]+>[^<]+<\/a>$/.test(html) || html.indexOf('href=') == -1)) {
                return false;
            }

            if (anchorElm) {
                var nodes = anchorElm.childNodes, i;

                if (nodes.length === 0) {
                    return false;
                }

                for (i = nodes.length - 1; i >= 0; i--) {
                    if (nodes[i].nodeType != 3) {
                        return false;
                    }
                }
            }

            return true;
        }

        function getAnchorList() {
            var anchorList = [];

            tinymce.each(editor.dom.select('a:not([href])'), function (anchor) {
                if (anchor.id && anchorList.indexOf(anchor.id) === -1) {
                    anchorList.push(anchor.id);
                }
            });

            return anchorList;
        }

        var selectedElm = editor.selection.getNode();
        var anchorElm = editor.dom.getParent(selectedElm, 'a[href]');
        var onlyTextSelected = isOnlyTextSelected(anchorElm);

        editor.execCommand("openLinkDialog", {
            editor: editor,
            element: anchorElm,
            text: editor.selection.getContent({format: 'text'}),
            anchorList: getAnchorList(),
            onlyTextSelected: onlyTextSelected
        });
    }

    editor.addButton('link', {
        icon: 'link',
        tooltip: 'Insert/edit link',
        shortcut: 'Meta+K',
        onclick: showDialog,
        stateSelector: 'a[href]'
    });

    editor.addButton('unlink', {
        icon: 'unlink',
        tooltip: 'Remove link',
        cmd: 'unlink',
        stateSelector: 'a[href]'
    });

    editor.addShortcut('Meta+K', '', showDialog);
    editor.addCommand('mceLink', showDialog);

    this.showDialog = showDialog;

    editor.addMenuItem('link', {
        icon: 'link',
        text: 'Insert/edit link',
        shortcut: 'Meta+K',
        onclick: showDialog,
        stateSelector: 'a[href]',
        context: 'insert',
        prependToContext: true
    });
});
