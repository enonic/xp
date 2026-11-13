var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

exports.createInvalidStyles = function () {
    assert.assertThrows(() => schemaLib.createOrUpdateStyles({
        application: 'myapp',
        resource: `<?xml version="1.0" encoding="UTF-8"?>
        <styles xmlns="urn:enonic:xp:model:1.0">
            <wrong-tag></wrong-tag>
        </styles>`
    }));
};

exports.createStylesWithoutResource = function () {
    assert.assertThrows(() => schemaLib.createOrUpdateStyles({
        application: 'myapp'
    }));
};