var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual mixin type.
var result = schemaLib.getSchema({
    name: 'mymixin',
    type: 'FORM_FRAGMENT'
});

log.info('Fetched mixin: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mymixin',
    displayName: 'My mixin display name',
    description: 'My mixin description',
    createdTime: '2009-01-01T10:00:00Z',
    creator: 'user:system:anonymous',
    modifiedTime: '2010-01-01T10:00:00Z',
    resource: '<mixin><some-data></some-data></mixin>',
    type: 'FORM_FRAGMENT',
    form: [
        {
            'formItemType': 'Input',
            'name': 'inputToBeMixedIn',
            'label': 'Mixed in',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        }
    ]
}, result);

