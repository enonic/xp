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
                afterrender: function (cmp) {
                    this.loadContentAndFacets();
                },
                search: this.doSearch,
                reset: this.doReset
            }
        });

    },


    doSearch: function (values) {

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        this.loadContentAndFacets(values);

    },


    doReset: function (dirty) {

        if (!dirty) {
            // prevent reset if the filter is not dirty
            return false;
        }

        this.loadContentAndFacets();
    },


    loadContentAndFacets: function (values) {
        var me = this;

        var params = this.createLoadContentParams(values);
        var filterDirty = values && Object.getOwnPropertyNames(values).length > 0;

        Admin.lib.RemoteService.content_find(params, function (response) {
            if (response && response.success) {

                // set facet data
                me.getContentFilter().updateFacets(response.facets);

                // set tree data
                var ids = Ext.Array.pluck(response.contents, 'id'),
                    treeGridPanel = me.getContentTreeGridPanel();

                treeGridPanel.setContentSearchParams(filterDirty && ids.length > 0 ? { contentIds: ids } : {});
                treeGridPanel.refresh();
            }
        })

    },


    createLoadContentParams: function (values) {

        var now = new Date();
        var oneDayAgo = new Date();
        var oneWeekAgo = new Date();
        var oneHourAgo = new Date();
        oneDayAgo.setDate(now.getDate() - 1);
        oneWeekAgo.setDate(now.getDate() - 7);
        Admin.lib.DateHelper.addHours(oneHourAgo, -1);

        var facets = {
            "space": {
                "terms": {
                    "field": "space",
                    "size": 10,
                    "all_terms": false,
                    "order": "term"
                }
            },
            "type": {
                "terms": {
                    "field": "contentType",
                    "size": 10,
                    "all_terms": false,
                    "order": "term"
                }
            },
            ">1 day": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneDayAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            ">1 hour": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneHourAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            ">1 week": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneWeekAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            }
        };

        return {
            fulltext: values && values.query || undefined,
            include: true,
            facets: facets
        };
    }

});
