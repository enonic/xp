Ext.define('Admin.controller.contentManager.FilterPanelController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling filter panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.FilterPanel'
    ],
    requires: [
        'Admin.lib.DateHelper'
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
        filterPanel.updateFacets(rawData.facets);
    },

    doSearch: function (values) {

        function generateFacetsDef() {
            var now = new Date();
            var oneDayAgo = new Date();
            var oneWeekAgo = new Date();
            var oneHourAgo = new Date();
            oneDayAgo.setDate(now.getDate() - 1);
            oneWeekAgo.setDate(now.getDate() - 7);
            Admin.lib.DateHelper.addHours(oneHourAgo, -1);

            var facetDef = {
                "space": {
                    "terms": {
                        "field": "space",
                        "size": 10,
                        "all_terms": true,
                        "order": "term"
                    }
                },
                "type": {
                    "terms": {
                        "field": "contentType",
                        "size": 10,
                        "all_terms": true,
                        "order": "term"
                    }
                },
                "modified": {
                    "range": {
                        "field": "lastModified.date",
                        "ranges": [
                            {
                                "from": oneDayAgo
                            },
                            {
                                "from": oneWeekAgo
                            },
                            {
                                "from": oneHourAgo
                            }
                        ]
                    }
                }
            }
            return facetDef;
        }

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        var filterPanel = this.getContentFilter();

        // set the list mode
        var treeGridPanel = this.getContentTreeGridPanel();
        treeGridPanel.setActiveList(filterPanel.isDirty() ? 'grid' : 'tree');
        treeGridPanel.setFilter({ fulltext: values.query, include: true, facets: generateFacetsDef() });
        treeGridPanel.refresh();
    }

});