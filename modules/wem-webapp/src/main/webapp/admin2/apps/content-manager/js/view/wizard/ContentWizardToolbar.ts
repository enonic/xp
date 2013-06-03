Ext.define('Admin.view.contentManager.wizard.ContentWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentWizardToolbar',

    requires: [
        'Ext.ux.toggleslide.ToggleSlide'
    ],

    border: false,

    cls: 'admin-toolbar',

    isNewGroup: true,
    isLiveMode: false,

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {
        var me = this;
        this.items = <any[]>[

            {
                text: 'Save',
                itemId: 'save',
                action: 'saveContent'
            },
            {
                text: 'Preview',
                itemId: 'preview',
                action: 'previewContent'
            },
            {
                text: 'Publish',
                itemId: 'publish',
                action: 'publishContent'
            },

            {
                text: 'Delete',
                itemId: 'delete',
                action: 'deleteContent'
            },

            {
                text: 'Duplicate',
                itemId: 'duplicate',
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                itemId: 'move',
                action: 'moveContent'
            },
            {
                text: 'Export',
                itemId: 'export',
                action: 'exportContent'
            },
            '-',
            {
                text: 'Close',
                action: 'closeWizard'
            },
            '->',
            {
                xtype: 'toggleslide',
                onText: 'Live',
                offText: 'Form',
                action: 'toggleLive',
                state: this.isLiveMode,
                listeners: {
                    change: function (toggle, state) {
                        me.isLiveMode = state;
                    }
                }
            },

            {
                text: 'Close',
                action: 'closeWizard'
            }

        ];
        this.callParent(arguments);
    }

});
