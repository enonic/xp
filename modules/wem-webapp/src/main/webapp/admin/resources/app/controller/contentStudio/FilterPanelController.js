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

        //TODO: submit the search

        // set the list mode
        var treeGridPanel = this.getTreeGridPanel();
//        TODO: uncomment when list switch is necessary again
//        treeGridPanel.setActiveList(filterPanel.isDirty() ? 'grid' : 'tree');

        // update details to current selection
        var detailPanel = this.getDetailPanel();
        detailPanel.setData(treeGridPanel.getSelection());
    }


});