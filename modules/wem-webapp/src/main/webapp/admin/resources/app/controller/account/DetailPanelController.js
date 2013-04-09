Ext.define('Admin.controller.account.DetailPanelController', {
    extend: 'Admin.controller.account.Controller',

    /*      Controller for handling detail panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.account.DetailPanel'
    ],

    init: function () {
        this.control({
                'accountDetail': {
                    deselect: this.deselectRecord,
                    clearselection: this.clearSelection
                }
            });
    },

    deselectRecord: function (key) {
        var grid = this.getAccountGridPanel();

        var plugin = grid.getPlugin('persistentGridSelection');
        var selModel = plugin ? plugin : grid.getSelectionModel();
        var selection = selModel.getSelection();

        Ext.each(selection, function (item) {
            if (item.get('key') === key) {
                Ext.get('selected-item-box:' + key).remove();
                selModel.deselect(item);
            }
        });
    },

    clearSelection: function () {
        this.getContentTreeGridPanel().deselect(-1);
    }
});
