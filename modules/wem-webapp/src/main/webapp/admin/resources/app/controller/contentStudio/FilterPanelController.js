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
                search: this.doSearch
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

        var gridStore = this.getTreeGridPanel().getActiveList().store;
        gridStore.clearFilter();
        gridStore.getProxy().extraParams = this.getStoreParamsFromFilter(values);
        if (viewMode === 'grid') {
            gridStore.loadPage(1);
        } else if (viewMode === 'tree') {
            gridStore.load();
        }

        // update details to current selection
        var detailPanel = this.getDetailPanel();
        detailPanel.setData(treeGridPanel.getSelection());
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