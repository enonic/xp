var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `superType: "base:structured"
abstract: false
final: true
allowChildContent: true
displayName: "My Tag"
description: "My description"
form:
- type: "Tag"
  name: "tag_unlimited"
  label: "Tag, unlimited occurrences"
  helpText: "Some help text"
  occurrences:
    min: 0
    max: 0
  config:
    context: "true"
config:
  alignment: "bottom"
`;

// BEGIN
// Create virtual content type.
var result = schemaLib.createSchema({
    name: 'myapp:mytype',
    type: 'CONTENT_TYPE',
    resource

});

log.info('Created content type: ' + result.name);

// END

assert.assertJsonEquals({
    name: 'myapp:mytype',
    displayName: 'My Tag',
    description: 'My description',
    createdTime: '2021-09-25T10:00:00Z',
    creator: 'user:system:anonymous',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'superType: \"base:structured\"\nabstract: false\nfinal: true\nallowChildContent: true\ndisplayName: \"My Tag\"\ndescription: \"My description\"\nform:\n- type: \"Tag\"\n  name: \"tag_unlimited\"\n  label: \"Tag, unlimited occurrences\"\n  helpText: \"Some help text\"\n  occurrences:\n    min: 0\n    max: 0\n  config:\n    context: \"true\"\nconfig:\n  alignment: \"bottom\"\n',
    type: 'CONTENT_TYPE',
    icon: {
        'data': {},
        'mimeType': 'image/png',
        'modifiedTime': '2016-01-01T12:00:00Z'
    },
    form: [
        {
            'formItemType': 'Input',
            'name': 'tag_unlimited',
            'label': 'Tag, unlimited occurrences',
            'helpText': 'Some help text',
            'inputType': 'Tag',
            'occurrences': {
                'maximum': 0,
                'minimum': 0
            },
            'config': {
                'context': 'true'
            }
        }
    ],
    config: {
        'alignment': 'bottom'
    }
}, result);

