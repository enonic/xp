var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Update dynamic i18n phrases.
var result = schemaLib.updatePhrases({
    application: 'myapp',
    name: 'phrases_en',
    resource: 'action.save=Save changes\naction.delete=Delete'
});

log.info('Updated phrases: ' + result.name);

// END


assert.assertJsonEquals({
    application: 'myapp',
    name: 'phrases_en',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'action.save=Save changes\naction.delete=Delete'
}, result);
