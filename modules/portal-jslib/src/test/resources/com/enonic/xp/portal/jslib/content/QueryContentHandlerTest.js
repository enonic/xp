var expectedJson = {
    "aggregations": {
        "by_month": {
            "buckets": [{
                "doc_count": 8,
                "key": "2014-01"
            }, {
                "doc_count": 10,
                "key": "2014-02"
            }, {
                "doc_count": 12,
                "key": "2014-03"
            }]
        },
        "genders": {
            "buckets": [{
                "doc_count": 10,
                "key": "male"
            }, {
                "doc_count": 12,
                "key": "female"
            }]
        },
        "item_count": {
            "avg": 3.0,
            "count": 5.0,
            "max": 5.0,
            "min": 1.0,
            "sum": 15.0
        },
        "my_date_range": {
            "buckets": [{
                "doc_count": 2,
                "from": "2014-09-01T00:00:00Z"
            }, {
                "doc_count": 5,
                "from": "2014-10-01T00:00:00Z",
                "to": "2014-09-01T00:00:00Z"
            }, {
                "doc_count": 7,
                "to": "2014-11-01T00:00:00Z"
            }]
        },
        "price_ranges": {
            "buckets": [{
                "doc_count": 2,
                "key": "a",
                "to": 50
            }, {
                "doc_count": 4,
                "from": 50,
                "key": "b",
                "to": 100
            }, {
                "doc_count": 4,
                "from": 100,
                "key": "c"
            }]
        }
    },
    "contents": [{
        "_createdTime": "1970-01-01T00:00:00Z",
        "_creator": "user:system:admin",
        "_id": "111111",
        "_modifiedTime": "1970-01-01T00:00:00Z",
        "_modifier": "user:system:admin",
        "_name": "mycontent",
        "_path": "/a/b/mycontent",
        "data": {},
        "displayName": "My Content",
        "draft": true,
        "hasChildren": false,
        "meta": {},
        "page": {},
        "type": "system:unstructured"
    }, {
        "_createdTime": "1970-01-01T00:00:00Z",
        "_creator": "user:system:admin",
        "_id": "222222",
        "_modifiedTime": "1970-01-01T00:00:00Z",
        "_modifier": "user:system:admin",
        "_name": "othercontent",
        "_path": "/a/b/othercontent",
        "data": {},
        "displayName": "Other Content",
        "draft": true,
        "hasChildren": false,
        "meta": {},
        "page": {},
        "type": "system:unstructured"
    }, {
        "_createdTime": "1970-01-01T00:00:00Z",
        "_creator": "user:system:admin",
        "_id": "333333",
        "_modifiedTime": "1970-01-01T00:00:00Z",
        "_modifier": "user:system:admin",
        "_name": "another",
        "_path": "/a/b/another",
        "data": {},
        "displayName": "Another Content",
        "draft": true,
        "hasChildren": false,
        "meta": {},
        "page": {},
        "type": "system:unstructured"
    }],
    "total": 20
};

var expectedEmptyJson = {
    "aggregations": {},
    "contents": [],
    "total": 0
};

exports.query = function () {

    var result = execute('content.query', {
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
                    "date_histogram": {
                        "field": "init_date",
                        "interval": "1m",
                        "minDocCount": 0
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
                    "date_range": {
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
    assert.assertJson(expectedJson, result);
};

exports.queryEmpty = function () {
    var result = execute('content.query', {});

    assert.assertJson(expectedEmptyJson, result);
};
