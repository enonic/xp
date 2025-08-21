var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = `<?xml version='1.0' encoding='UTF-8'?>
                <site xmlns='urn:enonic:xp:model:1.0'>
                  <x-data name='myapp1:menu-item'/>
                  <x-data name='myapp2:my-meta-mixin'/>
                  <form>
                    <input type='TextLine' name='some-name'>
                      <label>Textline</label>
                      <immutable>false</immutable>
                      <indexed>false</indexed>
                      <custom-text/>
                      <help-text/>
                      <occurrences minimum='0' maximum='1'/>
                    </input>
                  </form>
                  <processors>
                    <response-processor name='filter1' order='10'/>
                    <response-processor name='filter2' order='20'/>
                  </processors>
                  <mappings>
                    <mapping controller='/site/page/person/person.js' order='10'>
                      <pattern>/person/*</pattern>
                    </mapping>
                    <mapping controller='controller1.js'>
                      <match>_path:'/*/fisk'</match>
                    </mapping>
                    <mapping controller='controller2.js' order='5'>
                      <pattern invert='true'>/.*</pattern>
                      <match>type:'portal:fragment'</match>
                    </mapping>
                  </mappings>
                </site>`;


// BEGIN
// Update virtual styles.
var result = schemaLib.updateSite({
    application: 'myapp',
    resource

});

log.info('Updated site: ' + result.application);

// END


assert.assertJsonEquals({
    application: 'myapp',
    resource: `<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n                <site xmlns=\'urn:enonic:xp:model:1.0\'>\n                  <x-data name=\'myapp1:menu-item\'/>\n                  <x-data name=\'myapp2:my-meta-mixin\'/>\n                  <form>\n                    <input type=\'TextLine\' name=\'some-name\'>\n                      <label>Textline</label>\n                      <immutable>false</immutable>\n                      <indexed>false</indexed>\n                      <custom-text/>\n                      <help-text/>\n                      <occurrences minimum=\'0\' maximum=\'1\'/>\n                    </input>\n                  </form>\n                  <processors>\n                    <response-processor name=\'filter1\' order=\'10\'/>\n                    <response-processor name=\'filter2\' order=\'20\'/>\n                  </processors>\n                  <mappings>\n                    <mapping controller=\'/site/page/person/person.js\' order=\'10\'>\n                      <pattern>/person/*</pattern>\n                    </mapping>\n                    <mapping controller=\'controller1.js\'>\n                      <match>_path:'/*/fisk'</match>\n                    </mapping>\n                    <mapping controller=\'controller2.js\' order=\'5\'>\n                      <pattern invert=\'true\'>/.*</pattern>\n                      <match>type:'portal:fragment'</match>\n                    </mapping>\n                  </mappings>\n                </site>`,
    modifiedTime: '2021-09-25T10:00:00Z',
    form: [
        {
            'formItemType': 'Input',
            'name': 'some-name',
            'label': 'Textline',
            'inputType': 'TextLine',
            'occurrences': {
                'maximum': 1,
                'minimum': 0
            },
            'config': {}
        }
    ],
    xDataMappings: [
        {
            'name': 'myapp1:menu-item',
            'optional': false,
            'allowContentTypes': ''
        },
        {
            'name': 'myapp2:my-meta-mixin',
            'optional': false,
            'allowContentTypes': ''
        }
    ]
}, result);

