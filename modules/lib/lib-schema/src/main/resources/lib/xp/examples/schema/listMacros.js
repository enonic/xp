var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual macros of an application.
var result = schemaLib.listMacros({
    application: 'myapp'
});

log.info('Fetched macros: ' + result.length);

// END


assert.assertJsonEquals([
    {
        key: 'myapp:mymacro',
        name: 'mymacro',
        title: 'My Macro',
        modifiedTime: '2021-02-25T10:44:33.170079900Z',
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
    }
], result);
