Ext.define('Admin.view.contentStudio.wizard.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentStudioWizardToolbar',

    cls: 'admin-toolbar',

    border: false,

    isNewGroup: true,

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {
        var me = this;
        var saveBtn = {
            text: 'Save',
            action: 'saveType',
            itemId: 'save',
            disabled: false, //true, TODO disable by default, enable when modified
            baseType: me.baseType
        };

        var deleteBtn = {
            text: 'Delete',
            action: 'deleteType',
            baseType: me.baseType
        };

        var closeBtn = {
            text: 'Close',
            action: 'closeWizard'
        };

        if (!this.isNew) {
            this.items = [saveBtn, deleteBtn, '->', closeBtn];
        } else {
            this.items = [ saveBtn, '->', closeBtn ];
        }
        this.callParent(arguments);
    }

});
