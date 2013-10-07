Ext.define('Admin.controller.spaceAdmin.BrowseToolbarController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.spaceAdmin.wizard.WizardPanel'
    ],

    init: function () {

        this.control({
            'spaceBrowseToolbar *[action=newSpace]': {
                click: function (button, event) {
                    this.showNewSpaceWindow();
                }
            },
            'spaceBrowseToolbar *[action=viewSpace]': {
                click: function (button, event) {
                    this.viewSelectedSpaces();
                }
            },
            'spaceBrowseToolbar *[action=editSpace]': {
                click: function (button, event) {
                    this.editSelectedSpaces();
                }
            },
            'spaceBrowseToolbar *[action=deleteSpace]': {
                click: function (button, event) {
                    this.deleteSelectedSpaces();
                }
            }
        });
    },

    viewSelectedSpaces: function () {
        var selection = this.getSpaceTreeGridPanel().getSelection();
        for (var i = 0; i < selection.length; i++) {
            this.viewSpace(selection[i]);
        }
    },

    editSelectedSpaces: function () {
        var selection = this.getSpaceTreeGridPanel().getSelection();
        for (var i = 0; i < selection.length; i++) {
            this.editSpace(selection[i]);
        }
    },

    deleteSelectedSpaces: function () {
        var selection = this.getSpaceTreeGridPanel().getSelection();
        this.showDeleteSpaceWindow(selection);
    }

});
