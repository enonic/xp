Ext.define('Admin.view.contentManager.wizard.form.input.TextLine', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextLine',
    label: 'Text Line',
    initComponent: function () {
        var me = this;

        me.items = [
            {
                xtype: 'textfield',
                name: me.name,
                value: me.value
            }
        ];

        me.callParent(arguments);
    },

    setValue: function (value) {
        this.down('textfield').setValue(value);
    }
});