Ext.define('Admin.controller.DetailPanelController', {
    extend: 'Admin.controller.Controller',

    /*      Controller for handling detail panel UI events       */

    stores: [],
    models: [],
    /*    views: [
     'Admin.view.contentManager.DetailPanel'
     ],*/

    init: function () {
        this.control({
            'contentDetail': {
                deselect: this.deselectRecord,
                clearselection: this.clearSelection
            }
        });
    },

    deselectRecord: function (key) {
        this.getContentTreeGridPanel().deselect(key);
    },

    clearSelection: function () {
        this.getContentTreeGridPanel().deselect(-1);
    }


});