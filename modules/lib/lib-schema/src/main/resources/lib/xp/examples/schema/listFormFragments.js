var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic form fragments.
var result = schemaLib.listFormFragments({
    application: 'myapp'
});

log.info('Fetched form fragments: ' + result.map((fragment) => fragment.name).join(','));

// END


assert.assertJsonEquals([
    {
        name: 'myapp:fragment1',
        title: 'My FormFragment display name',
        description: 'My FormFragment description',
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
    },
    {
        name: 'myapp:fragment2',
        title: 'Other fragment',
        modifiedTime: '2012-01-01T10:00:00Z',
        resource: 'kind: "FormFragment"\n' +
                  'title: "Other fragment"\n',
        type: 'FORM_FRAGMENT',
        form: []
    }
], result);
