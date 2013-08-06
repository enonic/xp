module app_browse_filter {

    export class SpaceBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor() {
            super([
                {
                    "name": 'Space',
                    "displayName": 'Space',
                    "terms": [
                        { "name": 'Public Web', "key": 'public', "count": 8 },
                        { "name": 'Intranet', "key": 'intra', "count": 20 }
                    ]
                },
                {
                    "name": "Type",
                    "displayName": "Type",
                    "terms": [
                        { "name": "Space", "key": "space", "count": 10 },
                        { "name": "Part", "key": "part", "count": 80 },
                        { "name": "Page Template", "key": "template", "count": 7 }
                    ]
                },
                {
                    "name": "Module",
                    "displayName": "Module",
                    "terms": [
                        { "name": "Twitter Bootrstrap", "key": "twitter", "count": 0 },
                        { "name": "Enonic", "key": "enonic", "count": 3 },
                        { "name": "Foo", "key": "foo", "count": 6 }
                    ]
                }
            ]);
        }
    }
}