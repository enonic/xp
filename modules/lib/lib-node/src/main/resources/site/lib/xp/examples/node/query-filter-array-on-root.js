var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// BEGIN
// Query content using aggregations.
var result = repo.query({
    start: 0,
    count: 2,
    query: "startTime > instant('2016-10-11T14:38:54.454Z')",
    filters: [
        {
            exists: {
                field: "field1"
            }
        },
        {
            exists: {
                field: "field2"
            }
        },
        {
            exists: {
                field: "field3      "
            }
        }
    ],
    sort: "duration DESC"
});

log.info('Found ' + result.total + ' number of contents');

for (var i = 0; i < result.hits.length; i++) {
    var node = result.hits[i];
    log.info('Node ' + node.id + ' found');
}
// END

// BEGIN
// Result set returned.
var expected = {
    "total": 12902,
    "count": 2,
    "hits": [
        {
            "id": "b186d24f-ac38-42ca-a6db-1c1bda6c6c26",
            "score": 1.2300000190734863
        },
        {
            "id": "350ba4a6-589c-498b-8af0-f183850e1120",
            "score": 1.399999976158142
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
