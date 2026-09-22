var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic i18n phrases.
var result = schemaLib.deletePhrases({
    application: 'myapp',
    name: 'phrases_en'
});

if (result) {
    log.info('Deleted phrases: phrases_en');
} else {
    log.info('Phrases deletion failed: phrases_en');
}

// END


assert.assertTrue(result);
