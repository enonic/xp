Ext.define('Admin.controller.HomeController', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],
    views: [
        'Admin.view.HomeBackgroundImage',
        'Admin.view.HomeLoginPanel',
        'Admin.view.HomeVersionInfo'
    ],

    init: function () {
        this.control({
            '#mainViewport': {
                afterrender: this.onAfterRender
            }
        });
    },

    onAfterRender: function () {
        Ext.create('Admin.view.HomeBackgroundImage');
        Ext.create('Admin.view.HomeVersionInfo');
        Ext.create('Admin.view.HomeLoginPanel');
    }

});