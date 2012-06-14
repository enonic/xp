Ext.define( 'Admin.controller.contentManager.FilterPanelController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling filter panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.FilterPanel'
    ],

    init: function()
    {
        this.control( {
            'contentFilter #filterButton': {
                click: this.doSearch
            },
            'contentFilter checkbox': {
                change: this.doSearch
            }
        } );
    },


    doSearch: function()
    {
        // update filter title
        var filterPanel = this.getContentFilter();
        filterPanel.updateTitle();

        // submit the search
        var values = filterPanel.getValues();
        //TODO: submit the search

        // set the list mode
        var showPanel = this.getContentShowPanel();
        showPanel.setActiveList( values.length == 0 ? 'tree' : 'grid' );

        // update details to current selection
        var detailPanel = this.getContentDetailPanel();
        detailPanel.setData( showPanel.getSelection() );
    }


} );