Ext.define('Admin.view.contentManager.LiveEditTestWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.liveEditTestWindow',

    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,

    width: 800,
    height: 560,
    padding: 20,

    layout: 'border',

    initComponent: function () {
        var me = this;

        this.items = [
            {
                region: 'north',
                xtype: 'component',
                tpl: '<h2>{title}</h2><p>{subtitle}</p>',
                data: {
                    title: 'Upsale Teaser',
                    subtitle: 'enonic:upsale-teaser'
                },
                margin: '0 0 0 0'
            },
            {
                region: 'center',
                border: 0,
                html: '<img src="resources/images/dummy-settings.png"/>'
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
                        ui: 'dark-grey',
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

