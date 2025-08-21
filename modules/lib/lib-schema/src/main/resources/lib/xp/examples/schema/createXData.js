var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `<?xml version='1.0' encoding='UTF-8'?>
        <x-data xmlns='urn:enonic:xp:model:1.0'>
          <display-name>Virtual X-data</display-name>
          <description>X-data description</description>
          <form>
            <input type='TextLine' name='label'>
              <label>Label</label>
              <immutable>false</immutable>
              <indexed>true</indexed>
              <custom-text/>
              <help-text/>
              <occurrences minimum='0' maximum='2'/>
            </input>
          </form>
        </x-data>`;

// BEGIN
// Create virtual mixin.
var result = schemaLib.createSchema({
    name: 'myapp:mydata',
    type: 'XDATA',
    resource

});

log.info('Created x-data: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'myapp:mydata',
    displayName: 'Virtual X-data',
    displayNameI18nKey: '',
    description: 'X-data description',
    descriptionI18nKey: '',
    createdTime: '2021-09-25T10:00:00Z',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n' +
              '        <x-data xmlns=\'urn:enonic:xp:model:1.0\'>\n' +
              '          <display-name>Virtual X-data</display-name>\n' +
              '          <description>X-data description</description>\n' +
              '          <form>\n' +
              '            <input type=\'TextLine\' name=\'label\'>\n' +
              '              <label>Label</label>\n' +
              '              <immutable>false</immutable>\n' +
              '              <indexed>true</indexed>\n' +
              '              <custom-text/>\n' +
              '              <help-text/>\n' +
              '              <occurrences minimum=\'0\' maximum=\'2\'/>\n' +
              '            </input>\n' +
              '          </form>\n' +
              '        </x-data>',
    type: 'XDATA',
    form: [
        {
            'formItemType': 'Input',
            'name': 'label',
            'label': 'Label',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 2,
                'minimum': 0
            },
            'config': {}
        }
    ]
}, result);

