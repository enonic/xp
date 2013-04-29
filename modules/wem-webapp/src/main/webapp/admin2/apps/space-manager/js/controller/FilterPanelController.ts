Ext.define('Admin.controller.FilterPanelController', {
    extend: 'Admin.controller.Controller',

    /*      Controller for handling filter panel UI events       */

    stores: [],
    models: [],
    views: [],

    init: function () {
        this.control({
            'spaceFilter': {
                search: this.doSearch,
                reset: this.doReset
            }
        });
    },


    doSearch: function (values) {

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        // cast the filter params on the store
        var treeGrid = this.getSpaceTreeGridPanel();
        treeGrid.setRemoteSearchParams(this.getStoreParamsFromFilter(values));
        treeGrid.refresh();

        var selection = treeGrid.getSelection();

        this.updateDetailPanel(selection);
        this.updateToolbarButtons(selection);
    },

    doReset: function (dirty) {
        if (!dirty) {
            // prevent reset if the filter is not dirty
            return false;
        }

        var treeGrid = this.getSpaceTreeGridPanel();
        treeGrid.setRemoteSearchParams({});
        treeGrid.refresh();

        return true;
    },

    getStoreParamsFromFilter: function (values) {
        // TODO: return params for the store
        return {};
    }


});