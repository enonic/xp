Ext.define('Admin.lib.formitem.HtmlArea', {
    extend: 'Admin.lib.formitem.Base',
    alias: 'widget.HtmlArea',
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