Ext.define('Admin.controller.HomeController', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],
    views: [
        'Admin.view.HomeBackgroundImage'
    ],

    init: function () {
        this.control({
            '#mainViewport': {
                afterrender: this.initLogin
            }
        });
    },

    initLogin: function () {
        Ext.create('Admin.view.HomeBackgroundImage');
        console.log('init login');
    }

});