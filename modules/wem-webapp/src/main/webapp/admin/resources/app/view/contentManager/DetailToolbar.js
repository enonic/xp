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
