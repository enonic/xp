var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Rename content by path. Keeps same parent.
var content1 = contentLib.move({
    source: '/my-site/my-content-name',
    target: 'new-name'
});

log.info('New path: ' + content1._path); // '/my-site/new-name'
// END

// BEGIN
// Move content by path. New parent path, keeps same name.
var content2 = contentLib.move({
    source: '/my-site/my-content-name',
    target: '/my-site/folder/'
});

log.info('New path: ' + content2._path); // '/my-site/folder/my-content-name'
// END

// BEGIN
// Move content by id to new path. New parent path, keeps same name.
var content3 = contentLib.move({
    source: '8d933461-ede7-4dd5-80da-cb7de0cd7bba',
    target: '/my-site/folder/'
});

log.info('New path: ' + content3._path); // '/my-site/folder/my-content-name'
// END

// BEGIN
// Move and rename content.
var content4 = contentLib.move({
    source: '/my-site/my-content-name',
    target: '/my-site/folder/new-name'
});

log.info('New path: ' + content4._path); // '/my-site/folder/new-name'
// END

// BEGIN
// Handle error if target already exists.
try {
    var content5 = contentLib.move({
        source: '/my-site/my-content-name',
        target: '/my-site/folder/existing-content'
    });

} catch (e) {
    if (e.code == 'contentAlreadyExists') {
        log.error('There is already a content in the target specified');
    } else {
        log.error('Unexpected error: ' + e.message);
    }
}
// END


assert.assertEquals('/my-site/new-name', content1._path);
assert.assertEquals('/my-site/folder/my-content-name', content2._path);
assert.assertEquals('/my-site/folder/my-content-name', content3._path);
