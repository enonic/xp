var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `<?xml version='1.0' encoding='UTF-8'?>
                <layout xmlns='urn:enonic:xp:model:1.0'>
                  <display-name i18n='key.display-name'>Virtual Layout</display-name>
                  <description i18n='key.description'>My Layout Description</description>
                  <form>
                    <input type='Double' name='pause'>
                      <label i18n='key1.label'>Pause parameter</label>
                      <immutable>false</immutable>
                      <indexed>false</indexed>
                      <help-text i18n='key1.help-text'/>
                      <occurrences minimum='0' maximum='1'/>
                    </input>
                    <item-set name='myFormItemSet'>
                      <label>My form item set</label>
                      <immutable>false</immutable>
                      <occurrences minimum='0' maximum='1'/>
                      <items>
                        <input type='TextLine' name='myTextLine'>
                          <label>My text line</label>
                          <immutable>false</immutable>
                          <indexed>false</indexed>
                          <occurrences minimum='1' maximum='1'/>
                        </input>
                        <input type='TextLine' name='myCustomInput'>
                          <label>My custom input</label>
                          <immutable>false</immutable>
                          <indexed>false</indexed>
                          <occurrences minimum='0' maximum='1'/>
                        </input>
                      </items>
                    </item-set>
                  </form>
                  <regions>
                    <region name='header'/>
                    <region name='main'/>
                    <region name='footer'/>
                  </regions>
                </layout>
                `;

// BEGIN
// Update virtual layout.
var result = schemaLib.updateComponent({
    key: 'myapp:mylayout',
    type: 'LAYOUT',
    resource

});

log.info('Updated layout: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'myapp:mylayout',
    displayName: 'Virtual Layout',
    displayNameI18nKey: 'key.display-name',
    description: 'My Layout Description',
    descriptionI18nKey: 'key.description',
    componentPath: 'myapp:/site/layouts/mylayout',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n' +
              '                <layout xmlns=\'urn:enonic:xp:model:1.0\'>\n' +
              '                  <display-name i18n=\'key.display-name\'>Virtual Layout</display-name>\n' +
              '                  <description i18n=\'key.description\'>My Layout Description</description>\n' +
              '                  <form>\n                    <input type=\'Double\' name=\'pause\'>\n' +
              '                      <label i18n=\'key1.label\'>Pause parameter</label>\n' +
              '                      <immutable>false</immutable>\n' +
              '                      <indexed>false</indexed>\n' +
              '                      <help-text i18n=\'key1.help-text\'/>\n' +
              '                      <occurrences minimum=\'0\' maximum=\'1\'/>\n' +
              '                    </input>\n' +
              '                    <item-set name=\'myFormItemSet\'>\n' +
              '                      <label>My form item set</label>\n' +
              '                      <immutable>false</immutable>\n' +
              '                      <occurrences minimum=\'0\' maximum=\'1\'/>\n' +
              '                      <items>\n' +
              '                        <input type=\'TextLine\' name=\'myTextLine\'>\n' +
              '                          <label>My text line</label>\n' +
              '                          <immutable>false</immutable>\n' +
              '                          <indexed>false</indexed>\n' +
              '                          <occurrences minimum=\'1\' maximum=\'1\'/>\n' +
              '                        </input>\n' +
              '                        <input type=\'TextLine\' name=\'myCustomInput\'>\n' +
              '                          <label>My custom input</label>\n' +
              '                          <immutable>false</immutable>\n' +
              '                          <indexed>false</indexed>\n' +
              '                          <occurrences minimum=\'0\' maximum=\'1\'/>\n' +
              '                        </input>\n' +
              '                      </items>\n                    </item-set>\n' +
              '                  </form>\n                  <regions>\n' +
              '                    <region name=\'header\'/>\n' +
              '                    <region name=\'main\'/>\n' +
              '                    <region name=\'footer\'/>\n' +
              '                  </regions>\n' +
              '                </layout>\n' +
              '                ',
    type: 'LAYOUT',
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
                        'minimum': 1
                    },
                    'config': {}
                },
                {
                    'formItemType': 'Input',
                    'name': 'myCustomInput',
                    'label': 'My custom input',
                    'inputType': 'TextLine',
                    'occurrences': {
                        'maximum': 1,
                        'minimum': 0
                    },
                    'config': {}
                }
            ]
        }
    ],
    config: {},
    regions: [
        'header',
        'main',
        'footer'
    ]
}, result);

