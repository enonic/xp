var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Query the direct children of a content. The parent can be given by path or by id.
// Without a sort expression the children come back in the child order of the parent.
var result = contentLib.query({
    parent: '/path/to',
    start: 0,
    count: 2,
    contentTypes: ['base:unstructured']
});

log.info('Found ' + result.total + ' number of contents');

for (var i = 0; i < result.hits.length; i++) {
    var content = result.hits[i];
    log.info('Content ' + content._name + ' loaded');
}
// END

// BEGIN
// Result set returned.
var expected = {
    'total': 20,
    'count': 2,
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
                'state': 'READY'
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
                'state': 'READY'
            }
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
