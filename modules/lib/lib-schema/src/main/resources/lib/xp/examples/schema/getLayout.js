var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual part.
var result = schemaLib.getComponent({
    key: 'myapp:mylayout',
    type: 'LAYOUT'
});

log.info('Fetched layout: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mylayout',
    displayName: 'News layout',
    description: 'My news layout',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/cms/layouts/mylayout',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    resource: '<layout><some-data></some-data></layout>',
    type: 'LAYOUT',
    form: [
        {
            'formItemType': 'Input',
            'name': 'width',
            'label': 'width',
            'inputType': 'Double',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            }
        }
    ],
    config: {},
    regions: [
        'region-one'
    ]
}, result);

