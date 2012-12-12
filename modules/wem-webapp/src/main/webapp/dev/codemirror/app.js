Ext.application({
    name: 'App',

    controllers: [
    ],

    requires: [
        'Admin.lib.CodeMirror'
    ],

    launch: function () {
        Ext.create('Ext.container.Viewport', {
            padding: 10,
            layout: 'fit',
            items: [
                {
                    layout: 'border',
                    border: false,
                    items: [
                        {
                            region: 'center',
                            xtype: 'form',
                            border: false,
                            items: [
                                {
                                    xtype: 'component',
                                    html: '<h2>Code Mirror</h2><p>Code Mirror for ExtJS forms. Extends <a href="http://docs.sencha.com/ext-js/4-1/#!/api/Ext.form.field.TextArea">Ext.form.field.TextArea</a></p>' +
                                          '<p>xtype: codemirror</p>'
                                },
                                {
                                    xtype: 'codemirror',
                                    fieldLabel: 'Code Mirror',
                                    id: 'codeMirrorTest',
                                    value: '<data>\n\t<item id="1">Fisk</item>\n</data>',
                                    width: 500
                                },
                                {
                                    xtype: 'button',
                                    text: 'Get Value',
                                    margin: '0 5 0 0',
                                    handler: function () {
                                        alert(Ext.getCmp('codeMirrorTest').getValue());
                                    }
                                },
                                {
                                    xtype: 'button',
                                    text: 'Set Value',
                                    handler: function () {
                                        Ext.getCmp('codeMirrorTest').setValue('<data>\n\t<item id="2">Ost</item>\n</data>');
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        });
    }
});
