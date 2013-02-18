Ext.define('Admin.view.contentManager.DetailToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentDetailToolbar',

    cls: 'admin-toolbar',

    requires: [
        'Ext.ux.form.field.ToggleSlide'
    ],

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {
        var me = this;
        this.items = [

            {
                text: 'Publish',
                action: 'publishContent'
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
                text: 'Relations'
            },
            {
                text: 'History'
            },

            {
                text: 'View'
            },

            {
                text: 'Export'
            },
            '->',
            {
                xtype: 'toggleslide',
                onText: 'Live',
                offText: 'Form',
                action: 'toggleLive',
                state: this.isLiveMode,
                listeners: {
                    change: function(toggle, state) {
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
