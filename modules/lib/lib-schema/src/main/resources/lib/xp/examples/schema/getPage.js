var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual part.
var result = schemaLib.getComponent({
    key: 'myapp:mypage',
    type: 'PAGE'
});

log.info('Fetched page: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypage',
    displayName: 'News page',
    description: 'My news page',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/site/pages/mypage',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    resource: '<page><some-data></some-data></page>',
    type: 'PAGE',
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
    config: {},
    regions: [
        'region-one'
    ]
}, result);

