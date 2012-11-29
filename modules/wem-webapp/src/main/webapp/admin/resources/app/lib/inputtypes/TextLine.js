Ext.define('Admin.lib.inputtypes.TextLine', {
    extend: 'Admin.lib.inputtypes.Base',
    alias: 'widget.input.TextLine',
    label: 'Text Line',
    initComponent: function () {
        var me = this;

        me.items = [
            {
                xtype: 'textfield',
                name: me.name
            }
        ];

        me.callParent(arguments);
    }
});