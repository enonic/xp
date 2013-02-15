Ext.define('Admin.controller.spaceAdmin.DetailToolbarController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    stores: [
    ],

    models: [
    ],
    views: [
        'Admin.view.spaceAdmin.DetailToolbar'
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
            },
            'spaceDetailToolbar *[action=deleteSpace]': {
                click: function (el, e) {
                    var space = el.up('spaceDetail').getData();
                    this.showDeleteSpaceWindow(space);
                }
            }

        });
    }



});
