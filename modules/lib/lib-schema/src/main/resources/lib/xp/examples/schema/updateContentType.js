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
    minimum: 0
    maximum: 0`;

// BEGIN
// Update virtual content type.
var result = schemaLib.updateSchema({
    name: 'myapp:mytype',
    type: 'CONTENT_TYPE',
    resource

});

log.info('Updated content type: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mytype',
    displayName: 'My Tag',
    description: 'My description',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'superType: "base:structured"\n' +
              'abstract: false\n' +
              'final: true\n' +
              'allowChildContent: true\n' +
              'displayName: "My Tag"\n' +
              'description: "My description"\n' +
              'form:\n' +
              '- type: "Tag"\n' +
              '  name: "tag_unlimited"\n' +
              '  label: "Tag, unlimited occurrences"\n' +
              '  helpText: "Some help text"\n' +
              '  occurrences:\n' +
              '    minimum: 0\n' +
              '    maximum: 0',
    type: 'CONTENT_TYPE',
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
            'config': {}
        }
    ],
    config: {}
}, result);

