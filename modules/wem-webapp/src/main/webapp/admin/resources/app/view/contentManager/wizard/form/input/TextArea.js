Ext.define('Admin.view.contentManager.wizard.form.input.TextArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextArea',
    label: 'Text Area',
    initComponent: function () {

        this.items = [
            {
                xtype: 'textarea',
                name: this.name,
                value: this.value
            }
        ];

        this.callParent(arguments);
    },

    setValue: function (value) {
        this.down('textarea').setValue(value);
    }
});