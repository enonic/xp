var contentLib = require('/lib/xp/content');

/* global log*/

// BEGIN
// Restore content by path.
var result1 = contentLib.restore({
    content: '/my-content-name',
});

log.info('Restored content ids: ' + result.join(','));

// END

// BEGIN
// Restore content by id.
var result2 = contentLib.archive({
    content: 'my-content-id'
});

log.info('Restored content ids: ' + result2.join(','));
// END

// BEGIN
// Restore content by id to custom path.
var result3 = contentLib.archive({
    content: 'my-content-id',
    path: '/custom-parent'
});

log.info('Restored content ids: ' + result2.join(','));
// END
