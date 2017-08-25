var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');


var multiRepoConnection = nodeLib.multiRepoConnect({
    sources: [
        {
            repoId: 'my-repo',
            branch: 'master',
            principals: ["role:system.admin"]
        },
        {
            repoId: 'cms-repo',
            branch: 'draft',
            principals: ["role:system.admin"]
        }
    ]
});

// BEGIN
// Query multi-repo connection.
var result = multiRepoConnection.query({
    start: 0,
    count: 2,
    query: "startTime > instant('2016-10-11T14:38:54.454Z')",
    filters: {
        boolean: {
            must: {
                exists: {
                    field: "modifiedTime"
                }
            },
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
    }
});

log.info("result %s", JSON.stringify(result, null, 4));
// END


// BEGIN
// Result set returned.
var expected = {
    "total": 12902,
    "count": 2,
    "hits": [
        {
            "id": "b186d24f-ac38-42ca-a6db-1c1bda6c6c26",
            "score": 1.2300000190734863,
            "repoId": "my-repo",
            "branch": "master"
        },
        {
            "id": "350ba4a6-589c-498b-8af0-f183850e1120",
            "score": 1.399999976158142,
            "repoId": "cms-repo",
            "branch": "draft"
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);

