var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/assert');

// BEGIN
// Query content using aggregations.
var result = contentLib.query({
    start: 0,
    count: 2,
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
    ]
};
// END

assert.assertJsonEquals(expected, result);
