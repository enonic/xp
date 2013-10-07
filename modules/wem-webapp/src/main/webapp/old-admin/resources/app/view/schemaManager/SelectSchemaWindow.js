Ext.define('Admin.view.schemaManager.SelectSchemaWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.selectSchemaWindow',

    requires: [
    ],

    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,

    width: 600,
    height: 360,
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

        Ext.define('Admin.model.schemaManager.Schema', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'name', type: 'string' },
                'iconUrl'
            ]
        });

        var schemaStore = Ext.create('Ext.data.Store', {
            model: 'Admin.model.schemaManager.Schema',
            data: [
                {
                    name: 'ContentType',
                    iconUrl: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/schema/image/ContentType:system:structured')
                },
                {
                    name: 'RelationshipType',
                    iconUrl: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/schema/image/RelationshipType:_:_') // default icon for RelationshipType
                },
                {
                    name: 'Mixin',
                    iconUrl: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/schema/image/Mixin:_:_') // default icon for Mixin
                }
            ]
        });

        var schemaDataView = {
            xtype: 'dataview',
            cls: 'admin-data-view',
            itemId: 'basetypeList',
            tpl: me.dataViewItemTemplate,
            itemSelector: '.admin-data-view-row',
            trackOver: true,
            overItemCls: 'x-item-over',
            store: schemaStore,
            listeners: {
                itemclick: function (view, record, item, index, e, eOpts) {
                    me.fireEvent('createNewSchema', me, record);
                }
            }
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
                border: false,
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
                            schemaDataView
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
                        text: 'Cancel',
                        handler: function (btn, evt) {
                            me.close();
                        }
                    }
                ]
            }
        ];

        this.callParent(arguments);

        this.addEvents('createNewSchema');
    }


});


