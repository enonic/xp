var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual content types.
var result = schemaLib.listSchemas({
    application: 'myapp',
    type: 'CONTENT_TYPE'
});

log.info('Fetched content types: ' + result.map((type) => type.key).join(','));

// END


assert.assertJsonEquals([
    {
        name: 'myapp:type1',
        displayName: 'My type display name',
        description: 'My type description',
        modifiedTime: '2010-01-01T10:00:00Z',
        resource: '<content-type><some-data></some-data></content-type>',
        type: 'CONTENT_TYPE',
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
                                },
                                'config': {}
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
        displayName: 'My type display name 2',
        description: 'My type description 2',
        modifiedTime: '2012-01-01T10:00:00Z',
        resource: '<content-type><some-other-data></some-other-data></content-type>',
        type: 'CONTENT_TYPE',
        form: [],
        config: {}
    }
], result);

