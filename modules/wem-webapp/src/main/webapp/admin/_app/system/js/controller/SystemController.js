Ext.define( 'App.controller.SystemController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    init: function()
    {
        this.control({
            'viewport': {
                afterrender: this.selectDefaultApplication
            },
            'systemNavigation': {
                itemclick: this.selectApplication
            }
         });
    },

    selectDefaultApplication: function( cmp, options ) {
        var nav = cmp.down('systemNavigation');
        if ( nav ) {
            var first = nav.getRootNode().firstChild;
            nav.getSelectionModel().select( first );
            this.selectApplication(null, first);
        }
    },

    selectApplication: function( view, record, item, index, evt, opts ) {
        var iframe = Ext.getDom('system-iframe');
        if (iframe && record)
            iframe.src = record.data.appUrl;
    }

} );
