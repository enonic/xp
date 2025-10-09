var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `x:
- name: "myapp1:menu-item"
  optional: false
  allowContentTypes: "mycontent"
- name: "myapp2:my-meta-mixin"
  optional: false
form:
- type: "TextLine"
  name: "some-name"
  label: "Textline"
  occurrences:
    minimum: 0
    maximum: 1`;


// BEGIN
// Update virtual styles.
var result = schemaLib.updateSite({
    application: 'myapp',
    resource

});

log.info('Updated site: ' + result.application);

// END


assert.assertJsonEquals({
    application: 'myapp',
    resource: `x:
- name: "myapp1:menu-item"
  optional: false
  allowContentTypes: "mycontent"
- name: "myapp2:my-meta-mixin"
  optional: false
form:
- type: "TextLine"
  name: "some-name"
  label: "Textline"
  occurrences:
    minimum: 0
    maximum: 1`,
    modifiedTime: '2021-09-25T10:00:00Z',
    form: [
        {
            'formItemType': 'Input',
            'name': 'some-name',
            'label': 'Textline',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        }
    ],
    xDataMappings: [
        {
            'name': 'myapp1:menu-item',
            'optional': false,
            'allowContentTypes': 'mycontent'
        },
        {
            'name': 'myapp2:my-meta-mixin',
            'optional': false
        }
    ]
}, result);

