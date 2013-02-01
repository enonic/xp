Ext.define('Admin.controller.contentManager.DetailToolbarController', {
    extend: 'Admin.controller.contentManager.Controller',

    stores: [
    ],

    models: [
    ],
    views: [
        'Admin.view.contentManager.DetailToolbar'
    ],

    init: function () {

        this.control({
            'contentDetailToolbar *[action=publishContent]': {
                click: function (el, e) {

                }
            },
            'contentDetailToolbar *[action=editContent]': {
                click: function (el, e) {
                    this.editContent();
                }
            },
            'contentDetailToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent();
                }
            },
            'contentDetailToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    this.duplicateContent();
                }
            },
            'contentDetailToolbar *[action=moveContent]': {
                click: function (el, e) {

                }
            },
            'contentDetailToolbar *[action=relations]': {
                click: function (el, e) {

                }
            },
            'contentDetailToolbar *[action=closeContent]': {
                click: function (el, e) {
                    this.getCmsTabPanel().getActiveTab().close();
                }
            }


        });
    }



});
