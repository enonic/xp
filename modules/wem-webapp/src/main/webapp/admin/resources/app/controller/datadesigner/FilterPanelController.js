Ext.define('Admin.controller.datadesigner.FilterPanelController', {
    extend: 'Admin.controller.datadesigner.Controller',

    /*      Controller for handling filter panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.datadesigner.FilterPanel'
    ],

    init: function () {
        this.control({
            'datadesignerFilter': {
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
        treeGridPanel.setActiveList(filterPanel.isDirty() ? 'grid' : 'tree');

        // update details to current selection
        var detailPanel = this.getDetailPanel();
        detailPanel.setData(treeGridPanel.getSelection());
    }


});