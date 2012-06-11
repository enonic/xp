/**
 * Base controller for userstore
 */
Ext.define( 'Admin.controller.userstore.Controller', {
    extend:'Admin.controller.Controller',

    /*      Base controller for the userstore module      */

    stores:[],
    models:[],
    views:[],


    init:function ()
    {

        this.control( {

        } );

        this.application.on( {
            showDeleteUserstoreWindow:{
                fn:this.showDeleteUserstoreWindow,
                scope:this
            }
        } );

    },


    /*      Public, should operate with accounts only      */

    showDeleteUserstoreWindow:function ( accounts )
    {
        if ( !accounts ) {
            accounts = this.getUserstoreGridPanel().getSelection();
        } else {
            accounts = [].concat( accounts );
        }
        if ( accounts && accounts.length > 0 ) {
            this.getDeleteAccountWindow().doShow( accounts );
        }
    },


    getDeleteAccountWindow:function ()
    {
        var win = Ext.ComponentQuery.query( 'deleteUserstoreWindow' )[0];
        if ( !win ) {
            win = Ext.create( 'widget.deleteUserstoreWindow' );
        }
        return win;
    },

    getUserstoreGridPanel:function ()
    {
        return Ext.ComponentQuery.query( 'userstoreGrid' )[0];
    },

    getMainPanel:function ()
    {
        return Ext.ComponentQuery.query( 'mainPanel' )[0];

    },

    getCmsTabPanel:function ()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    }


} );