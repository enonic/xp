Ext.define('Admin.view.NotifyUserWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.notifyUserWindow',

    dialogTitle: 'Notify User',
    items: [
        {
            xtype: 'form',
            itemId: 'notifyForm',
            method: 'POST',
            autoHeight: true,
            border: false,
            url: 'data/account/notify',
            bodyPadding: '5px 0 0',
            items: [
                {
                    xtype: 'fieldset',
                    margin: 0,
                    title: 'Message',
                    defaults: {
                        xtype: 'textfield',
                        allowBlank: false,
                        validateOnChange: true,
                        labelClsExtra: 'admin-form-label'
                    },
                    items: [
                        {
                            fieldLabel: 'To <span>*</span>',
                            name: 'to',
                            itemId: 'to',
                            anchor: '100%',
                            allowBlank: false
                        },
                        {
                            fieldLabel: 'Cc',
                            name: 'cc',
                            allowBlank: true,
                            anchor: '100%'
                        },
                        {
                            fieldLabel: 'Subject <span>*</span>',
                            itemId: 'subject',
                            anchor: '100%',
                            name: 'subject'
                        },
                        {
                            fieldLabel: 'Message <span>*</span>',
                            itemId: "message",
                            name: 'message',
                            xtype: 'textarea',
                            anchor: '100%',
                            rows: 3,
                            allowBlank: false
                        },
                        {
                            margin: '0 0 10px 105px',
                            formBind: true,
                            xtype: 'button',
                            scale: 'medium',
                            text: 'Send',
                            iconCls: 'icon-btn-tick-24',
                            action: 'send'
                        }
                    ]
                }
            ]
        }
    ],

    doShow: function (model) {
        this.callParent(arguments);
        var formPanel = this.down('#notifyForm');
        formPanel.getForm().reset();
        formPanel.down('#to').setValue(model.data.email);
        formPanel.down('#subject').setValue(model.subject);
        formPanel.down('#message').setValue(model.message);


    }

});
