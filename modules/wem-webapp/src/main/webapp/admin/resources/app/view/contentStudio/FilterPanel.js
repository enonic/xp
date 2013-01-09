Ext.define('Admin.view.contentStudio.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.contentStudioFilter',

    includeSearch: true,
    updateFacetCount: 'query',
    facetData: [
        {
            "name": "Type",
            "terms": [
                { "name": "Content Type", "count": 6 },
                { "name": "Relationship Type", "count": 2 },
                { "name": "Mixin", "count": 5 }
            ]
        },
        {
            "name": "Module",
            "terms": [
                { "name": "System", "count": 4 },
                { "name": "A", "count": 7 },
                { "name": "B", "count": 1 },
                { "name": "C", "count": 3 }
            ]
        }
    ]

});
