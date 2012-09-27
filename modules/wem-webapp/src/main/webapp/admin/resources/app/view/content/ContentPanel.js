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
                        fieldLabel: 'Content field1',
                        value: '',
                        name: 'contentField1'
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Content field1',
                        value: '',
                        name: 'contentField2'
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
        var field1Value = form.findField('contentField1').getValue();
        var field2Value = form.findField('contentField2').getValue();
        var data = {
            'contentField1': field1Value,
            'contentField2': field2Value
        };
        return data;
    },

    onSendClick: function () {
        var data = this.getData();
        console.dir(data);

        Admin.lib.RemoteService.content_createOrUpdate({ "content": data }, function (rpcResp) {
            if (rpcResp.success) {
                if (rpcResp.success) {
                    handleRpcResponse(rpcResp);
                }
            }
        });
    }

});
