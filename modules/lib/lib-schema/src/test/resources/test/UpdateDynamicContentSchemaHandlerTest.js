var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.updateInvalidContentSchema = function () {
    assert.assertThrows(() => schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'CONTENT_TYPE',
        resource: `<?xml version='1.0' encoding='UTF-8'?>
        <content-type xmlns='urn:enonic:xp:model:1.0'>
        </content-type>`
    }));
};

exports.updateInvalidContentSchemaType = function () {
    assert.assertThrows(() => schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'INVALID_TYPE',
        resource: ''
    }));
};

let resource = `superType: "base:structured"
abstract: false
final: true
allowChildContent: true
displayName: "My Tag"
description: "My description"
form:
- type: "OptionSet"
  name: "radioOptionSet"
  expanded: false
  label: "Single selection"
  occurrences:
    min: 0
    max: 1
  options:
  - name: "option_1"
    label: "Option 1"
    defaultOption: false
    items:
    - type: "TextLine"
      name: "text-input"
      label: "Name"
      helpText: "Text input"
      occurrences:
        min: 1
        max: 1
      default: "something"
    - type: "ItemSet"
      name: "minimum3"
      label: "Minimum 3"
      occurrences:
        min: 3
        max: 0
      items:
      - type: "TextLine"
        name: "label"
        label: "Label"
        occurrences:
          min: 0
          max: 1
      - type: "TextLine"
        name: "value"
        label: "Value"
        occurrences:
          min: 0
          max: 1
  - name: "option_2"
    label: "Option 2"
    defaultOption: false
  selected:
    min: 1
    max: 1
- type: "OptionSet"
  name: "checkOptionSet"
  expanded: true
  label: "Multi selection"
  occurrences:
    min: 0
    max: 1
  options:
  - name: "option_1"
    label: "Option 1"
    defaultOption: true
  - name: "option_2"
    label: "Option 2"
    defaultOption: true
    items:
    - type: "OptionSet"
      name: "nestedOptionSet"
      expanded: false
      label: "Multi selection"
      occurrences:
        min: 1
        max: 1
      options:
      - name: "option2_1"
        label: "Option 1_1"
        defaultOption: false
        items:
        - type: "TextLine"
          name: "name"
          label: "Name"
          helpText: "Text input"
          occurrences:
            min: 1
            max: 1
      - name: "option2_2"
        label: "Option 2_2"
        defaultOption: true
        items:
        - type: "Checkbox"
          name: "myCheckbox"
          label: "my-checkbox"
          occurrences:
            min: 0
            max: 1
      selected:
        min: 2
        max: 2
  - name: "option_3"
    label: "Option 3"
    defaultOption: false
    items:
    - type: "ImageSelector"
      name: "imageselector"
      label: "Image selector"
      occurrences:
        min: 1
        max: 1
      config:
        allowPath:
        - "path1"
        - "path2"
        allowContentType:
        - "mytype2"
        - "mytype"
  - name: "option_4"
    label: "Option 4"
    defaultOption: false
    items:
    - type: "Double"
      name: "double"
      label: "Double"
      occurrences:
        min: 1
        max: 1
    - type: "Long"
      name: "long"
      label: "Long"
      occurrences:
        min: 0
        max: 1
  selected:
    min: 0
    max: 3
`;

