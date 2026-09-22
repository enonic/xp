var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

exports.getMissingIcon = function () {
    assert.assertNull(schemaLib.getMacroIcon({
        key: 'myapp:mymacro'
    }));
};
