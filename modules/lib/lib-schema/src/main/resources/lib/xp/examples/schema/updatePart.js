var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `displayName:
  text: "Virtual Part"
  i18n: "key.display-name"
description:
  text: "My Part Description"
  i18n: "key.description"
form:
- type: "Double"
  name: "width"
  label:
    text: "Column width"
    i18n: "key.label"
  helpText:
    text: "key.help-text"
    i18n: "key.help-text"
  occurrences:
    min: 0
    max: 1
- type: "FormFragment"
  name: "link-urls"
config:
  input:
    type: "Double"
    name: "width"
    label:
      text: "Column width"
      i18n: "key.label"
    helpText:
      i18n: "key.help-text"
                `;

// BEGIN
// Update virtual part.
var result = schemaLib.updateComponent({
    key: 'myapp:mypart', type: 'PART', resource

});

log.info('Updated part: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypart',
    displayName: 'Virtual Part',
    displayNameI18nKey: 'key.display-name',
    description: 'My Part Description',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/cms/parts/mypart',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'displayName:\n  text: \"Virtual Part\"\n  i18n: \"key.display-name\"\ndescription:\n  text: \"My Part Description\"\n  i18n: \"key.description\"\nform:\n- type: \"Double\"\n  name: \"width\"\n  label:\n    text: \"Column width\"\n    i18n: \"key.label\"\n  helpText:\n    text: \"key.help-text\"\n    i18n: \"key.help-text\"\n  occurrences:\n    min: 0\n    max: 1\n- type: \"FormFragment\"\n  name: \"link-urls\"\nconfig:\n  input:\n    type: \"Double\"\n    name: \"width\"\n    label:\n      text: \"Column width\"\n      i18n: \"key.label\"\n    helpText:\n      i18n: \"key.help-text\"\n                ',
    type: 'PART',
    form: [{
        'formItemType': 'Input',
        'name': 'width',
        'label': 'Column width',
        'helpText': 'key.help-text',
        'inputType': 'Double',
        'occurrences': {
            'maximum': 1, 'minimum': 0
        },
        'config': {},
    }, {
        formItemType: 'FormFragment',
        name: 'myapp:link-urls'
    }],
    config: {
        'input': {
            'type': 'Double',
            'name': 'width',
            'label': {
                'text': 'Column width',
                'i18n': 'key.label'
            },
            'helpText': {
                'i18n': 'key.help-text'
            }
        }
    }
}, result);

