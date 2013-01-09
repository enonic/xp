Ext.define('Admin.controller.home.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],
    views: [
        'Admin.view.home.MainBackgroundContainer'
    ],


    init: function () {
        this.control({
            '#mainViewport': {
                afterrender: {
                    fn: this.createView
                }
            }
        });
    },


    createView: function () {
        console.log('init home controller');

        Ext.create('Admin.view.home.MainBackgroundContainer');
    }

});