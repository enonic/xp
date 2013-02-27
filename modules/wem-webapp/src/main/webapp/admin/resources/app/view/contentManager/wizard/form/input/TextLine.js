Ext.define('Admin.view.contentManager.wizard.form.input.TextLine', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextLine',
    initComponent: function () {

        this.items = [
            {
                xtype: 'textfield',
                name: this.name,
                value: this.value
            }
        ];

        this.callParent(arguments);
    },

    setValue: function (value) {
        this.down('textfield').setValue(value);
    }
});