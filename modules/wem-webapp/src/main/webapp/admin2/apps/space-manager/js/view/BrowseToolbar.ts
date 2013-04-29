Ext.define('Admin.view.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.spaceBrowseToolbar',

    cls: 'admin-toolbar',
    border: true,

    defaults: {
        scale: 'medium',
        iconAlign: 'top',
        minWidth: 64
    },

    items: [
        {
            text: ' New',
            action: 'newSpace'
        },
        {
            text: 'Edit',
            disabled: true,
            action: 'editSpace'
        },
        {
            text: 'Open',
            disabled: true,
            action: 'viewSpace'
        },
        {
            text: 'Delete',
            disabled: true,
            action: 'deleteSpace'
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    }

});
