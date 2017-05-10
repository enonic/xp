var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');


// BEGIN
// Connect to repo 'myRepo', branch 'master'.
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
