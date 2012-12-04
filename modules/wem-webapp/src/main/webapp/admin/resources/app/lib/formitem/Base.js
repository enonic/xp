Ext.define('Admin.lib.formitem.Base', {
    extend: 'Ext.form.FieldContainer',
    label: 'Label',
    layout: 'anchor',
    width: 440,
    occurrences: null,
    initComponent: function () {
        var me = this;
        me.defaults = {
            width: me.width,
            margin: '0 0 1 0'
        };
        me.callParent(arguments);
    }

});