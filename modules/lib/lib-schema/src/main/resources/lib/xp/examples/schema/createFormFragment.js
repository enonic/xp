var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `kind: "FormFragment"
title: "Dynamic FormFragment"
description: "FormFragment description"
form:
- type: "TextLine"
  name: "text2"
  label: "Text 2"
  occurrences:
    min: 0
    max: 1
- type: "FormFragment"
  name: "inline"`;

// BEGIN
// Create dynamic form fragment.
var result = schemaLib.createFormFragment({
    name: 'myapp:mytype',
    resource

});

log.info('Created form fragment: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mytype',
    title: 'Dynamic FormFragment',
    description: 'FormFragment description',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'kind: "FormFragment"\n' +
              'title: "Dynamic FormFragment"\n' +
              'description: "FormFragment description"\n' +
              'form:\n' +
              '- type: "TextLine"\n' +
              '  name: "text2"\n' +
              '  label: "Text 2"\n' +
              '  occurrences:\n' +
              '    min: 0\n' +
              '    max: 1\n' +
              '- type: "FormFragment"\n' +
              '  name: "inline"',
    type: 'FORM_FRAGMENT',
    form: [
        {
            'formItemType': 'Input',
            'name': 'text2',
            'label': 'Text 2',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            }
        }, {
            formItemType: 'FormFragment',
            name: 'myapp:inline'
        }
    ],

}, result);

