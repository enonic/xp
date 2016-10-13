var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Query content using aggregations.
var result = nodeLib.query({
    start: 0,
    count: 2,
    query: "startTime > instant('2016-10-11T14:38:54.454Z')",
    sort: "duration DESC",
    aggregations: {
        urls: {
            terms: {
                field: "url",
                order: "_count desc",
                size: 2
            },
            aggregations: {
                duration: {
                    histogram: {
                        field: "duration",
                        interval: 100,
                        minDocCount: 1,
                        extendedBoundMin: 0,
                        extendedBoundMax: 10000,
                        order: "_key desc"
                    }
                },
                durationStats: {
                    stats: {
                        field: "duration"
                    }
                }
            }
        }
    }
});

log.info('Found ' + result.total + ' number of contents');

for (var i = 0; i < result.hits.length; i++) {
    var node = result.hits[i];
    log.info('Node ' + node.id + ' found');
}
// END

// BEGIN
// Result set returned.
var expected ={
    "total": 12902,
    "count": 2,
    "hits": [
        {
            "id": "b186d24f-ac38-42ca-a6db-1c1bda6c6c26"
        },
        {
            "id": "350ba4a6-589c-498b-8af0-f183850e1120"
        }
    ],
    "aggregations": {
        "urls": {
            "buckets": [
                {
                    "key": "/portal/draft/superhero/search",
                    "docCount": 6762,
                    "duration": {
                        "buckets": [
                            {
                                "key": "1600",
                                "docCount": 2
                            },
                            {
                                "key": "1400",
                                "docCount": 1
                            },
                            {
                                "key": "1300",
                                "docCount": 5
                            }
                        ]
                    },
                    "durationStats": {
                        "count": 6762.0,
                        "min": 12.0,
                        "max": 1649.0,
                        "avg": 286.59,
                        "sum": 1937941.0
                    }
                },
                {
                    "key": "/portal/draft/superhero",
                    "docCount": 1245,
                    "duration": {
                        "buckets": [
                            {
                                "key": "1600",
                                "docCount": 2
                            },
                            {
                                "key": "1400",
                                "docCount": 1
                            },
                            {
                                "key": "1300",
                                "docCount": 5
                            }
                        ]
                    },
                    "durationStats": {
                        "count": 6762.0,
                        "min": 12.0,
                        "max": 1649.0,
                        "avg": 286.59,
                        "sum": 1937941.0
                    }
                }
            ]
        }
    }
};
// END

assert.assertJsonEquals(expected, result);
