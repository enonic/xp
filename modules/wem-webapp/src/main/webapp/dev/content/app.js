Ext.application({
    name: 'App',

    controllers: [
    ],

    requires: [
        'Admin.view.contentManager.wizard.ContentDataPanel'
    ],

    launch: function () {
        function loadContentType(file) {
            Ext.Ajax.request({
                url: file,
                success: function (response) {
                    var viewport = Ext.ComponentQuery.query('viewport')[0];
                    var center = Ext.getCmp('center');

                    if (center.down()) {
                        center.remove(center.down());
                    }

                    var contentTypeJson = Ext.JSON.decode(response.responseText, true);

                    var contentDataPanel = new Admin.view.contentManager.wizard.ContentDataPanel({
                        border: false,
                        contentType: contentTypeJson
                    });
                    center.add(contentDataPanel);

                    var submitButton = Ext.widget('button', {
                        text: 'Submit',
                        formBind: true, //only enabled once the form is valid
                        disabled: true,
                        handler: function () {
                            var formPanel = this.up('form');
                            var form = formPanel.getForm();
                            if (form.isValid()) {
                                var formData = formPanel.getData();

                            }
                        }
                    });

                    contentDataPanel.add(submitButton);

                    Ext.getCmp('output').setValue(contentTypeJson.qualifiedName + '\n\nJSON:\n\n' + response.responseText);
                }
            });
        }

        Ext.create('Ext.container.Viewport', {
            padding: 0,
            defaults: {
                margin: 0
            },

            layout: 'fit',
            items: [
                {
                    layout: 'border',
                    bodyStyle: 'background-color: #fff',
                    items: [
                        {
                            region: 'north',
                            border: false,
                            xtype: 'toolbar',
                            defaults: {
                                margin: '0 5'
                            },
                            items: [
                                {
                                    xtype: 'combo',
                                    fieldLabel: 'Load Content Type',
                                    labelWidth: 100,
                                    store: Ext.create('Ext.data.Store', {
                                        fields: ['file', 'qualifiedName'],
                                        data: [
                                            {file: 'mock-contenttype-htmlarea.json', qualifiedName: 'Demo:HtmlArea'},
                                            {file: 'mock-contenttype-relation.json', qualifiedName: 'Demo:Relation'},
                                            {file: 'mock-contenttype-textarea.json', qualifiedName: 'Demo:TextArea'},
                                            {file: 'mock-contenttype-set.json', qualifiedName: 'Demo:FormItemSet'},
                                            {file: 'mock-contenttype-performance.json', qualifiedName: 'Demo:Performance'}
                                        ]
                                    }),
                                    queryMode: 'local',
                                    valueField: 'file',
                                    displayField: 'qualifiedName',
                                    listeners: {
                                        select: function (combo) {
                                            loadContentType(combo.getValue());
                                        },
                                        render: function (combo) {
                                            combo.setValue('mock-contenttype-set.json');
                                            loadContentType(combo.getValue());
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            region: 'center',
                            xtype: 'container',
                            id: 'center',
                            autoScroll: true,
                            flex: 2,
                            bodyStyle: 'background-color: #fff;',
                            defaults: {
                                bodyPadding: 20
                            }
                        },
                        {
                            region: 'east',
                            xtype: 'container',
                            flex: 1,
                            layout: 'fit',
                            padding: 0,
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'textarea',
                                    id: 'output'
                                }
                            ]
                        }
                    ]
                }
            ]
        });
    }
});
