var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
    'total': 20,
    'count': 3,
    'hits': [
        {
            '_id': 'id1',
            '_name': 'name1',
            '_path': '/a/b/name1',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 1',
            'valid': false,
            'data': {},
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY',
                'checks': {}
            }
        },
        {
            '_id': 'id2',
            '_name': 'name2',
            '_path': '/a/b/name2',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 2',
            'valid': false,
            'data': {},
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY',
                'checks': {}
            }
        },
        {
            '_id': 'id3',
            '_name': 'name3',
            '_path': '/a/b/name3',
            'creator': 'user:system:admin',
            'modifier': 'user:system:admin',
            'createdTime': '1970-01-01T00:00:00Z',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'type': 'base:unstructured',
            'displayName': 'My Content 3',
            'valid': false,
            'data': {},
            'x': {},
            'page': {},
            'attachments': {},
            'publish': {},
            'workflow': {
                'state': 'READY',
                'checks': {}
            }
        }
    ]
};

var expectedEmptyJson = {
    'total': 0,
    'count': 0,
    'hits': []
};

exports.getChildrenById = function () {
    var result = content.getChildren({
        key: '123456'
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.getChildrenByPath = function () {
    var result = content.getChildren({
        key: '/a/b'
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.getChildrenById_notFound = function () {
    var result = content.getChildren({
        key: '123456'
    });

    assert.assertJsonEquals(expectedEmptyJson, result);
};

exports.getChildrenByPath_notFound = function () {
    var result = content.getChildren({
        key: '/a/b/mycontent'
    });

    assert.assertJsonEquals(expectedEmptyJson, result);
};

exports.getChildrenByPath_allParameters = function () {
    var result = content.getChildren({
        key: '/a/b/mycontent',
        start: 5,
        count: 3,
        sort: '_modifiedTime ASC'
    });

    assert.assertJsonEquals(expectedJson, result);
};
