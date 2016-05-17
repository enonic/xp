/*global tinymce:true */

tinymce.PluginManager.add('macro', function (editor) {

    function showDialog() {
        var dom = editor.dom;

        editor.execCommand("openMacroDialog", {
            editor: editor,
            callback: insertMacroCallback
        });

        function insertMacroCallback(html) {
            editor.focus();
            editor.selection.setContent(html);

            var macroElm = dom.get('__mcenew');
            dom.setAttrib(macroElm, 'id', null);

            if (editor.selection) {
                editor.selection.select(macroElm);
                editor.nodeChanged();
            }

            return macroElm;
        }
    }

    editor.addButton('macro', {
        icon: 'media',
        tooltip: 'Insert macro',
        onclick: showDialog
    });
});
