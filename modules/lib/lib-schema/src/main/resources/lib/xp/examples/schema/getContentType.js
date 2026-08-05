var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic content type.
var result = schemaLib.getContentType({
    name: 'myapp:mytype'
});

log.info('Fetched content type: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mytype',
    title: 'My type display name',
    description: 'My type description',
    modifiedTime: '2010-01-01T10:00:00Z',
    resource: 'kind: "ContentType"\n' +
              'superType: "base:structured"\n' +
              'title: "My type display name"\n' +
              'description: "My type description"\n' +
              'allowChildContentType:\n' +
              '- "myapp:other-type"\n' +
              '- "myapp:another-type"\n' +
              'displayNamePlaceholder: "Enter a display name"\n' +
              'displayNameExpression: "${title}"\n' +
              'form:\n' +
              '- type: "FieldSet"\n' +
              '  label: "My layout"\n' +
              '  items:\n' +
              '  - type: "ItemSet"\n' +
              '    name: "mySet"\n' +
              '    occurrences:\n' +
              '      min: 1\n' +
              '      max: 1\n' +
              '    items:\n' +
              '    - type: "TextLine"\n' +
              '      name: "myInput"\n' +
              '      label: "Input"\n',
    type: 'CONTENT_TYPE',
    superType: 'base:structured',
    abstract: false,
    final: true,
    allowChildContent: true,
    allowChildContentType: ['myapp:other-type', 'myapp:another-type'],
    displayNamePlaceholder: 'Enter a display name',
    displayNameExpression: '${title}',
    form: [
        {
            'formItemType': 'Layout',
            'label': 'My layout',
            'items': [
                {
                    'formItemType': 'ItemSet',
                    'name': 'mySet',
                    'occurrences': {
                        'maximum': 1,
                        'minimum': 1
                    },
                    'items': [
                        {
                            'formItemType': 'Input',
                            'name': 'myInput',
                            'label': 'Input',
                            'inputType': 'TextLine',
                            'occurrences': {
                                'maximum': 1,
                                'minimum': 0
                            }
                        }
                    ]
                }
            ]
        }
    ],
    config: {}
}, result);

