Ext.define('Admin.view.FilterPanel', {
    extend: 'Admin.view.BaseFilterPanel',
    alias: 'widget.spaceFilter',

    facetData: [
        {
            "name": 'Space',
            "terms": [
                { "name": 'Public Web', "key": 'public', "count": 8 },
                { "name": 'Intranet', "key": 'intra', "count": 20 }
            ]
        },
        {
            "name": "Type",
            "terms": [
                { "name": "Space", "key": "space", "count": 10 },
                { "name": "Part", "key": "part", "count": 80 },
                { "name": "Page Template", "key": "template", "count": 7 }
            ]
        },
        {
            "name": "Module",
            "terms": [
                { "name": "Twitter Bootrstrap", "key": "twitter", "count": 0 },
                { "name": "Enonic", "key": "enonic", "count": 3 },
                { "name": "Foo", "key": "foo", "count": 6 }
            ]
        }
    ]

});
