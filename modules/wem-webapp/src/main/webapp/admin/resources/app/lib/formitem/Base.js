Ext.define('Admin.lib.formitem.Base', {
    extend: 'Ext.form.FieldContainer',
    label: '',
    occurrences: null,
    initComponent: function () {
        var me = this;

        me.defaults = {
            margin: '0 0 1 0',
            width: 600
        };
        me.callParent(arguments);
    }

});