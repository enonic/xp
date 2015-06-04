/*global tinymce:true */

tinymce.PluginManager.add('link', function (editor) {

    function showDialog() {
        var selectedElm = editor.selection.getNode();
        var anchorElm = editor.dom.getParent(selectedElm, 'a[href]');

        editor.execCommand("openLinkDialog", {
            editor: editor,
            link: anchorElm
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
