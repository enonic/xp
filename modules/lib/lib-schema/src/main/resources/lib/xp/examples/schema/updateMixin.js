var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `displayName: "Virtual Mixin"
description: "Mixin description"
form:
- type: "TextLine"
  name: "text2"
  label: "Text 2"
  occurrences:
    minimum: 0
    maximum: 1
- type: "FormFragment"
  name: "myapp:inline"
`;

// BEGIN
// Update virtual mixin.
var result = schemaLib.updateSchema({
    name: 'myapp:mytype',
    type: 'MIXIN',
    resource

});

log.info('Updated mixin: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mytype',
    displayName: 'Virtual Mixin',
    description: 'Mixin description',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'displayName: \"Virtual Mixin\"\ndescription: \"Mixin description\"\nform:\n- type: \"TextLine\"\n  name: \"text2\"\n  label: \"Text 2\"\n  occurrences:\n    minimum: 0\n    maximum: 1\n- type: \"FormFragment\"\n  name: \"myapp:inline\"\n',
    type: 'MIXIN',
    form: [
        {
            'formItemType': 'Input',
            'name': 'text2',
            'label': 'Text 2',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        },
        {
            'formItemType': 'FormFragment',
            'name': 'myapp:inline'
        }
    ]
}, result);

