Ext.define('App.view.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.languageContextMenu',

    items: [
        {
            text: 'Edit Language',
            iconCls: 'icon-edit',
            action: 'editLanguage'
        },
        {
            text: 'Delete Language',
            iconCls: 'icon-delete',
            action: 'deleteLanguage'
        }
    ]
});