exports.updateWithForm = function () {
    let result = schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'CONTENT_TYPE',
        resource
    });

    assert.assertJsonEquals({
        'name': 'myapp:mydata',
        'displayName': 'My Tag',
        'description': 'My description',
        'createdTime': '2021-09-25T10:00:00Z',
        'modifiedTime': '2021-09-25T10:00:00Z',
        'resource': 'superType: \"base:structured\"\nabstract: false\nfinal: true\nallowChildContent: true\ndisplayName: \"My Tag\"\ndescription: \"My description\"\nform:\n- type: \"OptionSet\"\n  name: \"radioOptionSet\"\n  expanded: false\n  label: \"Single selection\"\n  occurrences:\n    min: 0\n    max: 1\n  options:\n  - name: \"option_1\"\n    label: \"Option 1\"\n    defaultOption: false\n    items:\n    - type: \"TextLine\"\n      name: \"text-input\"\n      label: \"Name\"\n      helpText: \"Text input\"\n      occurrences:\n        min: 1\n        max: 1\n      default: \"something\"\n    - type: \"ItemSet\"\n      name: \"minimum3\"\n      label: \"Minimum 3\"\n      occurrences:\n        min: 3\n        max: 0\n      items:\n      - type: \"TextLine\"\n        name: \"label\"\n        label: \"Label\"\n        occurrences:\n          min: 0\n          max: 1\n      - type: \"TextLine\"\n        name: \"value\"\n        label: \"Value\"\n        occurrences:\n          min: 0\n          max: 1\n  - name: \"option_2\"\n    label: \"Option 2\"\n    defaultOption: false\n  selected:\n    min: 1\n    max: 1\n- type: \"OptionSet\"\n  name: \"checkOptionSet\"\n  expanded: true\n  label: \"Multi selection\"\n  occurrences:\n    min: 0\n    max: 1\n  options:\n  - name: \"option_1\"\n    label: \"Option 1\"\n    defaultOption: true\n  - name: \"option_2\"\n    label: \"Option 2\"\n    defaultOption: true\n    items:\n    - type: \"OptionSet\"\n      name: \"nestedOptionSet\"\n      expanded: false\n      label: \"Multi selection\"\n      occurrences:\n        min: 1\n        max: 1\n      options:\n      - name: \"option2_1\"\n        label: \"Option 1_1\"\n        defaultOption: false\n        items:\n        - type: \"TextLine\"\n          name: \"name\"\n          label: \"Name\"\n          helpText: \"Text input\"\n          occurrences:\n            min: 1\n            max: 1\n      - name: \"option2_2\"\n        label: \"Option 2_2\"\n        defaultOption: true\n        items:\n        - type: \"Checkbox\"\n          name: \"myCheckbox\"\n          label: \"my-checkbox\"\n          occurrences:\n            min: 0\n            max: 1\n      selected:\n        min: 2\n        max: 2\n  - name: \"option_3\"\n    label: \"Option 3\"\n    defaultOption: false\n    items:\n    - type: \"ImageSelector\"\n      name: \"imageselector\"\n      label: \"Image selector\"\n      occurrences:\n        min: 1\n        max: 1\n      config:\n        allowPath:\n        - \"path1\"\n        - \"path2\"\n        allowContentType:\n        - \"mytype2\"\n        - \"mytype\"\n  - name: \"option_4\"\n    label: \"Option 4\"\n    defaultOption: false\n    items:\n    - type: \"Double\"\n      name: \"double\"\n      label: \"Double\"\n      occurrences:\n        min: 1\n        max: 1\n    - type: \"Long\"\n      name: \"long\"\n      label: \"Long\"\n      occurrences:\n        min: 0\n        max: 1\n  selected:\n    min: 0\n    max: 3\n',
        'type': 'CONTENT_TYPE',
        'form': [
            {
                'formItemType': 'OptionSet',
                'name': 'radioOptionSet',
                'label': 'Single selection',
                'expanded': false,
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'selection': {
                    'maximum': 1,
                    'minimum': 1
                },
                'options': [
                    {
                        'name': 'option_1',
                        'label': 'Option 1',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'text-input',
                                'label': 'Name',
                                'helpText': 'Text input',
                                'inputType': 'TextLine',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'default': {
                                    'value': 'something',
                                    'type': 'String'
                                },
                                'config': {}
                            },
                            {
                                'formItemType': 'ItemSet',
                                'name': 'minimum3',
                                'label': 'Minimum 3',
                                'occurrences': {
                                    'maximum': 0,
                                    'minimum': 3
                                },
                                'items': [
                                    {
                                        'formItemType': 'Input',
                                        'name': 'label',
                                        'label': 'Label',
                                        'inputType': 'TextLine',
                                        'occurrences': {
                                            'maximum': 1,
                                            'minimum': 0
                                        },
                                        'config': {}
                                    },
                                    {
                                        'formItemType': 'Input',
                                        'name': 'value',
                                        'label': 'Value',
                                        'inputType': 'TextLine',
                                        'occurrences': {
                                            'maximum': 1,
                                            'minimum': 0
                                        },
                                        'config': {}
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        'name': 'option_2',
                        'label': 'Option 2',
                        'default': false,
                        'items': []
                    }
                ]
            },
            {
                'formItemType': 'OptionSet',
                'name': 'checkOptionSet',
                'label': 'Multi selection',
                'expanded': true,
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'selection': {
                    'maximum': 3,
                    'minimum': 0
                },
                'options': [
                    {
                        'name': 'option_1',
                        'label': 'Option 1',
                        'default': true,
                        'items': []
                    },
                    {
                        'name': 'option_2',
                        'label': 'Option 2',
                        'default': true,
                        'items': [
                            {
                                'formItemType': 'OptionSet',
                                'name': 'nestedOptionSet',
                                'label': 'Multi selection',
                                'expanded': false,
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'selection': {
                                    'maximum': 2,
                                    'minimum': 2
                                },
                                'options': [
                                    {
                                        'name': 'option2_1',
                                        'label': 'Option 1_1',
                                        'default': false,
                                        'items': [
                                            {
                                                'formItemType': 'Input',
                                                'name': 'name',
                                                'label': 'Name',
                                                'helpText': 'Text input',
                                                'inputType': 'TextLine',
                                                'occurrences': {
                                                    'maximum': 1,
                                                    'minimum': 1
                                                },
                                                'config': {}
                                            }
                                        ]
                                    },
                                    {
                                        'name': 'option2_2',
                                        'label': 'Option 2_2',
                                        'default': true,
                                        'items': [
                                            {
                                                'formItemType': 'Input',
                                                'name': 'myCheckbox',
                                                'label': 'my-checkbox',
                                                'inputType': 'CheckBox',
                                                'occurrences': {
                                                    'maximum': 1,
                                                    'minimum': 0
                                                },
                                                'config': {}
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        'name': 'option_3',
                        'label': 'Option 3',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'imageselector',
                                'label': 'Image selector',
                                'inputType': 'ImageSelector',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'config': {
                                    'allowPath': [
                                        'path1',
                                        'path2'
                                    ],
                                    'allowContentType': [
                                        'mytype2',
                                        'mytype'
                                    ]
                                }
                            }
                        ]
                    },
                    {
                        'name': 'option_4',
                        'label': 'Option 4',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'double',
                                'label': 'Double',
                                'inputType': 'Double',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'config': {}
                            },
                            {
                                'formItemType': 'Input',
                                'name': 'long',
                                'label': 'Long',
                                'inputType': 'Long',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 0
                                },
                                'config': {}
                            }
                        ]
                    }
                ]
            }
        ],
        'config': {},
    }, result);
};


