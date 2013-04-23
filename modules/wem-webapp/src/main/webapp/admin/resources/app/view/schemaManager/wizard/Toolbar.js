Ext.define('Admin.view.schemaManager.wizard.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.schemaManagerWizardToolbar',

    cls: 'admin-toolbar',

    border: false,

    isNew: true,

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
            schema: me.schema
        };

        var deleteBtn = {
            text: 'Delete',
            action: 'deleteType',
            schema: me.schema
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
