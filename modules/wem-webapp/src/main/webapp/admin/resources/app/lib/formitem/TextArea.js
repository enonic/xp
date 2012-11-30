Ext.define('Admin.lib.formitem.TextArea', {
    extend: 'Admin.lib.formitem.Base',
    alias: 'widget.TextArea',
    label: 'Text Area',
    initComponent: function () {
        var me = this;

        me.items = [
            {
                xtype: 'textarea',
                name: me.name
            }
        ];

        me.callParent(arguments);
    }
});