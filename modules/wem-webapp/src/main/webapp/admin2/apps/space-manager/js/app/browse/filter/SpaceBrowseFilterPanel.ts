module app_browse_filter {

    export class SpaceBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor() {
            super([
                {
                    "name": 'Space',
                    "displayName": 'Space',
                    "terms": [
                        { "name": 'Public Web', displayName: 'Public Web', "key": 'public', "count": 8 },
                        { "name": 'Intranet', displayName: 'Intranet', "key": 'intra', "count": 20 }
                    ]
                },
                {
                    "name": "Type",
                    "displayName": "Type",
                    "terms": [
                        { "name": "Space", displayName: 'Space', "key": "space", "count": 10 },
                        { "name": "Part", displayName: 'Part', "key": "part", "count": 80 },
                        { "name": "Page Template", displayName: 'Page Template', "key": "template", "count": 7 }
                    ]
                },
                {
                    "name": "Module",
                    "displayName": "Module",
                    "terms": [
                        { "name": "Twitter Bootrstrap", displayName: 'Twitter Bootstrap', "key": "twitter", "count": 0 },
                        { "name": "Enonic", displayName: 'Enonic', "key": "enonic", "count": 3 },
                        { "name": "Foo", displayName: 'Foo', "key": "foo", "count": 6 }
                    ]
                }
            ]);
        }
    }
}