var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual site.
var result = schemaLib.getSite({
    application: 'myapp'
});

log.info('Fetched site: myapp');

// END


assert.assertJsonEquals({
    application: 'myapp',
    resource: '<site><some-data></some-data></site>',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    form: [
        {
            'formItemType': 'Input',
            'name': 'input',
            'label': 'Input',
            'inputType': 'Double',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        }
    ],
    xDataMappings: [
        {
            'name': 'myapplication:my',
            'optional': false
        }
    ]
}, result);

