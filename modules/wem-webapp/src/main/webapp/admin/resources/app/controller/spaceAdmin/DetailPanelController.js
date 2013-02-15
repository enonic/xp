Ext.define('Admin.controller.spaceAdmin.DetailPanelController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    /*      Controller for handling detail panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.DetailPanel'
    ],

    init: function () {
        this.control({
            'spaceDetail': {
                deselect: this.deselectRecord,
                clearselection: this.clearSelection
            }
        });
    },


    deselectRecord: function (key) {
        this.getSpaceTreeGridPanel().deselect(key);
    },

    clearSelection: function () {
        this.getSpaceTreeGridPanel().deselect(-1);
    }


});