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
                text: 'Save',
                action: 'saveSpace',
                itemId: 'save'
                //disabled: true
            },
            {
                text: 'Delete',
                action: 'deleteSpace'
            },
            {
                text: 'Duplicate'
            },
            '->',
            {
                text: 'Close',
                action: 'closeWizard'
            }
        ];
        this.callParent(arguments);
    }

});
