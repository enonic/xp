var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic parts.
var result = schemaLib.listParts({
    application: 'myapp'
});

log.info('Fetched parts: ' + result.map((part) => part.key).join(','));

// END


assert.assertJsonEquals([
    {
        key: 'myapp:part1',
        title: 'News part',
        description: 'My news part',
        descriptionI18nKey: 'key.description',
        componentPath: 'myapp:/cms/parts/part1',
        modifiedTime: '2021-02-25T10:44:33.170079900Z',
        resource: 'kind: "Part"\n' +
                  'title: "News part"\n' +
                  'description:\n' +
                  '  text: "My news part"\n' +
                  '  i18n: "key.description"\n' +
                  'form:\n' +
                  '- type: "Double"\n' +
                  '  name: "width"\n' +
                  '  label: "width"\n',
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
                }
            }
        ],
        config: {}
    },
    {
        key: 'myapp:part2',
        title: 'Other part',
        componentPath: 'myapp:/cms/parts/part2',
        modifiedTime: '2022-02-25T10:44:33.170079900Z',
        resource: 'kind: "Part"\n' +
                  'title: "Other part"\n',
        type: 'PART',
        form: [],
        config: {}
    }
], result);

