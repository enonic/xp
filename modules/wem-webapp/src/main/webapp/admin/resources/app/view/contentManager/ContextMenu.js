Ext.define( 'Admin.view.contentManager.ContextMenu', {
    extend:'Ext.menu.Menu',
    alias:'widget.contentManagerContextMenu',

    items:[
        {
            text:'Edit',
            iconCls:'icon-edit',
            action:'editContent',
            disableOnMultipleSelection:false
        },
        {
            text:'Delete',
            iconCls:'icon-delete',
            action:'deleteContent'
        },
        {
            text:'View',
            iconCls:'icon-view',
            action:'viewContent',
            disableOnMultipleSelection:false
        }
    ]
} );

