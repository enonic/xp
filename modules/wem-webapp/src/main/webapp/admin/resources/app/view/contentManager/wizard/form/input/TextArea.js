Ext.define('Admin.view.contentManager.wizard.form.input.TextArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
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