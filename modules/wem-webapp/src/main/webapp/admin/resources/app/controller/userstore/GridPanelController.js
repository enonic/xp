Ext.define( 'Admin.controller.userstore.GridPanelController', {
    extend:'Admin.controller.userstore.Controller',

    stores:[
        'Admin.store.userstore.UserstoreConfigStore',
        'Admin.store.userstore.UserstoreConnectorStore'
    ],
    models:[
        'Admin.model.userstore.UserstoreConfigModel',
        'Admin.model.userstore.UserstoreConnectorModel'
    ],
    views:[
        'Admin.view.userstore.ContextMenu',
        'Admin.view.userstore.GridPanel'
    ],

    init:function ()
    {
        Ext.create( 'widget.userstoreContextMenu' );

        // Add listeners to GUI controls.
        this.control( {
            'userstoreGrid':{
                selectionchange:function ( selectionModel, selected, eOpts )
                {
                    this.application.fireEvent( 'updateDetailsPanel', selected )
                },

                itemdblclick:function ( view, record, item, index, e, eOpts )
                {
                    this.application.fireEvent( 'editUserstore', record.data, false );
                },

                itemcontextmenu:this.showContextMenu
            },

            'userstoreGrid button[action=newUserstore]':{
                click:function ( button, e, eOpts )
                {
                    this.application.fireEvent( 'editUserstore', null, true );
                }
            },

            'userstoreContextMenu menuitem[action=editUserstore]':{
                click:function ( item, e, eOpts )
                {
                    var userstore = this.getGrid().getSelectionModel().getSelection()[0].data;
                    this.application.fireEvent( 'editUserstore', userstore, false );
                }
            },
            'userstoreContextMenu menuitem[action=deleteUserstore]':{
                click:function ()
                {
                    this.showDeleteUserstoreWindow();
                }
            }
        } );
    },

    showContextMenu:function ( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getContextMenu().showAt( e.getXY() );
        return false;
    },

    getGrid:function ()
    {
        return Ext.ComponentQuery.query( 'userstoreGrid' )[0];
    },

    getContextMenu:function ()
    {
        return Ext.ComponentQuery.query( 'userstoreContextMenu' )[0];
    }

} );
