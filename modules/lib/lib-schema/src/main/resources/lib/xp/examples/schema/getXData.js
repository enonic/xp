var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual x-data type.
var result = schemaLib.getSchema({
    name: 'myapp:mydata',
    type: 'MIXIN'
});

log.info('Fetched x-data: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'media:cameraInfo',
    displayName: 'Photo Info',
    displayNameI18nKey: 'media.cameraInfo.displayName',
    modifiedTime: '1970-01-06T03:07:14.242Z',
    resource: '<x-data><some-data></some-data></x-data>',
    type: 'MIXIN',
    form: []
}, result);

