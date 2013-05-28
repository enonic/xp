Ext.define('Admin.view.contentManager.open.OpenToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentOpenToolbar',

    cls: 'admin-toolbar',

    isLiveMode: false,

    requires: [
        'Ext.ux.toggleslide.ToggleSlide'
    ],

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {
        var me = this;
        this.items = [

            {
                text: 'New',
                action: 'newContent'
            },

            {
                text: 'Edit',
                action: 'editContent'
            },
            {
                text: 'Delete',
                action: 'deleteContent'
            },

            {
                text: 'Duplicate',
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                action: 'moveContent'
            },
            {
                text: 'Export'
            },
            '->',
            {
                xtype: 'cycle',
                itemId: 'deviceCycle',
                disabled: !me.isLiveMode,
                showText: true,
                prependText: 'Device: ',
                menu: {
                    items: [
                        {
                            text: 'Desktop',
                            checked: true,
                            device: 'DESKTOP'
                        },
                        {
                            text: 'iPhone 5 Vertical',
                            device: 'IPHONE_5_VERTICAL'
                        },
                        {
                            text: 'iPhone 5 Horizontal',
                            device: 'IPHONE_5_HORIZONTAL'
                        },
                        {
                            text: 'iPad 3 Vertical',
                            device: 'IPAD_3_VERTICAL'
                        },
                        {
                            text: 'iPad 3 Horizontal',
                            device: 'IPAD_3_HORIZONTAL'
                        }
                    ]
                }
            },
            {
                xtype: 'toggleslide',
                onText: 'Preview',
                offText: 'Details',
                action: 'toggleLive',
                state: me.isLiveMode,
                listeners: {
                    change: function (toggle, state) {
                        me.isLiveMode = state;
                    }
                }
            },
            {
                text: 'Close',
                action: 'closeContent'
            }
        ];

        this.callParent(arguments);
    }

});
