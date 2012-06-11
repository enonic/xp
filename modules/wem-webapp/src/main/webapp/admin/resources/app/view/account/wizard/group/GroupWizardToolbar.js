Ext.define( 'Admin.view.account.wizard.group.GroupWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.groupWizardToolbar',

    border: false,

    isNewGroup: true,

    initComponent: function()
    {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var leftGrp = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Save',
                        action: 'saveGroup',
                        itemId: 'save',
                        disabled: true,
                        iconCls: 'icon-save-24'
                    }
                ]
            }
        ];

        if ( !this.isNew && !this.isRole ) {
            leftGrp.push( {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Delete',
                        action: 'deleteGroup',
                        iconCls: 'icon-delete-user-24'
                    }
                ]
            } );
        }

        var rightGrp = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Close',
                    action: 'closeWizard',
                    iconCls: 'icon-cancel-24'
                }
            ]};

        this.items = leftGrp.concat( '->', rightGrp );
        this.callParent( arguments );
    }

} );
