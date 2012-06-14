Ext.define( 'Admin.view.userstore.BrowseToolbar', {
    extend:'Ext.toolbar.Toolbar',
    alias:'widget.browseToolbar',

    border:false,

    initComponent:function ()
    {
        var buttonDefaults = {
            scale:'medium',
            iconAlign:'top',
            minWidth:64
        };

        this.items = [
            {
                xtype:'buttongroup',
                columns:1,
                defaults:buttonDefaults,
                items:[
                    {
                        text:'New',
                        action:'newUserstore',
                        iconCls:'icon-userstore-add-24'
                    }
                ]
            },
            {
                xtype:'buttongroup',
                columns:2,
                defaults:buttonDefaults,
                items:[
                    {
                        text:'Edit',
                        action:'editUserstore',
                        disabled:true,
                        iconCls:'icon-edit-24'
                    },
                    {
                        text:'Delete',
                        action:'deleteUserstore',
                        disabled:true,
                        iconCls:'icon-delete-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'View',
                        action: 'viewUserstore',
                        iconCls: 'icon-view-24'
                    }
                ]
            },
            {
                xtype:'buttongroup',
                columns:1,
                defaults:buttonDefaults,
                items:[
                    {
                        text:'Synchronize',
                        iconCls:'icon-refresh',
                        action:'syncUserstore'
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );
