Ext.define('Admin.lib.inputtypes.TextArea', {
    extend: 'Admin.lib.inputtypes.Base',
    alias: 'widget.input.TextArea',
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