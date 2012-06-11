Ext.define( 'Admin.view.account.preview.user.UserPreviewToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.userPreviewToolbar',

    initComponent: function()
    {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var leftGrp = [];

        if ( this.isEditable ) {
            leftGrp.push(
                    {
                        xtype: 'buttongroup',
                        columns: 2,
                        defaults: buttonDefaults,
                        items: [
                            {
                                text: 'Edit',
                                action: 'editUser',
                                iconCls: 'icon-edit-generic'
                            },
                            {
                                text: 'Delete',
                                action: 'deleteUser',
                                iconCls: 'icon-delete-user-24'
                            }
                        ]
                    },
                    {
                        xtype: 'buttongroup',
                        columns: 1,
                        defaults: buttonDefaults,
                        items: [
                            {
                                text: 'Change Password',
                                action: 'changePassword',
                                iconCls: 'icon-change-password-24'
                            }
                        ]
                    }
            );
        }

        var rightGrp = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Close',
                    action: 'closePreview',
                    iconCls: 'icon-cancel-24'
                }
            ]
        };

        this.items = leftGrp.concat( '->', rightGrp );
        this.callParent( arguments );
    }

} );
