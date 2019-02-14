var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.moveSameParentPath = function () {

    var result = contentLib.move({
        source: '/my-site/my-content-name',
        target: '/my-site/new-name'
    });

    assert.assertEquals('/my-site/new-name', result._path);
};
