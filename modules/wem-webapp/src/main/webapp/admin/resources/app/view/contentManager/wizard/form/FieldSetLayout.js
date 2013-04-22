Ext.define('Admin.view.contentManager.wizard.form.FieldSetLayout', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.FieldSetLayout',

    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },

    fieldSetLayoutConfig: undefined,

    content: null, // Blocks

    padding: '0 0 0 0',

    initComponent: function () {
        this.title = this.fieldSetLayoutConfig.label;

        this.callParent(arguments);
        this.initLayout();
    },


    initLayout: function () {
        this.addComponentsBasedOnContentType(this.fieldSetLayoutConfig.items, this, this.content);
    },

    getValue: function () {
        var value = [];
        this.items.each(function (item) {
            var currentItemValue = item.getValue();
            value = value.concat(currentItemValue);
        });
        return value;
    }

});