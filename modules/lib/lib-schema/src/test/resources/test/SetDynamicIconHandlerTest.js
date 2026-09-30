var assert = require('/lib/xp/testing');

/* global testInstance, __ */

// the JS API only exposes the types that have icons; the handler itself rejects any other type
exports.setIconOfUnsupportedType = function () {
    assert.assertThrows(() => {
        var bean = __.newBean('com.enonic.xp.lib.schema.SetDynamicIconHandler');
        bean.setName('myapp:mypage');
        bean.setType('PAGE');
        bean.setData(testInstance.createByteSource('<svg/>'));
        bean.setMimeType('image/svg+xml');
        bean.execute();
    });
};
