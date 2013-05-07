Ext.define('Admin.controller.DetailPanelController', {
    extend: 'Admin.controller.Controller',

    /*      Controller for handling detail panel UI events       */

    stores: [],
    models: [],

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