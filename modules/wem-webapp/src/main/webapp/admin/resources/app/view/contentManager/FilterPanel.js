Ext.define('Admin.view.contentManager.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.contentFilter',

    includeSearch: true,
    updateFacetCount: 'query',
    includeEmptyFacets: 'last'

});
