Ext.define( 'Admin.view.account.GroupDetailButton', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupDetailButton',

    layout: 'column',
    margin: 5,
    bodyStyle: {
        background: 'lightGrey'
    },

    initComponent: function()
    {
        var iconPane = {
            xtype: 'panel',
            layout: 'fit',
            border: 0,
            bodyStyle: {
                background: 'lightGrey'
            },
            items: [
                {
                    xtype: 'image',
                    style: {
                        background: 'lightGrey'
                    },
                    padding: 2,
                    src: 'app/account/images/group.png'
                }
            ]
        };
        var displayNamePane = {
            xtype: 'panel',
            border: 0,
            layout: 'fit',
            bodyStyle: {
                background: 'lightGrey'
            },
            items: [
                {
                    xtype: 'displayfield',
                    style: {
                        background: 'lightGrey'
                    },
                    value: this.value
                }
            ]
        };
        var buttonPane = {
            xtype: 'panel',
            border: 0,
            bodyStyle: {
                background: 'lightGrey'
            },
            margin: {left: 2, right: 0, bottom: 0, top: 0},
            items: [
                {
                    xtype: 'button',
                    style: {
                        background: 'lightGrey'
                    },
                    action: 'deleteGroup',
                    iconCls: 'icon-delete'
                }
            ]
        };
        this.items = [iconPane, displayNamePane, buttonPane];
        this.callParent( arguments );
    }


} );