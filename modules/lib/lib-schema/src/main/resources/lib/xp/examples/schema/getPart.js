var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic part.
var result = schemaLib.getPart({
    key: 'myapp:mypart'
});

log.info('Fetched part: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypart',
    title: 'News part',
    description: 'My news part',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/cms/parts/mypart',
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
}, result);

