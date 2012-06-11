Ext.define( 'Admin.controller.contentManager.ContentController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Base controller for content model     */

    stores: [],
    models: [],
    views: [],

    init: function()
    {
    },


    /*   Public, only CRUD model methods here please     */

    saveContentToDB: function( content, callback )
    {
        //TODO
        var key = -1;
        if ( Ext.isFunction( callback ) ) {
            callback.call( this, key );
        }
    },

    deleteContentFromDB: function( content, callback )
    {
        //TODO
        var success = true;
        if ( Ext.isFunction( callback ) ) {
            callback.call( this, success );
        }
    }

} );