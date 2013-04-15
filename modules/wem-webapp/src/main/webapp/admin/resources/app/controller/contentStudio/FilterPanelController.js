Ext.define('Admin.controller.contentStudio.FilterPanelController', {
    extend: 'Admin.controller.contentStudio.Controller',

    /*      Controller for handling filter panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentStudio.FilterPanel'
    ],

    init: function () {
        this.control({
            'contentStudioFilter': {
                search: this.doSearch,
                reset: this.doReset
            }
        });
    },


    doSearch: function (values) {

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        var filterPanel = this.getFilterPanel();

        // show as tree only when query and module filters are not set, otherwise show as flat list in grid
        var viewMode = filterPanel.isDirty() && (values.query || (values.Module && values.Module.length)) ? 'grid' : 'tree';

        // set the list mode
        var treeGridPanel = this.getTreeGridPanel();
        treeGridPanel.setActiveList(viewMode);

        treeGridPanel.setRemoteSearchParams(this.getStoreParamsFromFilter(values));
        treeGridPanel.refresh();

        // update details to current selection
        var detailPanel = this.getDetailPanel();
        detailPanel.setData(treeGridPanel.getSelection());
    },

    doReset: function(dirty) {
        if (!dirty) {
            // prevent reset if the filter is not dirty
            return false;
        }

        var treeGrid = this.getTreeGridPanel();
        treeGrid.setRemoteSearchParams({});
        treeGrid.refresh();

        return true;
    },

    getStoreParamsFromFilter: function (filterPanelValues) {
        var params = {types: [], modules: []};
        var paramTypes = params.types;
        var paramModules = params.modules;
        var typeFilter = filterPanelValues.Type;
        var moduleFilter = filterPanelValues.Module;
        if (typeFilter) {
            if (Ext.Array.contains(typeFilter, 'Relationship Type')) {
                paramTypes.push('RELATIONSHIP_TYPE');
            }
            if (Ext.Array.contains(typeFilter, 'Content Type')) {
                paramTypes.push('CONTENT_TYPE');
            }
            if (Ext.Array.contains(typeFilter, 'Mixin')) {
                paramTypes.push('MIXIN');
            }
        }
        Ext.Array.each(moduleFilter, function(moduleName) {
            paramModules.push(moduleName);
        });
        params.search = filterPanelValues.query;
        return params;
    }

});