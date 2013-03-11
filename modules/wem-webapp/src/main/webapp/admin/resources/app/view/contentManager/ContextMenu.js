Ext.define('Admin.view.contentManager.ContextMenu', {
    extend: 'Admin.view.BaseContextMenu',
    alias: 'widget.contentManagerContextMenu',

    items: [
        {
            text: 'Edit',
            //iconCls: 'icon-edit',
            icon: undefined,
            action: 'editContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Open',
            //iconCls: 'icon-view',
            icon: undefined,
            action: 'viewContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Delete',
            icon: undefined,
            //iconCls: 'icon-delete',
            action: 'deleteContent'
        }
    ]
});

