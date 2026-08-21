var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic page.
var result = schemaLib.getPage({
    key: 'myapp:mypage'
});

log.info('Fetched page: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypage',
    title: 'News page',
    description: 'My news page',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/cms/pages/mypage',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    resource: 'kind: "Page"\n' +
              'title: "News page"\n' +
              'description:\n' +
              '  text: "My news page"\n' +
              '  i18n: "key.description"\n' +
              'form:\n' +
              '- type: "Double"\n' +
              '  name: "width"\n' +
              '  label: "width"\n' +
              'regions:\n' +
              '- "region-one"\n',
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
            }
        }
    ],
    config: {},
    regions: [
        'region-one'
    ]
}, result);

