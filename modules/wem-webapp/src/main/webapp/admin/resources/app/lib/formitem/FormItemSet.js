Ext.define('Admin.lib.formitem.FormItemSet', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.FormItemSet',

    initComponent: function () {
        var me = this;

        me.title = me.fieldLabel;

        me.callParent(arguments);
    }

});