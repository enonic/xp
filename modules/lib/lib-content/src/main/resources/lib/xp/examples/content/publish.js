var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Publish content by path or key
var result = contentLib.publish({
    keys: ['/mysite/somepage', '79e21db0-5b43-45ce-b58c-6e1c420b22bd'],
    sourceBranch: 'draft',
    targetBranch: 'master',
    schedule: {
        from: new Date().toISOString(),
        to: '2018-01-01T13:37:00.000Z'
    },
    includeDependencies: false,
    message: 'My first publish',
});

if (result) {
    log.info('Pushed ' + result.pushedContents.length + ' content.');
    log.info('Content that failed operation: ' + result.failedContents.length);
} else {
    log.info('Operation failed.');
}
// END

// BEGIN
// Content published.
var expected = {
    'pushedContents': [
        'd7ad428b-eae2-4ff1-9427-e8e8a8a3ab23',
        '9f5b0db0-38f9-4e81-b92e-116f25476b1c',
        'e1f57280-d672-4cd8-b674-98e26e5b69ae'
    ],
    'failedContents': [
        '79e21db0-5b43-45ce-b58c-6e1c420b22bd'
    ]
};
// END

assert.assertJsonEquals(expected, result);
