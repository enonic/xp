Ext.define('Admin.view.contentStudio.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentStudioContextMenu',

    cls: 'admin-context-menu',
    border: false,

    items: [
        {
            text: 'Edit',
            iconCls: 'icon-edit',
            action: 'editSchema'
        },
        {
            text: 'Open',
            iconCls: 'icon-view',
            action: 'viewContentType'
        },
        {
            text: 'Delete',
            iconCls: 'icon-delete',
            action: 'deleteSchema'
        }
    ]
});

