Ext.define('Admin.controller.spaceAdmin.FilterPanelController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    /*      Controller for handling filter panel UI events       */

    stores: [],
    models: [],
    views: [],

    init: function () {
        this.control({
            'spaceFilter': {
                search: this.doSearch
            }
        });
    },


    doSearch: function (values) {

        // set browse tab active
        this.getCmsTabPanel().setActiveTab(0);

        var filterPanel = this.getSpaceFilterPanel();

        //TODO: submit the search

        // set the list mode
        var treeGridPanel = this.getSpaceTreeGridPanel();
        treeGridPanel.setActiveList(filterPanel.isDirty() ? 'grid' : 'tree');

        var selection = treeGridPanel.getSelection();

        this.updateDetailPanel(selection);
        this.updateToolbarButtons(selection);
    }


});