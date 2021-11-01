var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Restore content by path.
var result1 = contentLib.restore({
    content: '/path/to/mycontent',
});

log.info('Restored content ids: ' + result1.join(','));

// END

// BEGIN
// Restore content by id.
var result2 = contentLib.restore({
    content: 'my-content-id'
});

log.info('Restored content ids: ' + result2.join(','));
// END

// BEGIN
// Restore content by id to custom path.
var result3 = contentLib.restore({
    content: 'my-content-id',
    path: '/custom-parent'
});

log.info('Restored content ids: ' + result3.join(','));
// END

assert.assertEquals('my-content-id', result3[0]);

