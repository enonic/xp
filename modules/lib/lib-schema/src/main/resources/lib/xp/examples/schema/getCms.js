var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic CMS.
var result = schemaLib.getCms({
    application: 'myapp'
});

log.info('Fetched CMS: myapp');

// END


assert.assertJsonEquals({
    application: 'myapp',
    resource: `kind: "CMS"
mixins:
- name: "myapplication:my"
  optional: false
form:
- type: "Double"
  name: "input"
  label: "Input"
  occurrences:
    min: 0
    max: 1`,
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    form: [
        {
            'formItemType': 'Input',
            'name': 'input',
            'label': 'Input',
            'inputType': 'Double',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            }
        }
    ],
    mixinMappings: [
        {
            'name': 'myapplication:my',
            'optional': false
        }
    ]
}, result);

