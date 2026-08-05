var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.updateInvalidComponent = function () {
    assert.assertThrows(() => schemaLib.updatePart({
        key: 'myapp:mydata',
        resource: `<?xml version="1.0" encoding="UTF-8"?>
        <part xmlns="urn:enonic:xp:model:1.0">
                    <wrong-tag></wrong-tag>
        </part>`
    }));
};