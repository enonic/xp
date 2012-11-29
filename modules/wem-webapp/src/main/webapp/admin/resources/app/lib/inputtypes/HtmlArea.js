Ext.define('Admin.lib.inputtypes.HtmlArea', {
    extend: 'Admin.lib.inputtypes.Base',
    alias: 'widget.input.HtmlArea',
    initComponent: function () {
        var me = this;

        me.items = [
            {
                xtype: 'htmleditor',
                name: me.name
            }
        ];

        me.callParent(arguments);
    }

});