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
            ">1 day": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneDayAgo,
                            "include_lower": true
                        }
                    }
                }
            },
            ">1 hour": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneHourAgo,
                            "include_lower": true
                        }
                    }
                }
            },
            ">1 week": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneWeekAgo,
                            "include_lower": true
                        }
                    }
                }
            }
        };

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        var params = { fulltext: values.query, include: true, facets: facetDef };
        var treeGridPanel = this.getContentTreeGridPanel();
        var filterPanel = this.getContentFilter();

        if ( values.query ) {
            Admin.lib.RemoteService.content_find( params, function ( rpcResp ) {
                if ( rpcResp.success ) {
                    filterPanel.updateFacets(rpcResp.facets);

                    var i, ids = [],
                        contents = rpcResp.contents,
                        length = contents.length;

                    for ( i = 0; i < length; i++ ) {
                        ids.push( contents[i].id );
                    }
                    treeGridPanel.setContentSearchParams({ search : true, contentIds: ids });
                    treeGridPanel.refresh();
                }
            } );
        } else {
            treeGridPanel.setContentSearchParams( { search : false } );
            treeGridPanel.refresh();
        }


    }

});