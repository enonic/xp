var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic macro.
var result = schemaLib.getMacro({
    key: 'myapp:mymacro'
});

log.info('Fetched macro: ' + result.key);

// END


assert.assertEquals('myapp:mymacro', result.key);
assert.assertEquals('mymacro', result.name);
assert.assertEquals('My Macro', result.title);
assert.assertEquals('2021-02-25T10:44:33.170079900Z', result.modifiedTime);
assert.assertEquals('image/svg+xml', result.icon.mimeType);
assert.assertNotNull(result.icon.data);
assert.assertJsonEquals([
    {
        'formItemType': 'Input',
        'name': 'input',
        'label': 'Input',
        'inputType': 'Double',
        'occurrences': {
            'maximum': 1,
            'minimum': 0
        }
    }
], result.form);
assert.assertJsonEquals({
    'provider': 'myprovider'
}, result.config);
