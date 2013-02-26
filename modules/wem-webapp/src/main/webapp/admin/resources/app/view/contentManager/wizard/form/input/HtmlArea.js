Ext.define('Admin.view.contentManager.wizard.form.input.HtmlArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.HtmlArea',
    initComponent: function () {

        this.items = [
            {
                xtype: 'htmleditor',
                name: this.name,
                value: this.value
            }
        ];

        this.callParent(arguments);
    },

    setValue: function (value) {
        this.down('htmleditor').setValue(value);
    }

});