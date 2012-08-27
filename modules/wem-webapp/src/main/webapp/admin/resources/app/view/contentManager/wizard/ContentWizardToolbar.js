Ext.define('Admin.view.contentManager.wizard.ContentWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentWizardToolbar',

    border: false,

    isNewGroup: true,

    initComponent: function () {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };
        this.items = [
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Save',
                        itemId: 'save',
                        iconCls: 'icon-save-24'
                    },
                    {
                        text: 'Publish',
                        itemId: 'publish',
                        iconCls: 'icon-publish-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Delete',
                        itemId: 'delete',
                        action: 'deleteContent',
                        iconCls: 'icon-delete-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Duplicate',
                        itemId: 'duplicateContent',
                        action: 'duplicateContent',
                        iconCls: 'icon-copy-24'
                    },
                    {
                        text: 'Move',
                        itemId: 'move',
                        iconCls: 'icon-move-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Relations',
                        itemId: 'relations',
                        iconCls: 'icon-relation-24'
                    },
                    {
                        text: 'History',
                        itemId: 'history',
                        iconCls: 'icon-history-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Export',
                        itemId: 'export',
                        iconCls: 'icon-export-24'
                    }
                ]
            },
            '->',
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Form View',
                        action: 'cycleMode',
                        iconCls: 'icon-keyboard-key-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Close',
                        action: 'closeWizard',
                        iconCls: 'icon-cancel-24'
                    }
                ]
            }

        ];
        this.callParent(arguments);
    }

});
