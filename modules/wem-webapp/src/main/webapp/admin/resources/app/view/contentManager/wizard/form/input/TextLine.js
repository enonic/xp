Ext.define('Admin.view.contentManager.wizard.form.input.TextLine', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextLine',
    initComponent: function () {
        var me = this;

        this.items = [
            {
                xtype: 'textfield',
                name: this.name,
                value: this.value,
                enableKeyEvents: true,
                listeners : {
                    change : function (f, e) {
                        me.up('contentWizardPanel').onContentInputChanged (f, e);
                    }
                }
            }
        ];

        this.callParent(arguments);
    },

    setValue: function (value) {
        this.down('textfield').setValue(value);
    }
});
