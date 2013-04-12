Ext.define('Admin.view.contentManager.DetailToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentDetailToolbar',

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
                disabled: true,
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
                            text: 'iPhone 5',
                            device: 'IPHONE_5'
                        },
                        {
                            text: 'Galaxy SIII',
                            device: 'GALAXY_S3'
                        },
                        {
                            text: 'iPad 3',
                            device: 'IPAD_3'
                        },
                        {
                            text: 'Kindle Fire HD 7"',
                            device: 'KINDLE_FIRE_HD7'
                        },
                        {
                            text: '1080p Television',
                            device: '1080_P_TELEVISION'
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
