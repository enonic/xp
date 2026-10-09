var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic i18n phrases.
var result = schemaLib.getPhrases({
    application: 'myapp',
    name: 'phrases_en'
});

log.info('Fetched phrases: ' + result.name);

// END


assert.assertJsonEquals({
    application: 'myapp',
    name: 'phrases_en',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'action.save=Save'
}, result);
