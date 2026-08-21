var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete dynamic i18n phrases.

var params = {
    application: 'myapp',
    name: 'phrases_en'
};

var result = schemaLib.deletePhrases(params);


if (result) {
    log.info('Deleted phrases: ' + params.name);
} else {
    log.info('Phrases deletion failed: ' + params.name);
}

// END


assert.assertTrue(result);
