Ext.define('Admin.view.userstore.preview.UserstorePreviewToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.userstorePreviewToolbar',

    border: false,

    initComponent: function () {
        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Edit',
                        action: 'editUserstore',
                        iconCls: 'icon-edit-24'
                    },
                    {
                        text: 'Delete',
                        action: 'deleteUserstore',
                        iconCls: 'icon-delete-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Synchronize',
                        iconCls: 'icon-refresh',
                        action: 'syncUserstore'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});