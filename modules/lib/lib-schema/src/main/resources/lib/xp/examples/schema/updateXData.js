var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `displayName: "Virtual X-data"
description: "X-data description"
form:
- type: "TextLine"
  name: "label"
  label: "Label"
  occurrences:
    min: 0
    max: 2`;

// BEGIN
// Update virtual x-data.
var result = schemaLib.updateSchema({
    name: 'myapp:mydata',
    type: 'XDATA',
    resource

});

log.info('Updated x-data: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mydata',
    displayName: 'Virtual X-data',
    description: 'X-data description',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'displayName: "Virtual X-data"\n' +
              'description: "X-data description"\n' +
              'form:\n' +
              '- type: "TextLine"\n' +
              '  name: "label"\n' +
              '  label: "Label"\n' +
              '  occurrences:\n' +
              '    min: 0\n' +
              '    max: 2',
    type: 'XDATA',
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

