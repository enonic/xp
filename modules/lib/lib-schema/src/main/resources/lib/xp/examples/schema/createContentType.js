var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `<?xml version='1.0' encoding='UTF-8'?>
        <content-type xmlns='urn:enonic:xp:model:1.0'>
            <display-name>My Tag</display-name>
            <description>My description</description>
            <super-type>base:structured</super-type>
            <is-abstract>false</is-abstract>
            <is-final>true</is-final>
            <allow-child-content>true</allow-child-content>
            
            <x-data name='myapp:address'/>
            
            <form>
                <input name='tag_unlimited' type='Tag'>
                    <label>Tag, unlimited occurrences</label>
                    <immutable>false</immutable>
                    <indexed>true</indexed>
                    <custom-text>Custom text</custom-text>
                    <occurrences minimum='0' maximum='0'/>
                    <help-text>Some help text</help-text>
                     <config>
                         <context>true</context>
                     </config>
                </input>
            </form>
            <config>
                <alignment>bottom</alignment>
            </config>
            
        </content-type>`;

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
    displayNameI18nKey: '',
    description: 'My description',
    descriptionI18nKey: '',
    createdTime: '2021-09-25T10:00:00Z',
    creator: 'user:system:anonymous',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n        <content-type xmlns=\'urn:enonic:xp:model:1.0\'>\n            <display-name>My Tag</display-name>\n            <description>My description</description>\n            <super-type>base:structured</super-type>\n            <is-abstract>false</is-abstract>\n            <is-final>true</is-final>\n            <allow-child-content>true</allow-child-content>\n            \n            <x-data name=\'myapp:address\'/>\n            \n            <form>\n                <input name=\'tag_unlimited\' type=\'Tag\'>\n                    <label>Tag, unlimited occurrences</label>\n                    <immutable>false</immutable>\n                    <indexed>true</indexed>\n                    <custom-text>Custom text</custom-text>\n                    <occurrences minimum=\'0\' maximum=\'0\'/>\n                    <help-text>Some help text</help-text>\n                     <config>\n                         <context>true</context>\n                     </config>\n                </input>\n            </form>\n            <config>\n                <alignment>bottom</alignment>\n            </config>\n            \n        </content-type>',
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
                'context': [
                    {
                        'value': 'true'
                    }
                ]
            }
        }
    ],
    config: {
        'alignment': [
            {
                'value': 'bottom'
            }
        ]
    }
}, result);

