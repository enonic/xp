Ext.define('Admin.view.contentManager.wizard.form.input.HtmlArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.HtmlArea',
    initComponent: function () {
        var me = this;

        me.items = [
            {
                xtype: 'htmleditor',
                name: me.name,
                value: me.value
            }
        ];

        me.callParent(arguments);
    }

});