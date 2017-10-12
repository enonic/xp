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
        "name": "com.enonic.myapp:article",
        "displayName": "Article",
        "description": "Article content type",
        "superType": "base:structured",
        "abstract": false,
        "final": true,
        "allowChildContent": true,
        "contentDisplayNameScript": "$('title') + ' ' + $('author')",
        "icon": {
            "mimeType": "image/png",
            "modifiedTime": "2016-01-01T12:00:00Z"
        },
        "form": [
            {
                "formItemType": "Input",
                "name": "myTextLine",
                "label": "My text line",
                "customText": "Some custom text",
                "helpText": "Some help text",
                "maximize": true,
                "inputType": "TextLine",
                "occurrences": {
                    "maximum": 1,
                    "minimum": 1
                },
                "config": {
                    "regexp": [
                        {
                            "value": "\\b\\d{3}-\\d{2}-\\d{4}\\b"
                        }
                    ]
                }
            },
            {
                "formItemType": "Input",
                "name": "myCheckbox",
                "label": "My checkbox input",
                "maximize": true,
                "inputType": "CheckBox",
                "occurrences": {
                    "maximum": 1,
                    "minimum": 0
                },
                "default": {
                    "value": true,
                    "type": "Boolean"
                },
                "config": {}
            },
            {
                "formItemType": "Input",
                "name": "myRadioButton",
                "label": "Radio button",
                "maximize": true,
                "inputType": "RadioButton",
                "occurrences": {
                    "maximum": 1,
                    "minimum": 0
                },
                "config": {
                    "option": [
                        {
                            "value": "Option One",
                            "@value": "one"
                        },
                        {
                            "value": "Option Two",
                            "@value": "two"
                        }
                    ]
                }
            },
            {
                "formItemType": "Layout",
                "name": "myFieldSet",
                "label": "My field set",
                "items": [
                    {
                        "formItemType": "Input",
                        "name": "myTextLineInFieldSet",
                        "label": "My text line",
                        "maximize": true,
                        "inputType": "TextLine",
                        "occurrences": {
                            "maximum": 1,
                            "minimum": 0
                        },
                        "config": {}
                    }
                ]
            },
            {
                "formItemType": "ItemSet",
                "name": "myFormItemSet",
                "label": "My form item set",
                "occurrences": {
                    "maximum": 1,
                    "minimum": 0
                },
                "items": [
                    {
                        "formItemType": "Input",
                        "name": "myTextLine",
                        "label": "My text line",
                        "maximize": true,
                        "inputType": "TextLine",
                        "occurrences": {
                            "maximum": 1,
                            "minimum": 0
                        },
                        "config": {}
                    }
                ]
            },
            {
                "formItemType": "OptionSet",
                "name": "myOptionSet",
                "label": "My option set",
                "expanded": false,
                "helpText": "Option set help text",
                "occurrences": {
                    "maximum": 1,
                    "minimum": 0
                },
                "selection": {
                    "maximum": 1,
                    "minimum": 0
                },
                "options": [
                    {
                        "name": "myOptionSetOption1",
                        "label": "option label1",
                        "helpText": "Option help text",
                        "default": false,
                        "items": [
                            {
                                "formItemType": "Input",
                                "name": "myTextLine1",
                                "label": "myTextLine1",
                                "maximize": true,
                                "inputType": "TextLine",
                                "occurrences": {
                                    "maximum": 1,
                                    "minimum": 0
                                },
                                "config": {}
                            }
                        ]
                    },
                    {
                        "name": "myOptionSetOption2",
                        "label": "option label2",
                        "helpText": "Option help text",
                        "default": false,
                        "items": [
                            {
                                "formItemType": "Input",
                                "name": "myTextLine2",
                                "label": "myTextLine2",
                                "maximize": true,
                                "inputType": "TextLine",
                                "occurrences": {
                                    "maximum": 1,
                                    "minimum": 0
                                },
                                "config": {}
                            }
                        ]
                    }
                ]
            }
        ]
    },
    {
        "name": "com.enonic.someapp:person",
        "displayName": "Person",
        "description": "Person content type",
        "superType": "base:structured",
        "abstract": false,
        "final": true,
        "allowChildContent": true,
        "form": []
    }
];

assert.assertJsonEquals(expected, contentTypes);
