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
                    this.loadContentAndFacets({});
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

        this.loadContentAndFacets({});
    }

});
