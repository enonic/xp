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
        this.items = [

            {
                text: 'Save',
                itemId: 'save',
                action: 'saveContent'
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
                text: 'Relations',
                itemId: 'relations',
                action: 'contentRelations'
            },
            {
                text: 'History',
                itemId: 'history',
                action: 'contentHistory'
            },

            {
                text: 'Export',
                itemId: 'export',
                action: 'exportContent'
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
