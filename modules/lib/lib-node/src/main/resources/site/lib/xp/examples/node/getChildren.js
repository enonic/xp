var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Query content using aggregations.
var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

var result = repo.getChildren({
    start: 0,
    count: 2,
    parentKey: "abc"
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
            "id": "b186d24f-ac38-42ca-a6db-1c1bda6c6c26"
        },
        {
            "id": "350ba4a6-589c-498b-8af0-f183850e1120"
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
