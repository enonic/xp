var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Enumerate the direct children of a content, straight from branch storage.
var result = contentLib.list({
    parent: '/path/to'
});

log.info('The content has ' + result.count + ' children');

result.hits.forEach(function (entry) {
    log.info(entry.path + ' [' + entry.id + ']');
});
// END

// BEGIN
// Result set returned.
var expected = {
    'count': 2,
    'hits': [
        {
            'id': 'id1',
            'path': '/path/to/a'
        },
        {
            'id': 'id2',
            'path': '/path/to/b'
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
