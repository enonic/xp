Ext.define('Admin.view.contentStudio.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.contentStudioFilter',

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
                { "name": "system", "count": 4 },
                { "name": "demo", "count": 7 },
                { "name": "B", "count": 1 },
                { "name": "C", "count": 3 }
            ]
        }
    ]

});
