Ext.define('Admin.lib.formitem.Base', {
    extend: 'Ext.form.FieldContainer',
    label: '',

    inputConfig: undefined,

    initComponent: function () {
        var me = this;

        me.defaults = {
            margin: '0 0 1 0',
            width: 360
        };
        me.callParent(arguments);
    },

    getValue: function () {
        return this.items.items[0].getValue();
    }

});