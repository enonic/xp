var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `<?xml version='1.0' encoding='UTF-8'?>
                <page xmlns='urn:enonic:xp:model:1.0'>
                  <display-name i18n='key.display-name'>Virtual Page</display-name>
                  <description i18n='key.description'>My Page Description</description>
                  <form>
                    <input type='Double' name='pause'>
                      <label i18n='key1.label'>Pause parameter</label>
                      <immutable>false</immutable>
                      <indexed>false</indexed>
                      <occurrences minimum='0' maximum='1'/>
                      <help-text i18n='key1.help-text'/>
                    </input>
                  </form>
                  <regions>
                    <region name='header'/>
                    <region name='main'/>
                    <region name='footer'/>
                  </regions>
                </page>
                `;

// BEGIN
// Update virtual page.
var result = schemaLib.updateComponent({
    key: 'myapp:mypage',
    type: 'PAGE',
    resource

});

log.info('Updated page: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mypage',
    displayName: 'Virtual Page',
    displayNameI18nKey: 'key.display-name',
    description: 'My Page Description',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/site/pages/mypage',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n' +
              '                <page xmlns=\'urn:enonic:xp:model:1.0\'>\n' +
              '                  <display-name i18n=\'key.display-name\'>Virtual Page</display-name>\n' +
              '                  <description i18n=\'key.description\'>My Page Description</description>\n' +
              '                  <form>\n                    <input type=\'Double\' name=\'pause\'>\n' +
              '                      <label i18n=\'key1.label\'>Pause parameter</label>\n' +
              '                      <immutable>false</immutable>\n' +
              '                      <indexed>false</indexed>\n' +
              '                      <occurrences minimum=\'0\' maximum=\'1\'/>\n' +
              '                      <help-text i18n=\'key1.help-text\'/>\n' +
              '                    </input>\n' +
              '                  </form>\n' +
              '                  <regions>\n' +
              '                    <region name=\'header\'/>\n' +
              '                    <region name=\'main\'/>\n' +
              '                    <region name=\'footer\'/>\n' +
              '                  </regions>\n' +
              '                </page>\n' +
              '                ',
    type: 'PAGE',
    form: [
        {
            'formItemType': 'Input',
            'name': 'pause',
            'label': 'Pause parameter',
            'helpText': 'key1.help-text',
            'inputType': 'Double',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        }
    ],
    config: {},
    regions: [
        'header',
        'main',
        'footer'
    ]
}, result);

