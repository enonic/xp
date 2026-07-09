var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.createInvalidStyles = function () {
    assert.assertThrows(() => schemaLib.createStyles({
        application: 'myapp',
        resource: `<?xml version="1.0" encoding="UTF-8"?>
        <styles xmlns="urn:enonic:xp:model:1.0">
            <wrong-tag></wrong-tag>
        </styles>`
    }));
};
