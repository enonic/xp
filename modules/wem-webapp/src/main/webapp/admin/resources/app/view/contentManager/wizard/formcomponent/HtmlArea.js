Ext.define('Admin.view.contentManager.wizard.formcomponent.HtmlArea', {
    extend: 'Admin.view.contentManager.wizard.formcomponent.Base',
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