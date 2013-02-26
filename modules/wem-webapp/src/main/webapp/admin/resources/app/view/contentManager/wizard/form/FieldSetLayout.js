Ext.define('Admin.view.contentManager.wizard.form.FieldSetLayout', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.FieldSetLayout',

    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },

    fieldSetLayoutConfig: undefined,

    content: null, // Blocks

    listeners: {
        render: function () {
            this.initLayout();
        }
    },


    initComponent: function () {
        this.title = this.fieldSetLayoutConfig.label;

        this.callParent(arguments);
    },


    initLayout: function () {
        var contentData = !Ext.isEmpty(this.content) ? this.content[0].value : undefined;
        this.addComponentsBasedOnContentType(this.fieldSetLayoutConfig.items, this, contentData);
    },

    getValue: function () {
        var value = [];
        Ext.each(this.items, function (item) {
            var currentItemValue = item.getValue();
            value = value.concat(currentItemValue);
        });
        return value;
    }

});