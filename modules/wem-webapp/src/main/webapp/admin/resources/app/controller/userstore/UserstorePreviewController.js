Ext.define( 'Admin.controller.userstore.UserstorePreviewController', {
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
                    this.createUserstoreTab( this.getCurrentUserstoreData() );
                    this.getCmsTabPanel().getActiveTab().close();
                }
            },
            'userstorePreviewToolbar button[action=deleteUserstore]': {
                click: function( item, e, eOpts )
                {
                    this.showDeleteUserstoreWindow( {data: this.getCurrentUserstoreData()} );
                }
            }

        } );
    },

    getCurrentUserstoreData: function()
    {
        var userstorePreviewPanel = this.getCmsTabPanel().getActiveTab();
        if (userstorePreviewPanel.getData)
        {
            return userstorePreviewPanel.getData();
        }
        return ;
    },

    updatePanel:function ( selected )
    {
        var userstore = selected[0];
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
        Ext.ComponentQuery.query( 'browseToolbar button[action=viewUserstore]' )[0].setDisabled( disable );
    },

    getDetailPanel:function ()
    {
        return Ext.ComponentQuery.query( 'userstorePreviewPanel' )[0];
    }

} );
