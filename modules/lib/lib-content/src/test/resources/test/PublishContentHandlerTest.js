var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
    'pushedContents': [
        'd7ad428b-eae2-4ff1-9427-e8e8a8a3ab23',
        '9f5b0db0-38f9-4e81-b92e-116f25476b1c',
        'e1f57280-d672-4cd8-b674-98e26e5b69ae'
    ],
    'failedContents': [
        '79e21db0-5b43-45ce-b58c-6e1c420b22bd'
    ]
};

var expectedLimitedJson = {
    'pushedContents': [
        'e1f57280-d672-4cd8-b674-98e26e5b69ae'
    ],
    'failedContents': []
};

exports.publishById = function () {
    var result = content.publish({
        keys: ['9f5b0db0-38f9-4e81-b92e-116f25476b1c', '45d67001-7f2b-4093-99ae-639be9fdd1f6', '79e21db0-5b43-45ce-b58c-6e1c420b22bd'],
        sourceBranch: 'master',
        targetBranch: 'draft'
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.publishByPath = function () {
    var result = content.publish({
        keys: ['/myfolder/mycontent', '/yourfolder/yourcontent'],
        sourceBranch: 'draft',
        targetBranch: 'master'
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.publishWithoutChildrenOrDependencies = function () {
    var result = content.publish({
        keys: ['e1f57280-d672-4cd8-b674-98e26e5b69ae'],
        sourceBranch: 'draft',
        targetBranch: 'master',
        excludeDescendantsOf: ['e1f57280-d672-4cd8-b674-98e26e5b69ae'],
        includeDependencies: false
    });

    assert.assertJsonEquals(expectedLimitedJson, result);
};

exports.publishWithMessage = function () {
    var result = content.publish({
        keys: ['9f5b0db0-38f9-4e81-b92e-116f25476b1c', '45d67001-7f2b-4093-99ae-639be9fdd1f6', '79e21db0-5b43-45ce-b58c-6e1c420b22bd'],
        sourceBranch: 'master',
        targetBranch: 'draft',
        message: 'My first publish',
    });

    assert.assertJsonEquals(expectedJson, result);
};

var contentNotFoundExpectedJson = {
    "pushedContents": [],
    "failedContents": [
        "/non-existing-content"
    ]
};

exports.contentNotFound = function () {
    var result = content.publish({
        keys: ['/non-existing-content'],
        sourceBranch: 'master',
        targetBranch: 'draft',
        message: 'My first publish',
    });

    assert.assertJsonEquals(contentNotFoundExpectedJson, result);
};
