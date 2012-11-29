Ext.define('Admin.lib.inputtypes.FormItemSet', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.input.FormItemSet',

    initComponent: function () {
        var me = this;

        me.title = me.fieldLabel;

        me.callParent(arguments);
    }

});