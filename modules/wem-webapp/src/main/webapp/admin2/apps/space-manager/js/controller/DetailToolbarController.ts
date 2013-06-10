Ext.define('Admin.controller.DetailToolbarController', {
    extend: 'Admin.controller.Controller',

    stores: [
    ],

    models: [
    ],
    views: [
    ],

    init: function () {

        this.control({
            'spaceDetailToolbar *[action=closeSpace]': {
                click: function (el, e) {
                    this.getCmsTabPanel().getActiveTab().close();
                }
            },
            'spaceDetailToolbar *[action=editSpace]': {
                click: function (el, e) {
                    var space = el.up('spaceDetail').getData();
                    this.editSpace(space);
                }
            }
        });
    }



});
