var contentLib = require('/lib/xp/content');
var t = require('/lib/xp/testing');

// BEGIN
// Query content using aggregations.
var result = contentLib.query({
    start: 0,
    count: 2,
    sort: "modifiedTime DESC, geoDistance('data.location', '59.91,10.75')",
    query: "data.city = 'Oslo' AND fulltext('data.description', 'garden', 'AND') ",
    filters: {
        boolean: {
            must: [
                {
                    exists: {
                        field: "modifiedTime"
                    }
                },
                {
                    exists: {
                        field: "another"
                    }
                }
            ],
            mustNot: {
                hasValue: {
                    field: "myField",
                    values: [
                        "cheese",
                        "fish",
                        "onion"
                    ]
                }
            }
        },
        notExists: {
            field: "unwantedField"
        },
        ids: {
            values: ["id1", "id2"]
        }
    },
    branch: "draft",
    contentTypes: [
        app.name + ":house",
        app.name + ":apartment"
    ],
    aggregations: {
        floors: {
            terms: {
                field: "data.number_floor",
                order: "_count asc"
            },
            aggregations: {
                prices: {
                    histogram: {
                        field: "data.price",
                        interval: 1000000,
                        extendedBoundMin: 1000000,
                        extendedBoundMax: 3000000,
                        minDocCount: 0,
                        order: "_key desc"
                    }
                }
            }
        },
        by_month: {
            dateHistogram: {
                field: "data.publish_date",
                interval: "1M",
                minDocCount: 0,
                format: "MM-yyyy"
            }
        },
        price_ranges: {
            range: {
                field: "data.price",
                ranges: [
                    {to: 2000000},
                    {from: 2000000, to: 3000000},
                    {from: 3000000}
                ]
            }
        },
        my_date_range: {
            dateRange: {
                field: "data.publish_date",
                format: "MM-yyyy",
                ranges: [
                    {to: "now-10M/M"},
                    {from: "now-10M/M"}
                ]
            }
        },
        price_stats: {
            stats: {
                field: "data.price"
            }
        }
    }
});

log.info('Found ' + result.total + ' number of contents');

for (var i = 0; i < result.hits.length; i++) {
    var content = result.hits[i];
    log.info('Content ' + content._name + ' found');
}
// END

// BEGIN
// Result set returned.
var expected = {
    "total": 20,
    "count": 2,
    "hits": [
        {
            "_id": "id1",
            "_name": "name1",
            "_path": "/a/b/name1",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "My Content 1",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {},
            "attachments": {},
            "publish": {}
        },
        {
            "_id": "id2",
            "_name": "name2",
            "_path": "/a/b/name2",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "My Content 2",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {},
            "attachments": {},
            "publish": {}
        }
    ],
    "aggregations": {
        "genders": {
            "buckets": [
                {
                    "key": "male",
                    "docCount": 10
                },
                {
                    "key": "female",
                    "docCount": 12
                }
            ]
        },
        "by_month": {
            "buckets": [
                {
                    "key": "2014-01",
                    "docCount": 8
                },
                {
                    "key": "2014-02",
                    "docCount": 10
                },
                {
                    "key": "2014-03",
                    "docCount": 12
                }
            ]
        },
        "price_ranges": {
            "buckets": [
                {
                    "key": "a",
                    "docCount": 2,
                    "to": 50
                },
                {
                    "key": "b",
                    "docCount": 4,
                    "from": 50,
                    "to": 100
                },
                {
                    "key": "c",
                    "docCount": 4,
                    "from": 100
                }
            ]
        },
        "my_date_range": {
            "buckets": [
                {
                    "docCount": 2,
                    "from": "2014-09-01T00:00:00Z"
                },
                {
                    "docCount": 5,
                    "from": "2014-10-01T00:00:00Z",
                    "to": "2014-09-01T00:00:00Z"
                },
                {
                    "docCount": 7,
                    "to": "2014-11-01T00:00:00Z"
                }
            ]
        },
        "item_count": {
            "count": 5,
            "min": 1,
            "max": 5,
            "avg": 3,
            "sum": 15
        }
    }
};
// END

t.assertJsonEquals(expected, result);
