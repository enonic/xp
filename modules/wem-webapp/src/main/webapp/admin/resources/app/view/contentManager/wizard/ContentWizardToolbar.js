Ext.define('Admin.view.contentManager.wizard.ContentWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentWizardToolbar',

    border: false,

    cls: 'admin-toolbar',

    isNewGroup: true,

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {

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
                text: 'Form Edit',
                action: 'cycleMode',
                iconCls: 'icon-keyboard-key-24'
            },

            {
                text: 'Close',
                action: 'closeWizard'
            }

        ];
        this.callParent(arguments);
    }

});
