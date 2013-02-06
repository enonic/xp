Ext.define('Admin.controller.contentManager.BrowseToolbarController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores: [
    ],

    models: [
    ],
    views: [
        // Why does this point to wizard panel instead of BrowseToolbar?
        'Admin.view.contentManager.wizard.ContentWizardPanel'
    ],

    init: function () {

        this.control({
            'browseToolbar *[action=newContent]': {
                click: function (button, event) {
                    this.getNewContentWindow().doShow();
                }
            },
            'browseToolbar *[action=viewContent]': {
                click: function (button, event) {
                    this.viewContent();
                }
            },
            'browseToolbar *[action=editContent]': {
                click: function (button, event) {
                    this.editContent();
                }
            },
            'browseToolbar *[action=deleteContent]': {
                click: function (button, event) {
                    this.deleteContent();
                }
            },
            'browseToolbar *[action=duplicateContent]': {
                click: function (button, event) {
                    this.duplicateContent();
                }
            }
        });
    }



});
