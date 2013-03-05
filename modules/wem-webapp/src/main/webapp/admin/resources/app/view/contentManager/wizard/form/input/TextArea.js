Ext.define('Admin.view.contentManager.wizard.form.input.TextArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextArea',
    label: 'Text Area',
    initComponent: function () {
        var me = this;
        this.items = [
            {
                xtype: 'textarea',
                name: this.name,
                value: this.value,
                enableKeyEvents: true,
                listeners: {
                    change: function (f, e) {
                        me.up('contentWizardPanel').onFormInputChanged(f, e);
                    }
                }
            }
        ];

        this.callParent(arguments);
    },

    setValue: function (value) {
        this.down('textarea').setValue(value);
    }
});