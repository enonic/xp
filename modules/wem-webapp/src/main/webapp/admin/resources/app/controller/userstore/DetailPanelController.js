Ext.define( 'Admin.controller.userstore.DetailPanelController', {
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
        'Admin.view.userstore.preview.UserstorePreviewPanel',
        'Admin.view.userstore.preview.UserstorePreviewToolbar'
    ],

    init:function ()
    {
        this.application.on( {
            updateDetailsPanel:this.updatePanel,
            scope:this
        } );

        this.control( {

            'userstorePreviewToolbar button[action=editUserstore]': {
                click: function( item, e, eOpts )
                {
                    var userstorePreviewPanel = item.up( 'userstorePreviewPanel' );
                    var userstore = userstorePreviewPanel.getData();
                    this.viewUserstore( userstore );
                    userstorePreviewPanel.close();
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
