var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic pages.
var result = schemaLib.listPages({
    application: 'myapp'
});

log.info('Fetched pages: ' + result.map((page) => page.key).join(','));

// END


assert.assertJsonEquals([
    {
        key: 'myapp:page1',
        title: 'News page',
        description: 'My news page',
        descriptionI18nKey: 'key.description',
        componentPath: 'myapp:/cms/pages/page1',
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
    },
    {
        key: 'myapp:page2',
        title: 'Other page',
        componentPath: 'myapp:/cms/pages/page2',
        modifiedTime: '2022-02-25T10:44:33.170079900Z',
        resource: 'kind: "Page"\n' +
                  'title: "Other page"\n' +
                  'regions:\n' +
                  '- "region-two"\n',
        type: 'PAGE',
        form: [],
        config: {},
        regions: [
            'region-two'
        ]
    }
], result);
