const nodeLib = require('/lib/xp/node');
const assert = require('/lib/xp/testing');

const repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// BEGIN
// Fetch first page
let result = repo.getVersions({
    key: 'nodeId',
    count: 2
});

log.info('Total versions: %s', result.total);

// Fetch next page using cursor
if (result.cursor) {
    let nextPage = repo.getVersions({
        key: 'nodeId',
        count: 2,
        cursor: result.cursor
    });
}
// END

let expected = {
    'total': 40,
    'count': 2,
    'cursor': 'eyJ0cyI6MTAwMDAwMCwiaWQiOiJub2RlVmVyc2lvbk9sZCJ9',
    'hits': [
        {
            'versionId': 'nodeVersionNew',
            'nodeId': 'nodeId1',
            'nodePath': '/',
            'timestamp': '1970-01-01T00:16:40Z'
        },
        {
            'versionId': 'nodeVersionOld',
            'nodeId': 'nodeId1',
            'nodePath': '/',
            'timestamp': '1970-01-01T00:08:20Z'
        }
    ]
};

assert.assertJsonEquals(expected, result);
