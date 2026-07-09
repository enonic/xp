var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// List all namespaces.
var result = schemaLib.listNamespaces();

log.info('Found ' + result.length + ' namespaces');

// END


assert.assertJsonEquals([
    {
        key: 'myapp1',
        description: 'My namespace 1'
    },
    {
        key: 'myapp2'
    }
], result);
