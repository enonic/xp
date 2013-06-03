Ext.define('Admin.controller.BrowseToolbarController', {
    extend: 'Admin.controller.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores: [],
    models: [],
    views: [
//        'Admin.view.wizard.WizardPanel'
    ],

    init: function () {

        APP.event.OpenSpaceEvent.on((event) => {
            this.viewSelectedSpaces();
        });

        this.control({
            '#spaceBrowseToolbar *[action=newSpace]': {
                click: function (button, event) {
                    this.showNewSpaceWindow();
                }
            },
            '#spaceBrowseToolbar *[action=viewSpace]': {
                click: function (button, event) {
                    this.viewSelectedSpaces();
                }
            },
            '#spaceBrowseToolbar *[action=editSpace]': {
                click: function (button, event) {
                    this.editSelectedSpaces();
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
    }

});
