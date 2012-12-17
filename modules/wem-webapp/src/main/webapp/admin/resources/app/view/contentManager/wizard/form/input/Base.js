Ext.define('Admin.view.contentManager.wizard.form.input.Base', {
    extend: 'Ext.form.FieldContainer',
    label: '',

    inputConfig: undefined,

    initComponent: function () {
        var me = this;

        me.defaults = {
            margin: '0 0 5 0',
            width: 450
        };
        me.callParent(arguments);
    },

    getValue: function () {
        return this.items.items[0].getValue();
    }

});