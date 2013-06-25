Ext.define('Admin.controller.contentManager.DetailToolbarController', {
    extend: 'Admin.controller.contentManager.Controller',

    stores: [
    ],

    models: [
    ],
    views: [
        'Admin.view.contentManager.open.OpenToolbar'
    ],

    init: function () {

        this.control({
            'contentOpenToolbar *[action=newContent]': {
                click: function (el, e) {
                    this.getNewContentWindow().doShow();
                }
            },
            'contentOpenToolbar *[action=editContent]': {
                click: function (el, e) {
                    this.editContent();
                }
            },
            'contentOpenToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent();
                }
            },
            'contentOpenToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    this.duplicateContent();
                }
            },
            'contentOpenToolbar *[action=moveContent]': {
                click: function (el, e) {

                }
            },
            'contentOpenToolbar *[action=relations]': {
                click: function (el, e) {

                }
            },
            'contentOpenToolbar *[action=closeContent]': {
                click: function (el, e) {
                    this.getCmsTabPanel().getActiveTab().close();
                }
            }
        });
    }

});
