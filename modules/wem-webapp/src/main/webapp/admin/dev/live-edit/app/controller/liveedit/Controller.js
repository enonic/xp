Ext.define('Admin.controller.liveedit.Controller', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [
        'Admin.view.liveedit.Toolbar'
    ],

    requires: [
        'Admin.util.liveedit.Util'
    ],


    init: function () {
        this.control({
            'liveeditToolbar > buttongroup > button[itemId=saveButton]': {
                click: this.save
            }
        });
    },


    save: function () {
        var util = Admin.util.liveedit.Util;
        alert(JSON.stringify(util.getIframeWindow().AdminLiveEdit.Util.getPageConfiguration(), null, 4));
    }

    /*
     insertDummyComponent: function(combo, value)
     {
     var util = Admin.util.liveedit.Util;
     util.getIframeWindow().AdminLiveEdit.Util.insertWindowComponent(value);
     }
     */

});