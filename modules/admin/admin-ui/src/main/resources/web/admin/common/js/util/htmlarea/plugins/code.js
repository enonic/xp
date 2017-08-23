CKEDITOR.plugins.add('code', {

    init: function (editor) {
        editor.ui.addButton('Code', {
            icon: 'code',
            label: 'Source code',
            command: 'openCodeDialog'
        });
    }
});