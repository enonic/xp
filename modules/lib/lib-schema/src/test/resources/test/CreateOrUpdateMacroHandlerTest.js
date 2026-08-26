var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance */

exports.createOrUpdateMacroIcon = function () {
    var result = schemaLib.createOrUpdateMacro({
        key: 'myapp:mymacro',
        icon: {
            data: testInstance.createByteSource('<svg/>'),
            mimeType: 'image/svg+xml'
        }
    });
    assert.assertEquals('myapp:mymacro', result.key);
};

exports.createInvalidMacro = function () {
    assert.assertThrows(() => schemaLib.createOrUpdateMacro({
        key: null,
        resource: 'kind: "Macro"'
    }));
};