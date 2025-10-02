var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `displayName:
  text: "Virtual Page"
  i18n: "key.display-name"
description:
  text: "My Page Description"
  i18n: "key.description"
form:
- type: "Double"
  name: "pause"
  label:
    text: "Pause parameter"
    i18n: "key1.label"
  helpText:
    text: "key1.help-text"
    i18n: "key1.help-text"
  occurrences:
    minimum: 0
    maximum: 1
regions:
- "header"
- "main"
- "footer"`;

// BEGIN
// Create virtual page.
var result = schemaLib.createComponent({
    key: 'myapp:mypage',
    type: 'PAGE',
    resource

});

log.info('Created page: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypage',
    displayName: 'Virtual Page',
    displayNameI18nKey: 'key.display-name',
    description: 'My Page Description',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/site/pages/mypage',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'displayName:\n' +
              '  text: "Virtual Page"\n' +
              '  i18n: "key.display-name"\n' +
              'description:\n' +
              '  text: "My Page Description"\n' +
              '  i18n: "key.description"\n' +
              'form:\n' +
              '- type: "Double"\n' +
              '  name: "pause"\n' +
              '  label:\n' +
              '    text: "Pause parameter"\n' +
              '    i18n: "key1.label"\n' +
              '  helpText:\n' +
              '    text: "key1.help-text"\n' +
              '    i18n: "key1.help-text"\n' +
              '  occurrences:\n' +
              '    minimum: 0\n' +
              '    maximum: 1\n' +
              'regions:\n' +
              '- "header"\n' +
              '- "main"\n' +
              '- "footer"',
    type: 'PAGE',
    form: [
        {
            'formItemType': 'Input',
            'name': 'pause',
            'label': 'Pause parameter',
            'helpText': 'key1.help-text',
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
        'header',
        'main',
        'footer'
    ]
}, result);

