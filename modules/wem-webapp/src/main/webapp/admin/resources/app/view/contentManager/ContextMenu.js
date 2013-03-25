Ext.define('Admin.view.contentManager.ContextMenu', {
    extend: 'Admin.view.BaseContextMenu',
    alias: 'widget.contentManagerContextMenu',


    items: [
        {
            text: ' New',
            icon: undefined,
            action: 'newContent',
            disableOnMultipleSelection: true
        },
        {
            text: 'Edit',
            icon: undefined,
            action: 'editContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Open',
            icon: undefined,
            action: 'viewContent',
            disableOnMultipleSelection: false
        },
        {
            text: 'Delete',
            icon: undefined,
            action: 'deleteContent'
        },
        {
            text: 'Duplicate',
            icon: undefined,
            action: 'duplicateContent'
        },
        {
            text: 'Move',
            icon: undefined,
            disabled: true,
            action: 'moveContent'
        }
    ]
});

