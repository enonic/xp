Ext.define( 'Admin.controller.userstore.DetailPanelController', {
    extend:'Ext.app.Controller',

    stores:[
        'Admin.store.userstore.UserstoreConfigStore',
        'Admin.store.userstore.UserstoreConnectorStore'
    ],
    models:[
        'Admin.model.userstore.UserstoreConfigModel',
        'Admin.model.userstore.UserstoreConnectorModel'
    ],
    views:[
        'Admin.view.userstore.UserstorePreviewPanel'
    ],

    init:function ()
    {
        this.application.on( {
            updateDetailsPanel:this.updatePanel,
            scope:this
        } );

        this.control( {

            'browseToolbar button[action=editUserstore]':{
                click:function ( item, e, eOpts )
                {
                    var userstore = this.getDetailPanel().getData();
                    this.application.fireEvent( 'editUserstore', userstore, false );
                }
            }

        } );
    },

    updatePanel:function ( selected )
    {
        var userstore = selected[0];
        console.log(userstore)
        var detailPanel = this.getDetailPanel();

        if ( userstore ) {
            detailPanel.setData( userstore.raw );
        }

        this.setButtonsDisabled( false );

    },

    setButtonsDisabled:function ( disable )
    {
        Ext.ComponentQuery.query( 'browseToolbar button[action=editUserstore]' )[0].setDisabled( disable );
        Ext.ComponentQuery.query( 'browseToolbar button[action=deleteUserstore]' )[0].setDisabled( disable );
    },

    getDetailPanel:function ()
    {
        return Ext.ComponentQuery.query( 'userstorePreviewPanel' )[0];
    }

} );
