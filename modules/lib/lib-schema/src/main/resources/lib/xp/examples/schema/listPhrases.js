var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic i18n phrases of an application.
var result = schemaLib.listPhrases({
    application: 'myapp'
});

log.info('Fetched ' + result.length + ' phrases resources');

// END


assert.assertJsonEquals([
    {
        application: 'myapp',
        name: 'phrases',
        modifiedTime: '2021-09-25T10:00:00Z',
        resource: 'action.save=Save'
    },
    {
        application: 'myapp',
        name: 'phrases_no',
        modifiedTime: '2021-09-25T10:00:00Z',
        resource: 'action.save=Lagre'
    }
], result);
