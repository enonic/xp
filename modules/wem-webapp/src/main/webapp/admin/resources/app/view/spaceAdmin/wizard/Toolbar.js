Ext.define('Admin.view.spaceAdmin.wizard.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.spaceAdminWizardToolbar',

    cls: 'admin-toolbar',

    border: false,

    isNewGroup: true,

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {
        this.items = [
            {
                xtype: 'splitbutton',
                text: 'Save',
                action: 'saveSpace'
            },
            {
                text: 'Delete',
                action: 'deleteSpace'
            },
            {
                text: 'Duplicate'
            },
            {
                text: 'Move'
            },
            '->',
            {
                xtype: 'splitbutton',
                text: 'Form Edit'
            },
            {
                text: 'Close',
                action: 'closeWizard'
            }
        ];
        this.callParent(arguments);
    }

});
