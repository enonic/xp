var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `displayName:
  text: "Virtual Layout"
  i18n: "key.display-name"
description:
  text: "My Layout Description"
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
    min: 0
    max: 1
- type: "ItemSet"
  name: "myFormItemSet"
  label: "My form item set"
  occurrences:
    min: 0
    max: 1
  items:
  - type: "TextLine"
    name: "myTextLine"
    label: "My text line"
    occurrences:
      min: 1
      max: 1
  - type: "TextLine"
    name: "myCustomInput"
    label: "My custom input"
    occurrences:
      min: 0
      max: 1
regions:
- "header"
- "main"
- "footer"`;

// BEGIN
// Update virtual layout.
var result = schemaLib.updateComponent({
    key: 'myapp:mylayout',
    type: 'LAYOUT',
    resource

});

log.info('Updated layout: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mylayout',
    displayName: 'Virtual Layout',
    displayNameI18nKey: 'key.display-name',
    description: 'My Layout Description',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/cms/layouts/mylayout',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'displayName:\n' +
              '  text: "Virtual Layout"\n' +
              '  i18n: "key.display-name"\n' +
              'description:\n' +
              '  text: "My Layout Description"\n' +
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
              '    min: 0\n' +
              '    max: 1\n' +
              '- type: "ItemSet"\n' +
              '  name: "myFormItemSet"\n' +
              '  label: "My form item set"\n' +
              '  occurrences:\n' +
              '    min: 0\n' +
              '    max: 1\n' +
              '  items:\n' +
              '  - type: "TextLine"\n' +
              '    name: "myTextLine"\n' +
              '    label: "My text line"\n' +
              '    occurrences:\n' +
              '      min: 1\n' +
              '      max: 1\n' +
              '  - type: "TextLine"\n' +
              '    name: "myCustomInput"\n' +
              '    label: "My custom input"\n' +
              '    occurrences:\n' +
              '      min: 0\n' +
              '      max: 1\n' +
              'regions:\n' +
              '- "header"\n' +
              '- "main"\n' +
              '- "footer"',
    type: 'LAYOUT',
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
        },
        {
            'formItemType': 'ItemSet',
            'name': 'myFormItemSet',
            'label': 'My form item set',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'items': [
                {
                    'formItemType': 'Input',
                    'name': 'myTextLine',
                    'label': 'My text line',
                    'inputType': 'TextLine',
                    'occurrences': {
                        'maximum': 1,
                        'minimum': 1
                    },
                    'config': {}
                },
                {
                    'formItemType': 'Input',
                    'name': 'myCustomInput',
                    'label': 'My custom input',
                    'inputType': 'TextLine',
                    'occurrences': {
                        'maximum': 1,
                        'minimum': 0
                    },
                    'config': {}
                }
            ]
        }
    ],
    config: {},
    regions: [
        'header',
        'main',
        'footer'
    ]
}, result);

