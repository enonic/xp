var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic layouts.
var result = schemaLib.listLayouts({
    application: 'myapp'
});

log.info('Fetched layouts: ' + result.map((layout) => layout.key).join(','));

// END


assert.assertJsonEquals([
    {
        key: 'myapp:layout1',
        title: 'News layout',
        description: 'My news layout',
        descriptionI18nKey: 'key.description',
        componentPath: 'myapp:/cms/layouts/layout1',
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
    },
    {
        key: 'myapp:layout2',
        title: 'Other layout',
        componentPath: 'myapp:/cms/layouts/layout2',
        modifiedTime: '2022-02-25T10:44:33.170079900Z',
        resource: 'kind: "Layout"\n' +
                  'title: "Other layout"\n' +
                  'regions:\n' +
                  '- "region-two"\n',
        type: 'LAYOUT',
        form: [],
        config: {},
        regions: [
            'region-two'
        ]
    }
], result);
