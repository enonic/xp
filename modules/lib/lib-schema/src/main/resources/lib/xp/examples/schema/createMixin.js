var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `<?xml version='1.0' encoding='UTF-8'?>
       <mixin xmlns='urn:enonic:xp:model:1.0'>
          <display-name>Virtual Mixin</display-name>
          <description>Mixin description</description>
          <form>
            <input type='TextLine' name='text2'>
              <label>Text 2</label>
            </input>
        
            <mixin name='myapplication:inline'/>
          </form>
        </mixin>`;

// BEGIN
// Create virtual mixin.
var result = schemaLib.createSchema({
    name: 'myapp:mytype',
    type: 'MIXIN',
    resource

});

log.info('Created mixin: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mytype',
    displayName: 'Virtual Mixin',
    displayNameI18nKey: '',
    description: 'Mixin description',
    descriptionI18nKey: '',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n' +
              '       <mixin xmlns=\'urn:enonic:xp:model:1.0\'>\n ' +
              '         <display-name>Virtual Mixin</display-name>\n' +
              '          <description>Mixin description</description>\n' +
              '          <form>\n' +
              '            <input type=\'TextLine\' name=\'text2\'>\n' +
              '              <label>Text 2</label>\n' +
              '            </input>\n' +
              '        \n' +
              '            <mixin name=\'myapplication:inline\'/>\n' +
              '          </form>\n' +
              '        </mixin>',
    type: 'MIXIN',
    form: [
        {
            'formItemType': 'Input',
            'name': 'text2',
            'label': 'Text 2',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        }, {
            formItemType: 'InlineMixin',
            name: 'myapplication:inline'
        }
    ],

}, result);

