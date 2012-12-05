Ext.define('Admin.lib.formitem.Base', {
    extend: 'Ext.form.FieldContainer',
    label: '',

    inputConfig: undefined,

    initComponent: function () {
        var me = this;

        me.defaults = {
            margin: '0 0 1 0',
            width: 400
        };
        me.callParent(arguments);
    }

});