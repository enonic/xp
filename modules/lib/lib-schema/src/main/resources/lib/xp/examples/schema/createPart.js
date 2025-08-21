var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `<?xml version='1.0' encoding='UTF-8'?>
                <part xmlns='urn:enonic:xp:model:1.0'>
                  <display-name i18n='key.display-name'>Virtual Part</display-name>
                  <description i18n='key.description'>My Part Description</description>
                  <form>
                    <input type='Double' name='width'>
                      <label i18n='key.label'>Column width</label>
                      <immutable>false</immutable>
                      <indexed>false</indexed>
                      <help-text i18n='key.help-text'/>
                      <occurrences minimum='0' maximum='1'/>
                    </input>
                
                    <mixin name='myapplication:link-urls'/>
                
                  </form>
                  <config>
                    <input type='Double' name='width'><label i18n='key.label'>Column width</label><immutable>false</immutable><indexed>false</indexed><help-text i18n='key.help-text'/><occurrences minimum='0' maximum='1'/></input>
                  </config>
                </part>
                `;

// BEGIN
// Create virtual part.
var result = schemaLib.createComponent({
    key: 'myapp:mypart',
    type: 'PART',
    resource

});

log.info('Created part: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypart',
    displayName: 'Virtual Part',
    displayNameI18nKey: 'key.display-name',
    description: 'My Part Description',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/site/parts/mypart',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n                <part xmlns=\'urn:enonic:xp:model:1.0\'>\n                  <display-name i18n=\'key.display-name\'>Virtual Part</display-name>\n                  <description i18n=\'key.description\'>My Part Description</description>\n                  <form>\n                    <input type=\'Double\' name=\'width\'>\n                      <label i18n=\'key.label\'>Column width</label>\n                      <immutable>false</immutable>\n                      <indexed>false</indexed>\n                      <help-text i18n=\'key.help-text\'/>\n                      <occurrences minimum=\'0\' maximum=\'1\'/>\n                    </input>\n                \n                    <mixin name=\'myapplication:link-urls\'/>\n                \n                  </form>\n                  <config>\n                    <input type=\'Double\' name=\'width\'><label i18n=\'key.label\'>Column width</label><immutable>false</immutable><indexed>false</indexed><help-text i18n=\'key.help-text\'/><occurrences minimum=\'0\' maximum=\'1\'/></input>\n                  </config>\n                </part>\n                ',
    type: 'PART',
    form: [
        {
            'formItemType': 'Input',
            'name': 'width',
            'label': 'Column width',
            'helpText': 'key.help-text',
            'inputType': 'Double',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        },
        {
            'formItemType': 'InlineMixin',
            'name': 'myapplication:link-urls'
        }
    ],

    config: {
        'input': [{
            'value': '', '@name': 'width', '@type': 'Double'
        }]
    }
}, result);

