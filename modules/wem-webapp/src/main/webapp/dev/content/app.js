Ext.application({
    name: 'App',

    controllers: [
    ],

    requires: [
        'ContentDataPanel',
        'Admin.lib.inputtypes.HtmlArea',
        'Admin.lib.inputtypes.Relation',
        'Admin.lib.inputtypes.TextLine',
        'Admin.lib.inputtypes.TextArea'
    ],

    launch: function () {
        function loadContentType(file) {
            Ext.Ajax.request({
                url: file,
                success: function (response) {
                    var viewport = Ext.ComponentQuery.query('viewport')[0];
                    var center = Ext.getCmp('center');

                    var renderTimeStart = new Date().getTime(),
                        renderTimeStop;

                    center.removeAll();

                    var json = Ext.JSON.decode(response.responseText, true);
                    var contentDataPanel = new ContentDataPanel({
                        border: false,
                        contentTypeItems: json.contentType.items
                    });

                    center.add(contentDataPanel);

                    renderTimeStop = new Date().getTime();
                    var totalTime = renderTimeStop - renderTimeStart;

                    Ext.getCmp('output').setValue(json.contentType.qualifiedName + '\n\nTotal load and render time: ' + totalTime + 'ms\n\nJSON:\n\n' + response.responseText);
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
                            height: 40,
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
                                            {file: 'mock-contenttype-textarea.json', qualifiedName: 'Demo:Article'},
                                            {file: 'mock-contenttype-set.json', qualifiedName: 'Demo:Set'}
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
                                            combo.setValue('mock-contenttype-relation.json');
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
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'textarea',
                                    fieldStyle: 'background: #f7f7f7;padding: 10px;',
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
