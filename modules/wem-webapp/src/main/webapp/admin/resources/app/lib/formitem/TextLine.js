Ext.define('Admin.lib.formitem.TextLine', {
    extend: 'Admin.lib.formitem.Base',
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
    }
});