Ext.define( 'Admin.controller.contentManager.DialogWindowController', {
    extend: 'Admin.controller.contentManager.ContentController',

    /*      Base controller for the content manager module      */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.DeleteContentWindow'
    ],


    init: function()
    {

        this.control( {
            'deleteContentWindow *[action=deleteContent]': {
                click: this.doDelete
            }
        } );

        this.application.on( {} );

    },

    doDelete: function( el, e )
    {
        var win = this.getDeleteContentWindow();
        this.deleteContentFromDB( win.modelData, function( success )
        {
            win.close();
            var parentApp = parent.mainApp;
            if ( parentApp ) {
                parentApp.fireEvent( 'notifier.show', "Content was deleted",
                        "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                        false );
            }
        } );
    }

} );