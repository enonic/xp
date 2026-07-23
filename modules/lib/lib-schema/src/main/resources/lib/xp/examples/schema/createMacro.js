var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `kind: "Macro"
title: "My Macro"
form:
- type: "Double"
  name: "input"
  label: "Input"
  occurrences:
    min: 0
    max: 1
config:
  provider: "myprovider"`;

// BEGIN
// Create dynamic macro.
var result = schemaLib.createMacro({
    key: 'myapp:mymacro',
    resource

});

log.info('Created macro: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mymacro',
    name: 'mymacro',
    title: 'My Macro',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: `kind: "Macro"
title: "My Macro"
form:
- type: "Double"
  name: "input"
  label: "Input"
  occurrences:
    min: 0
    max: 1
config:
  provider: "myprovider"`,
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
    config: {
        'provider': 'myprovider'
    }
}, result);
