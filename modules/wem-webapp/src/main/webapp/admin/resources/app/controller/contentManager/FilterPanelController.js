Ext.define('Admin.controller.contentManager.FilterPanelController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling filter panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.FilterPanel'
    ],

    init: function () {
        this.control({
            'contentFilter': {
                search: this.doSearch
            },
            'treeGridPanel': {
                datachanged: this.doContentFilterUpdate
            }
        });

    },


    // update contentFilter
    doContentFilterUpdate: function (store) {
        var rawData = store.getProxy().getReader().jsonData;
        var filterPanel = this.getContentFilter();
        filterPanel.updateFacets( rawData.facets );
    },

    doSearch: function (values) {
        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        var filterPanel = this.getContentFilter();

        // set the list mode
        var treeGridPanel = this.getContentTreeGridPanel();
        treeGridPanel.setActiveList(filterPanel.isDirty() ? 'grid' : 'tree');
        treeGridPanel.setFilter( { fulltext: values.query, include: true, interval: 'year' } );
        treeGridPanel.refresh();
    }


});