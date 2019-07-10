var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.existsById = function () {

    var result = contentLib.exist({
        key: '123456',
    });

    assert.assertEquals(true, result);
};

exports.emptyKey = function () {

    try {
        var result = contentLib.exist({
            key: '',
        });
    } catch (e) {
        assert.assertEquals('\'key\' param is empty', e.message);
    }

};
