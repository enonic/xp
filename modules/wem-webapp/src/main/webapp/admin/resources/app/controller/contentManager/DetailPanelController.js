Ext.define( 'Admin.controller.contentManager.DetailPanelController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling detail panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.DetailPanel'
    ],

    init: function()
    {
        this.control( {
            'contentDetail': {
                deselectrecord: this.deselectRecord
            }
        } );
    },


    deselectRecord: function( key )
    {
        this.getContentShowPanel().deselect( key );
    }


} );