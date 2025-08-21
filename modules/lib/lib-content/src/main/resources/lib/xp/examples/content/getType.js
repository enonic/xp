var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Get a content type by name
var contentType = contentLib.getType('com.enonic.myapp:person');
// END

// BEGIN
// Content type returned:
var expected = {
    'name': 'com.enonic.myapp:person',
    'displayName': 'Person',
    'description': 'Person content type',
    'superType': 'base:structured',
    'abstract': false,
    'final': true,
    'allowChildContent': true,
    'displayNameExpression': '${name}',
    'modifiedTime': '2022-05-25T10:00:00Z',
    'icon': {
        'data': {},
        'mimeType': 'image/png',
        'modifiedTime': '2016-01-01T12:00:00Z'
    },
    'form': [
        {
            'formItemType': 'Input',
            'name': 'name',
            'label': 'Full name',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 1
            },
            'config': {}
        },
        {
            'formItemType': 'Input',
            'name': 'title',
            'label': 'Photo',
            'helpText': 'Person photo',
            'inputType': 'ImageSelector',
            'occurrences': {
                'maximum': 1,
                'minimum': 1
            },
            'config': {}
        },
        {
            'formItemType': 'Input',
            'name': 'bio',
            'label': 'Bio',
            'inputType': 'HtmlArea',
            'occurrences': {
                'maximum': 1,
                'minimum': 1
            },
            'config': {}
        },
        {
            'formItemType': 'Input',
            'name': 'birthdate',
            'label': 'Birth date',
            'inputType': 'Date',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        },
        {
            'formItemType': 'Input',
            'name': 'email',
            'label': 'Email',
            'helpText': 'Email address',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 1
            },
            'config': {
                'regexp': [
                    {
                        'value': '^[^@]+@[^@]+\\.[^@]+$'
                    }
                ]
            }
        },
        {
            'formItemType': 'Input',
            'name': 'nationality',
            'label': 'Nationality',
            'inputType': 'ContentSelector',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {
                'allowContentType': [
                    {
                        'value': 'com.enonic.myapp:country'
                    }
                ]
            }
        }
    ]
};
// END

assert.assertJsonEquals(expected, contentType);

// BEGIN
// Get a content type icon
var ct = contentLib.getType('com.enonic.myapp:person');
var icon = ct.icon;
return {
    body: icon.data,
    contentType: icon.mimeType
};
// END
