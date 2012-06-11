/**
 * Base controller for admin
 */
Ext.define( 'Admin.controller.Controller', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    init: function() {
        // console.log('Admin.controller.Controller: init');
    },

    /**
     * @return {Admin.view.TabPanel}
     */

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    }

});