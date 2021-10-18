var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Archive content by path.
var result1 = contentLib.archive({
    content: '/my-site/my-content-name',
});

log.info('Archived content ids: ' + result.join(','));

// END

// BEGIN
// Archive content by id.
var result2 = contentLib.archive({
    content: 'my-content-id'
});

log.info('Archived content ids: ' + result2.join(','));
// END


assert.assertEquals('my-content-id', result2[0]);
