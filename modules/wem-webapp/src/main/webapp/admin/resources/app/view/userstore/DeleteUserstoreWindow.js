Ext.define( 'Admin.view.userstore.DeleteUserstoreWindow', {
    extend:'Admin.view.BaseDialogWindow',
    alias:'widget.deleteUserstoreWindow',

    dialogTitle:'Delete Userstore(s)',

    items:[
        {
            margin:'10px 0 10px 0px',
            xtype:'container',
            defaults:{
                xtype:'button',
                scale:'medium',
                margin:'0 10 0 0'
            },
            items:[
                {
                    text:'Delete',
                    iconCls:'icon-delete-user-24',
                    itemId:'deleteUserstoreButton',
                    action:'deleteUserstore'
                }
            ]
        }
    ],

    initComponent:function ()
    {
        this.callParent( arguments );
    },

    doShow:function ( selection )
    {
        if ( selection ) {
            this.setDialogInfoTpl( Templates.userstore.userstoreInfo );
            this.callParent( [selection[0]] );
        }
    }
} );