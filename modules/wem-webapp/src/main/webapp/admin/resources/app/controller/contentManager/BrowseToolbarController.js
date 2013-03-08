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

    requires: [
        'Admin.view.contentManager.ToolbarMenu'
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
            },
            'browseToolbar *[action=showToolbarMenu]': {
                click: function (button, event) {
                    this.showToolbarMenu(button, event);
                }
            },
            '*[action=moveDetailPanel]': {
                click: function (button, event) {
                    this.moveDetailPanel(button, event);
                }
            },
            '*[action=toggleDetailPanel]': {
                click: function (button, event) {
                    this.toggleDetailPanel(button, event);
                }
            }
        });
    }



});
