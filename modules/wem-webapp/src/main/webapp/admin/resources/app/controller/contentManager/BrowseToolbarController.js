Ext.define('Admin.controller.contentManager.BrowseToolbarController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores: [
    ],

    models: [
    ],
    views: [
        'Admin.view.contentManager.wizard.ContentWizardPanel'
    ],

    init: function () {

        this.control({
            'browseToolbar *[action=newContent]': {
                click: function (el, e) {
                    this.createContent('contentType', el.qualifiedContentType);
                }
            },
            'browseToolbar *[action=newSite]': {
                click: function (el, e) {
                    this.createContent('site');
                }
            },
            'browseToolbar *[action=viewContent]': {
                click: function (el, e) {
                    this.viewContent();
                }
            },
            'browseToolbar *[action=editContent]': {
                click: function (el, e) {
                    this.editContent();
                }
            },
            'browseToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent();
                }
            },
            'browseToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    this.duplicateContent();
                }
            }
        });
    }



});
