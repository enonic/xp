var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `displayName: "Virtual Mixin"
description: "Mixin description"
form:
- type: "TextLine"
  name: "label"
  label: "Label"
  occurrences:
    min: 0
    max: 2`;

// BEGIN
// Create virtual mixin.
var result = schemaLib.createSchema({
    name: 'myapp:mydata',
    type: 'MIXIN',
    resource

});

log.info('Created mixin: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mydata',
    displayName: 'Virtual Mixin',
    description: 'Mixin description',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'displayName: "Virtual Mixin"\n' +
              'description: "Mixin description"\n' +
              'form:\n' +
              '- type: "TextLine"\n' +
              '  name: "label"\n' +
              '  label: "Label"\n' +
              '  occurrences:\n' +
              '    min: 0\n' +
              '    max: 2',
    type: 'MIXIN',
    form: [
        {
            'formItemType': 'Input',
            'name': 'label',
            'label': 'Label',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 2,
                'minimum': 0
            },
            'config': {}
        }
    ]
}, result);

