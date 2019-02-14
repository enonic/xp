var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "com.enonic.cms.default",
    branch: "master"
});

// BEGIN
// Move and rename content by path.
var content3 = repo.move({
    source: '/my-name',
    target: '/content/my-site/folder/new-name'
});

log.info('New path: ' + content3._path); // '/content/my-site/folder/new-name'
// END

assert.assertEquals('/content/my-site/folder/new-name', content3._path);
