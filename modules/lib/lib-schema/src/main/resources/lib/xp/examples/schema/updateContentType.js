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
                </input>
            </form>
        </content-type>`;

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
    displayNameI18nKey: '',
    description: 'My description',
    descriptionI18nKey: '',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n' +
              '        <content-type xmlns=\'urn:enonic:xp:model:1.0\'>\n' +
              '            <display-name>My Tag</display-name>\n' +
              '            <description>My description</description>\n' +
              '            <super-type>base:structured</super-type>\n' +
              '            <is-abstract>false</is-abstract>\n' +
              '            <is-final>true</is-final>\n' +
              '            <allow-child-content>true</allow-child-content>\n' +
              '            \n' +
              '            <x-data name=\'myapp:address\'/>\n' +
              '            \n' +
              '            <form>\n' +
              '                <input name=\'tag_unlimited\' type=\'Tag\'>\n' +
              '                    <label>Tag, unlimited occurrences</label>\n' +
              '                    <immutable>false</immutable>\n' +
              '                    <indexed>true</indexed>\n' +
              '                    <custom-text>Custom text</custom-text>\n' +
              '                    <occurrences minimum=\'0\' maximum=\'0\'/>\n' +
              '                    <help-text>Some help text</help-text>\n' +
              '                </input>\n' +
              '            </form>\n' +
              '        </content-type>',
    type: 'CONTENT_TYPE',
    form: [
        {
            'formItemType': 'Input',
            'name': 'tag_unlimited',
            'label': 'Tag, unlimited occurrences',
            'customText': 'Custom text',
            'helpText': 'Some help text',
            'maximize': true,
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

