Ext.define('Admin.view.account.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.accountFilter',

    includeSearch: true,
    updateFacetCount: 'query',
    includeEmptyFacets: 'last'

});
