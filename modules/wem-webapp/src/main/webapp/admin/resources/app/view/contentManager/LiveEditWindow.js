Ext.define('Admin.view.contentManager.LiveEditWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.liveEditWindow',

    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,

    width: 800,
    height: 560,
    padding: 20,

    layout: 'border',

    initComponent: function () {
        var me = this;

        Admin.MessageBus.on('liveEditWindow.show', function () {
            me.doShow();
        }, me);

        this.items = [
            {
                region: 'north',
                xtype: 'component',
                tpl: '<h2>{title}</h2><p>{subtitle}</p>',
                data: {
                    title: 'Live edit window',
                    subtitle: 'this is live editing'
                },
                margin: '0 0 20 0'
            },
            {
                region: 'south',
                margin: '20 0 0 0',
                border: false,
                layout: {
                    type: 'hbox',
                    pack: 'end'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Confirm',
                        margin: '0 10 0 0',
                        handler: function (btn, evt) {
                            me.close();
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Cancel',
                        handler: function (btn, evt) {
                            me.close();
                        }
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }


});

