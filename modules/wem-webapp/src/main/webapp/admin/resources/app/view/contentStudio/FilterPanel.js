Ext.define('Admin.view.contentStudio.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.contentStudioFilter',

    includeSearch: true,
    updateFacetCount: 'query',
    facetData: [
        {
            "name": "Type",
            "terms": [
                { "name": "Field", "count": 4 },
                { "name": "Fieldset", "count": 7 },
                { "name": "Content type", "count": 1 },
            ]
        }
    ]

});
