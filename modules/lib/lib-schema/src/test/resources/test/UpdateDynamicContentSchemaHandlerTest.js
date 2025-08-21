var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.updateInvalidContentSchema = function () {
    assert.assertThrows(() => schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'CONTENT_TYPE',
        resource: `<?xml version='1.0' encoding='UTF-8'?>
        <content-type xmlns='urn:enonic:xp:model:1.0'>
        </content-type>`
    }));
};

exports.updateInvalidContentSchemaType = function () {
    assert.assertThrows(() => schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'INVALID_TYPE',
        resource: ''
    }));
};

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
            <option-set name='radioOptionSet'>
              <label>Single selection</label>
              <expanded>false</expanded>
              <occurrences minimum='0' maximum='1'/>
              <options minimum='1' maximum='1'>
                <option name='option_1'>
                  <label>Option 1</label>
                  <default>false</default>
                  <items>
                    <input name='text-input' type='TextLine'>
                      <label>Name</label>
                      <help-text>Text input</help-text>
                      <default>something</default>
                      <occurrences minimum='1' maximum='1'/>
                    </input>
                    <item-set name='minimum3'>
                      <label>Minimum 3</label>
                      <occurrences minimum='3' maximum='0'/>
                      <items>
                        <input name='label' type='TextLine'>
                          <label>Label</label>
                          <occurrences minimum='0' maximum='1'/>
                        </input>
                        <input name='value' type='TextLine'>
                          <label>Value</label>
                          <occurrences minimum='0' maximum='1'/>
                        </input>
                      </items>
                    </item-set>
                  </items>
                </option>
                <option name='option_2'>
                  <label>Option 2</label>
                  <default>false</default>
                  <items>
                  </items>
                </option>
              </options>
            </option-set>
            <option-set name='checkOptionSet'>
              <label>Multi selection</label>
              <expanded>true</expanded>
              <occurrences minimum='0' maximum='1'/>
              <options minimum='0' maximum='3'>
                <option name='option_1'>
                  <label>Option 1</label>
                  <default>true</default>
                  <items>
                  </items>
                </option>
                <option name='option_2'>
                  <label>Option 2</label>
                  <default>true</default>
                  <items>
                    <option-set name='nestedOptionSet'>
                      <label>Multi selection</label>
                      <expanded>false</expanded>
                      <occurrences minimum='1' maximum='1'/>
                      <options minimum='2' maximum='2'>
                        <option name='option2_1'>
                          <label>Option 1_1</label>
                          <default>false</default>
                          <items>
                            <input name='name' type='TextLine'>
                              <label>Name</label>
                              <help-text>Text input</help-text>
                              <occurrences minimum='1' maximum='1'/>
                            </input>
                          </items>
                        </option>
                        <option name='option2_2'>
                          <label>Option 2_2</label>
                          <default>true</default>
                          <items>
                            <input name='myCheckbox' type='Checkbox'>
                              <label>my-checkbox</label>
                              <immutable>false</immutable>
                              <indexed>false</indexed>
                              <occurrences minimum='0' maximum='1'/>
                            </input>
                          </items>
                        </option>
                      </options>
                    </option-set>
                  </items>
                </option>
                <option name='option_3'>
                  <label>Option 3</label>
                  <default>false</default>
                  <items>
                    <input name='imageselector' type='ImageSelector'>
                      <label>Image selector</label>
                      <indexed>true</indexed>
                      <occurrences minimum='1' maximum='1'/>
                      <config>
                        <allowType>mytype</allowType>
                        <allowType>mytype2</allowType>
                        <allowPath>path1</allowPath>
                        <allowPath>path2</allowPath>
                      </config>
                    </input>
                  </items>
                </option>
                <option name='option_4'>
                  <label>Option 4</label>
                  <default>false</default>
                  <items>
                    <input name='double' type='Double'>
                      <label>Double</label>
                      <indexed>true</indexed>
                      <occurrences minimum='1' maximum='1'/>
                    </input>
                    <input name='long' type='Long'>
                      <label>Long</label>
                      <indexed>true</indexed>
                    </input>
                  </items>
                </option>
              </options>
            </option-set>
          </form>
        </content-type>`;

exports.updateWithForm = function () {
    let result = schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'CONTENT_TYPE',
        resource
    });

    assert.assertJsonEquals({
        'name': 'myapp:mydata',
        'displayName': 'My Tag',
        'displayNameI18nKey': '',
        'description': 'My description',
        'descriptionI18nKey': '',
        'createdTime': '2021-09-25T10:00:00Z',
        'modifiedTime': '2021-09-25T10:00:00Z',
        'resource': '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n        <content-type xmlns=\'urn:enonic:xp:model:1.0\'>\n            <display-name>My Tag</display-name>\n            <description>My description</description>\n            <super-type>base:structured</super-type>\n            <is-abstract>false</is-abstract>\n            <is-final>true</is-final>\n            <allow-child-content>true</allow-child-content>\n            \n            <x-data name=\'myapp:address\'/>\n            \n            <form>\n            <option-set name=\'radioOptionSet\'>\n              <label>Single selection</label>\n              <expanded>false</expanded>\n              <occurrences minimum=\'0\' maximum=\'1\'/>\n              <options minimum=\'1\' maximum=\'1\'>\n                <option name=\'option_1\'>\n                  <label>Option 1</label>\n                  <default>false</default>\n                  <items>\n                    <input name=\'text-input\' type=\'TextLine\'>\n                      <label>Name</label>\n                      <help-text>Text input</help-text>\n                      <default>something</default>\n                      <occurrences minimum=\'1\' maximum=\'1\'/>\n                    </input>\n                    <item-set name=\'minimum3\'>\n                      <label>Minimum 3</label>\n                      <occurrences minimum=\'3\' maximum=\'0\'/>\n                      <items>\n                        <input name=\'label\' type=\'TextLine\'>\n                          <label>Label</label>\n                          <occurrences minimum=\'0\' maximum=\'1\'/>\n                        </input>\n                        <input name=\'value\' type=\'TextLine\'>\n                          <label>Value</label>\n                          <occurrences minimum=\'0\' maximum=\'1\'/>\n                        </input>\n                      </items>\n                    </item-set>\n                  </items>\n                </option>\n                <option name=\'option_2\'>\n                  <label>Option 2</label>\n                  <default>false</default>\n                  <items>\n                  </items>\n                </option>\n              </options>\n            </option-set>\n            <option-set name=\'checkOptionSet\'>\n              <label>Multi selection</label>\n              <expanded>true</expanded>\n              <occurrences minimum=\'0\' maximum=\'1\'/>\n              <options minimum=\'0\' maximum=\'3\'>\n                <option name=\'option_1\'>\n                  <label>Option 1</label>\n                  <default>true</default>\n                  <items>\n                  </items>\n                </option>\n                <option name=\'option_2\'>\n                  <label>Option 2</label>\n                  <default>true</default>\n                  <items>\n                    <option-set name=\'nestedOptionSet\'>\n                      <label>Multi selection</label>\n                      <expanded>false</expanded>\n                      <occurrences minimum=\'1\' maximum=\'1\'/>\n                      <options minimum=\'2\' maximum=\'2\'>\n                        <option name=\'option2_1\'>\n                          <label>Option 1_1</label>\n                          <default>false</default>\n                          <items>\n                            <input name=\'name\' type=\'TextLine\'>\n                              <label>Name</label>\n                              <help-text>Text input</help-text>\n                              <occurrences minimum=\'1\' maximum=\'1\'/>\n                            </input>\n                          </items>\n                        </option>\n                        <option name=\'option2_2\'>\n                          <label>Option 2_2</label>\n                          <default>true</default>\n                          <items>\n                            <input name=\'myCheckbox\' type=\'Checkbox\'>\n                              <label>my-checkbox</label>\n                              <immutable>false</immutable>\n                              <indexed>false</indexed>\n                              <occurrences minimum=\'0\' maximum=\'1\'/>\n                            </input>\n                          </items>\n                        </option>\n                      </options>\n                    </option-set>\n                  </items>\n                </option>\n                <option name=\'option_3\'>\n                  <label>Option 3</label>\n                  <default>false</default>\n                  <items>\n                    <input name=\'imageselector\' type=\'ImageSelector\'>\n                      <label>Image selector</label>\n                      <indexed>true</indexed>\n                      <occurrences minimum=\'1\' maximum=\'1\'/>\n                      <config>\n                        <allowType>mytype</allowType>\n                        <allowType>mytype2</allowType>\n                        <allowPath>path1</allowPath>\n                        <allowPath>path2</allowPath>\n                      </config>\n                    </input>\n                  </items>\n                </option>\n                <option name=\'option_4\'>\n                  <label>Option 4</label>\n                  <default>false</default>\n                  <items>\n                    <input name=\'double\' type=\'Double\'>\n                      <label>Double</label>\n                      <indexed>true</indexed>\n                      <occurrences minimum=\'1\' maximum=\'1\'/>\n                    </input>\n                    <input name=\'long\' type=\'Long\'>\n                      <label>Long</label>\n                      <indexed>true</indexed>\n                    </input>\n                  </items>\n                </option>\n              </options>\n            </option-set>\n          </form>\n        </content-type>',
        'type': 'CONTENT_TYPE',
        'form': [
            {
                'formItemType': 'OptionSet',
                'name': 'radioOptionSet',
                'label': 'Single selection',
                'expanded': false,
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'selection': {
                    'maximum': 1,
                    'minimum': 1
                },
                'options': [
                    {
                        'name': 'option_1',
                        'label': 'Option 1',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'text-input',
                                'label': 'Name',
                                'helpText': 'Text input',
                                'inputType': 'TextLine',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'default': {
                                    'value': 'something',
                                    'type': 'String'
                                },
                                'config': {}
                            },
                            {
                                'formItemType': 'ItemSet',
                                'name': 'minimum3',
                                'label': 'Minimum 3',
                                'occurrences': {
                                    'maximum': 0,
                                    'minimum': 3
                                },
                                'items': [
                                    {
                                        'formItemType': 'Input',
                                        'name': 'label',
                                        'label': 'Label',
                                        'inputType': 'TextLine',
                                        'occurrences': {
                                            'maximum': 1,
                                            'minimum': 0
                                        },
                                        'config': {}
                                    },
                                    {
                                        'formItemType': 'Input',
                                        'name': 'value',
                                        'label': 'Value',
                                        'inputType': 'TextLine',
                                        'occurrences': {
                                            'maximum': 1,
                                            'minimum': 0
                                        },
                                        'config': {}
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        'name': 'option_2',
                        'label': 'Option 2',
                        'default': false,
                        'items': []
                    }
                ]
            },
            {
                'formItemType': 'OptionSet',
                'name': 'checkOptionSet',
                'label': 'Multi selection',
                'expanded': true,
                'occurrences': {
                    'maximum': 1,
                    'minimum': 0
                },
                'selection': {
                    'maximum': 3,
                    'minimum': 0
                },
                'options': [
                    {
                        'name': 'option_1',
                        'label': 'Option 1',
                        'default': true,
                        'items': []
                    },
                    {
                        'name': 'option_2',
                        'label': 'Option 2',
                        'default': true,
                        'items': [
                            {
                                'formItemType': 'OptionSet',
                                'name': 'nestedOptionSet',
                                'label': 'Multi selection',
                                'expanded': false,
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'selection': {
                                    'maximum': 2,
                                    'minimum': 2
                                },
                                'options': [
                                    {
                                        'name': 'option2_1',
                                        'label': 'Option 1_1',
                                        'default': false,
                                        'items': [
                                            {
                                                'formItemType': 'Input',
                                                'name': 'name',
                                                'label': 'Name',
                                                'helpText': 'Text input',
                                                'inputType': 'TextLine',
                                                'occurrences': {
                                                    'maximum': 1,
                                                    'minimum': 1
                                                },
                                                'config': {}
                                            }
                                        ]
                                    },
                                    {
                                        'name': 'option2_2',
                                        'label': 'Option 2_2',
                                        'default': true,
                                        'items': [
                                            {
                                                'formItemType': 'Input',
                                                'name': 'myCheckbox',
                                                'label': 'my-checkbox',
                                                'inputType': 'Checkbox',
                                                'occurrences': {
                                                    'maximum': 1,
                                                    'minimum': 0
                                                },
                                                'config': {}
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        'name': 'option_3',
                        'label': 'Option 3',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'imageselector',
                                'label': 'Image selector',
                                'inputType': 'ImageSelector',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'config': {
                                    'allowContentType': [
                                        {
                                            'value': 'mytype'
                                        },
                                        {
                                            'value': 'mytype2'
                                        }
                                    ],
                                    'allowPath': [
                                        {
                                            'value': 'path1'
                                        },
                                        {
                                            'value': 'path2'
                                        }
                                    ]
                                }
                            }
                        ]
                    },
                    {
                        'name': 'option_4',
                        'label': 'Option 4',
                        'default': false,
                        'items': [
                            {
                                'formItemType': 'Input',
                                'name': 'double',
                                'label': 'Double',
                                'inputType': 'Double',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 1
                                },
                                'config': {}
                            },
                            {
                                'formItemType': 'Input',
                                'name': 'long',
                                'label': 'Long',
                                'inputType': 'Long',
                                'occurrences': {
                                    'maximum': 1,
                                    'minimum': 0
                                },
                                'config': {}
                            }
                        ]
                    }
                ]
            }
        ],
        'config': {},
    }, result);
};


