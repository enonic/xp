Ext.define('Admin.view.contentManager.wizard.formcomponent.TextArea', {
    extend: 'Admin.view.contentManager.wizard.formcomponent.Base',
    alias: 'widget.TextArea',
    label: 'Text Area',
    initComponent: function () {
        var me = this;

        me.items = [
            {
                xtype: 'textarea',
                name: me.name,
                value: me.value
            }
        ];

        me.callParent(arguments);
    }
});