Ext.define('Admin.view.content.ContentPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.createContentPanel',

    initComponent: function () {
        var me = this;
        me.items = [
            {
                xtype: 'fieldset',
                title: 'Content form test',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        xtype: 'textfield',
                        fieldLabel: 'My textline 1',
                        value: '',
                        name: 'myTextLine1'
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'My textline 2',
                        value: '',
                        name: 'myTextLine2'
                    },
                    {
                        xtype: 'fieldset',
                        title: 'My componentSet',
                        value: '',
                        items: [
                            {
                                xtype: 'textfield',
                                fieldLabel: 'My textline 1',
                                name: 'myComponentSet.myTextLine1'
                            }
                        ]
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Path',
                        value: '/myContent',
                        name: 'contentPath'
                    },
                    {
                        xtype: 'button',
                        itemId: 'sendButton',
                        text: 'Send data',
                        width: 100,
                        listeners: {
                            click: {
                                fn: me.onSendClick,
                                scope: me
                            }
                        }
                    }
                ]
            }
        ];
        me.callParent(arguments);
    },

    getData: function () {
        var form = this.getForm();
        var data = {
            'myTextLine1': form.findField('myTextLine1').getValue(),
            'myTextLine2': form.findField('myTextLine2').getValue(),
            'myComponentSet.myTextLine1': form.findField('myComponentSet.myTextLine1').getValue()
        };
        return data;
    },

    getQualifiedContentTypeName: function () {
        return "mymodule:myContentType";
    },

    getContentPath: function () {
        var form = this.getForm();
        return form.findField('contentPath').getValue();
    },

    onSendClick: function () {
        var json = {
            "contentData": this.getData(),
            "qualifiedContentTypeName": this.getQualifiedContentTypeName(),
            "contentPath": this.getContentPath()
        };
        console.dir(json);

        Admin.lib.RemoteService.content_createOrUpdate(json, function (rpcResp) {
            //if ( rpcResp.success )
            //{
            //    handleRpcResponse( rpcResp );
            //}
        });
    }

});
