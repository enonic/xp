const contentLib = require('/lib/xp/content');
const assert = require('/lib/xp/testing');

// BEGIN
// Fetch active versions for draft and master branches
let result = contentLib.getActiveVersions({
    key: 'contentid',
    branches: ['draft', 'master']
});

log.info('Draft version: %s', result.draft.versionId);
log.info('Master version: %s', result.master.versionId);
// END

let expected = {
    'draft': {
        'versionId': 'draftversion',
        'contentId': 'contentid',
        'path': '/my-content',
        'timestamp': '2024-01-01T00:00:00Z',
        'actions': [
            {
                'operation': 'content.publish',
                'editorial': 'editorial-version',
                'user': 'user:system:admin',
                'opTime': '2024-01-01T00:00:00Z'
            }
        ]
    },
    'master': {
        'versionId': 'masterversion',
        'contentId': 'contentid',
        'path': '/my-content',
        'timestamp': '2023-12-01T00:00:00Z'
    }
};

assert.assertJsonEquals(expected, result);
