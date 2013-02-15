Ext.define('Admin.view.spaceAdmin.DetailToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.spaceDetailToolbar',

    cls: 'admin-toolbar',

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {

        this.items = [

            {
                text: 'Edit',
                action: 'editSpace'
            },
            {
                text: 'Delete',
                action: 'deleteSpace'
            },
            '->',
            {
                text: 'Close',
                action: 'closeSpace'
            }

        ];

        this.callParent(arguments);
    }

});
