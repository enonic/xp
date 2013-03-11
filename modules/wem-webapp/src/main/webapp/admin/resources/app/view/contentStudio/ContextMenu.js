Ext.define('Admin.view.contentStudio.ContextMenu', {
    extend: 'Admin.view.BaseContextMenu',
    alias: 'widget.contentStudioContextMenu',

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

