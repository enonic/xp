Ext.define('Admin.view.contentManager.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.contentFilter',

    includeSearch: true,
    updateFacetCount: 'query',
    facetData: [
        {
            "name": 'Space',
            "terms": [
                { "name": 'Travel', "key": 'travel', "count": 2 },
                { "name": 'Blueman', "key": 'blueman', "count": 1 },
                { "name": 'Cityscape', "key": 'cityscape', "count": 3 }
            ]
        },
        {
            "name": "Type",
            "terms": [
                { "name": "News", "key": "news", "count": 7 },
                { "name": "Article", "key": "article", "count": 4 }
            ]
        },
        {
            "name": "Last Modified",
            "terms": [
                { "name": "< 1h", "key": "hour", "count": 0 },
                { "name": "< 1d", "key": "day", "count": 3 },
                { "name": "< 1w", "key": "week", "count": 6 },
                { "name": "> [from date]", "key": "from", "count": 2 },
                { "name": "< [to date]", "key": "to", "count": 0 }
            ]
        }
    ]

});
