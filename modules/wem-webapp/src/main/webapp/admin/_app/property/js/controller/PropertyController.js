Ext.define('App.controller.PropertyController', {
    extend: 'Ext.app.Controller',

    stores: ['PropertyStore'],
    models: ['PropertyModel'],
    views: ['GridPanel']

});