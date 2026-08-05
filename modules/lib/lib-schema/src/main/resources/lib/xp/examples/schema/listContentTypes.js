var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic content types.
var result = schemaLib.listContentTypes({
    application: 'myapp'
});

log.info('Fetched content types: ' + result.map((type) => type.name).join(','));

// END


assert.assertJsonEquals([
    {
        name: 'myapp:type1',
        title: 'My type display name',
        description: 'My type description',
        modifiedTime: '2010-01-01T10:00:00Z',
        resource: 'kind: "ContentType"\n' +
                  'superType: "base:structured"\n' +
                  'title: "My type display name"\n' +
                  'description: "My type description"\n' +
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
        allowChildContentType: [],
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
    },
    {
        name: 'myapp:type2',
        title: 'My type display name 2',
        description: 'My type description 2',
        modifiedTime: '2012-01-01T10:00:00Z',
        resource: 'kind: "ContentType"\n' +
                  'superType: "media:archive"\n' +
                  'title: "My type display name 2"\n' +
                  'description: "My type description 2"\n',
        type: 'CONTENT_TYPE',
        superType: 'media:archive',
        abstract: false,
        final: true,
        allowChildContent: true,
        allowChildContentType: [],
        form: [],
        config: {}
    }
], result);

