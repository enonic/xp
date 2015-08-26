var assert = Java.type('org.junit.Assert');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "total": 20,
    "count": 3,
    "hits": [
        {
            "_id": "111111",
            "_name": "mycontent",
            "_path": "/a/b/mycontent",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "My Content",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {}
        },
        {
            "_id": "222222",
            "_name": "othercontent",
            "_path": "/a/b/othercontent",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "Other Content",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {}
        },
        {
            "_id": "333333",
            "_name": "another",
            "_path": "/a/b/another",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "Another Content",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {}
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
                    "from": 100,
                }
            ]
        },
        "my_date_range": {
            "buckets": [
                {
                    "docCount": 2,
                    "from": "2014-09-01T00:00:00Z",
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

var expectedEmptyJson = {
    "total": 0,
    "count": 0,
    "hits": [],
    "aggregations": {}
};

function assertJson(expected, result) {
    assert.assertEquals(JSON.stringify(expected, null, 2), JSON.stringify(result, null, 2));
}

exports.query = function () {

    var result = content.query({
            "start": 0,
            "count": 100,
            "sort": "_modifiedTime DESC, geodistance('p1', 'p2')",
            "query": "type = 'article' AND fulltext('myField', 'searching for cheese', 'AND') ",
            "contentTypes": [
                "article",
                "comment"
            ],
            "aggregations": {
                "genders": {
                    "terms": {
                        "field": "gender",
                        "order": "_count asc",
                        "size": 2
                    },
                    "aggregations": {
                        "prices": {
                            "histogram": {
                                "field": "price",
                                "interval": 50,
                                "extendedBoundMin": 0,
                                "extendedBoundMax": 500,
                                "minDocCount": 0,
                                "order": "_key desc"
                            }
                        }
                    }
                },
                "by_month": {
                    "dateHistogram": {
                        "field": "init_date",
                        "interval": "1M",
                        "minDocCount": 0,
                        "format": "MM-yyy"
                    }
                },
                "price_ranges": {
                    "range": {
                        "field": "price",
                        "ranges": [
                            {"to": 50},
                            {"from": 50, "to": 100},
                            {"from": 100}
                        ]
                    }
                },
                "my_date_range": {
                    "dateRange": {
                        "field": "date",
                        "format": "MM-yyy",
                        "ranges": [
                            {"to": "now-10M/M"},
                            {"from": "now-10M/M"}
                        ]
                    }
                },
                "time_stats": {
                    "stats": {
                        "field": "item_count"
                    }
                }
            }
        }
    );

    assertJson(expectedJson, result);
};

exports.queryEmpty = function () {
    var result = content.query({});
    assertJson(expectedEmptyJson, result);
};


