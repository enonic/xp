var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic layout.
var result = schemaLib.getLayout({
    key: 'myapp:mylayout'
});

log.info('Fetched layout: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mylayout',
    title: 'News layout',
    description: 'My news layout',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/cms/layouts/mylayout',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    resource: 'kind: "Layout"\n' +
              'title: "News layout"\n' +
              'description:\n' +
              '  text: "My news layout"\n' +
              '  i18n: "key.description"\n' +
              'form:\n' +
              '- type: "Double"\n' +
              '  name: "width"\n' +
              '  label: "width"\n' +
              'regions:\n' +
              '- "region-one"\n',
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

