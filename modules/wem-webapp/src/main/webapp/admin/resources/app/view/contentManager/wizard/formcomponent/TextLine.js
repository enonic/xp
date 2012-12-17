Ext.define('Admin.view.contentManager.wizard.formcomponent.TextLine', {
    extend: 'Admin.view.contentManager.wizard.formcomponent.Base',
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