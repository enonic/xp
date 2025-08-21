var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual parts.
var result = schemaLib.listComponents({
    application: 'myapp',
    type: 'PART'
});

log.info('Fetched parts: ' + result.map((part) => part.key).join(','));

// END


assert.assertJsonEquals([
    {
        key: 'myapp:part1',
        displayName: 'News part',
        description: 'My news part',
        descriptionI18nKey: 'key.description',
        componentPath: 'myapp:/site/parts/part1',
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
    },
    {
        key: 'myapp:part2',
        displayName: 'Other part',
        componentPath: 'myapp:/site/parts/part2',
        modifiedTime: '2022-02-25T10:44:33.170079900Z',
        resource: '<part><some-other-data></some-other-data></part>',
        type: 'PART',
        form: [],
        config: {}
    }
], result);

