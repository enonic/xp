var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic form fragment type.
var result = schemaLib.getFormFragment({
    name: 'myapp:myFormFragment'
});

log.info('Fetched form fragment: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:myFormFragment',
    title: 'My FormFragment display name',
    description: 'My FormFragment description',
    createdTime: '2009-01-01T10:00:00Z',
    creator: 'user:system:anonymous',
    modifiedTime: '2010-01-01T10:00:00Z',
    resource: 'kind: "FormFragment"\n' +
              'title: "My FormFragment display name"\n' +
              'description: "My FormFragment description"\n' +
              'form:\n' +
              '- type: "TextLine"\n' +
              '  name: "inputToBeMixedIn"\n' +
              '  label: "Mixed in"\n',
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
            }
        }
    ]
}, result);

