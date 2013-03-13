Ext.define('Admin.view.contentManager.wizard.form.input.TextArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.TextArea',
    label: 'Text Area',


    initComponent: function () {
        this.items = [
            {
                xtype: 'textarea',
                displayNameSource: true,   // property to select components taking part in auto generation
                name: this.name,
                value: this.value,
                enableKeyEvents: true
            }
        ];

        this.callParent(arguments);
    },

    setValue: function (value) {
        this.down('textarea').setValue(value);
    }
});