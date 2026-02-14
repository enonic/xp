var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Gets the list of all content types in the system
var contentTypes = contentLib.getTypes();

log.info(contentTypes.length + ' content types found:');
contentTypes.forEach(function (ct) {
    if (ct.superType === 'base:structured') {
        log.info(ct.name + ' - ' + ct.displayName);
    }
});
// END

var expected = [
    {
        'name': 'com.enonic.myapp:article',
        'displayName': 'Article',
        'description': 'Article content type',
        'superType': 'base:structured',
        'abstract': false,
        'final': true,
        'allowChildContent': true,
        'icon': {
            'data': {},
            'mimeType': 'image/png',
            'modifiedTime': '2016-01-01T12:00:00Z'
        },
        'form': [
            {
                'formItemType': 'Input',
                'name': 'myTextLine',
                'label': 'My text line',
                'helpText': 'Some help text',
                'inputType': 'TextLine',
                'occurrences': {
                    'maximum': 1,
                    'minimum': 1
                },
                'regexp': '\\b\\d{3}-\\d{2}-\\d{4}\\b'
            },
            {
                'formItemType': 'Input',
                'name': 'myCheckbox',
                'label': 'My checkbox input',
                'inputType': 'CheckBox',
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'default': 'checked'
            },
            {
                'formItemType': 'Input',
                'name': 'myRadioButton',
                'label': 'Radio button',
                'inputType': 'RadioButton',
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'options': [
                    {
                        'value': 'one',
                        'label': {
                            'text': 'Value One'
                        }
                    },
                    {
                        'value': 'two',
                        'label': {
                            'text': 'Value Two'
                        }
                    }
                ],
                'theme': [
                    'dark',
                    'light'
                ],
                'disabled': false
            },
            {
                'formItemType': 'Layout',
                'label': 'My field set',
                'items': [
                    {
                        'formItemType': 'Input',
                        'name': 'myTextLineInFieldSet',
                        'label': 'My text line',
                        'inputType': 'TextLine',
                        'occurrences': {
                            'maximum': 1,
                            'minimum': 0
                        }
                    }
                ]
            },
            {
                'formItemType': 'ItemSet',
                'name': 'myFormItemSet',
                'label': 'My form item set',
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'items': [
                    {
                        'formItemType': 'Input',
                        'name': 'myTextLine',
                        'label': 'My text line',
                        'inputType': 'TextLine',
                        'occurrences': {
                            'maximum': 1,
                            'minimum': 0
                        }
                    }
                ]
            },
            {
                'formItemType': 'OptionSet',
                'name': 'myOptionSet',
                'label': 'My option set',
                'expanded': false,
                'helpText': 'Option set help text',
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'selection': {
                    'maximum': 1,
                    'minimum': 0
                },
                'options': [
                    {
                        'name': 'myOptionSetOption1',
                        'label': 'option label1',
                        'helpText': 'Option help text',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'myTextLine1',
                                'label': 'myTextLine1',
                                'inputType': 'TextLine',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 0
                                }
                            }
                        ]
                    },
                    {
                        'name': 'myOptionSetOption2',
                        'label': 'option label2',
                        'helpText': 'Option help text',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'myTextLine2',
                                'label': 'myTextLine2',
                                'inputType': 'TextLine',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 0
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    },
    {
        'name': 'com.enonic.someapp:person',
        'displayName': 'Person',
        'description': 'Person content type',
        'superType': 'base:structured',
        'abstract': false,
        'final': true,
        'allowChildContent': true,
        'form': []
    }
];

assert.assertJsonEquals(expected, contentTypes);
