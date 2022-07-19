var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual content type.
var result = schemaLib.getSchema({
    name: 'myapp:mytype',
    type: 'CONTENT_TYPE'
});

log.info('Fetched content type: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mytype',
    displayName: 'My type display name',
    description: 'My type description',
    modifiedTime: '2010-01-01T10:00:00Z',
    resource: '<content-type><some-data></some-data></content-type>',
    type: 'CONTENT_TYPE'
}, result);

