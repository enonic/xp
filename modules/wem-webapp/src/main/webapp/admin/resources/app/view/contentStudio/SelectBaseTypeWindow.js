Ext.define('Admin.view.contentStudio.SelectBaseTypeWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.selectBaseTypeWindow',

    requires: [
    ],

    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,

    width: 800,
    height: 560,
    padding: 20,

    layout: 'border',

    dataViewItemTemplate: '<tpl for=".">' +
                          '<div class="admin-data-view-row">' +
                          '<div class="admin-data-view-thumbnail">' +
                          '<img src="{iconUrl}?size=32"/>' +
                          '</div>' +
                          '<div class="admin-data-view-description">' +
                          '<h6>{name}</h6>' +
                          '</div>' +
                          '<div class="x-clear"></div>' +
                          '</div>' +
                          '</tpl>',


    initComponent: function () {
        var me = this;

        var baseDataView = {
            xtype: 'dataview',
            cls: 'admin-data-view',
            tpl: me.dataViewItemTemplate,
            itemSelector: '.admin-data-view-row',
            trackOver: true,
            overItemCls: 'x-item-over',
            listeners: {
                itemclick: function (dataview, record, item, index, e, opts) {
                    me.fireEvent('contentTypeSelected', me, record);
                }
            },
            store: Ext.create('Ext.data.Store', {
                data: [
                    {name: 'ContentType'},
                    {name: 'RelationshipType'},
                    {name: 'Mixin'}
                ]
            })
        };

        this.items = [
            {
                region: 'north',
                xtype: 'component',
                tpl: '<h2>{title}</h2><p>{subtitle}</p>',
                data: {
                    title: 'Select kind',
                    subtitle: 'You are creating a new type'
                },
                margin: '0 0 20 0'
            },
            {
                region: 'center',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    {
                        flex: 1,
                        overflowY: 'auto',
                        border: false,
                        items: [
                            baseDataView
                        ]
                    }
                ]
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


