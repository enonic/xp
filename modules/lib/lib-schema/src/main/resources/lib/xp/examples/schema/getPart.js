var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual part.
var result = schemaLib.getComponent({
    key: 'myapp:mypart',
    type: 'PART'
});

log.info('Fetched part: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypart',
    displayName: 'News part',
    description: 'My news part',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/site/parts/mypart',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    resource: '<part><some-data></some-data></part>',
    type: 'PART',
    form: [
        {
            'formItemType': 'Input',
            'name': 'width',
            'label': 'width',
            'inputType': 'Double',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        }
    ],
    config: {}
}, result);

