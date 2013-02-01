Ext.define('Admin.view.contentManager.wizard.form.FieldSetLayout', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.FieldSetLayout',

    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },

    contentTypeItemConfig: undefined,

    content: null, // Blocks

    listeners: {
        render: function () {
            this.initLayout();
        }
    },


    initComponent: function () {
        var me = this;

        me.title = me.contentTypeItemConfig.label;

        me.defaults = {
        };

        me.callParent(arguments);
    },


    initLayout: function () {
        var me = this;

        if (me.content) {
            me.mixins.formGenerator.addComponentsBasedOnContentData(me.content[0].value, me.contentTypeItemConfig.items, me);
        } else {
            me.mixins.formGenerator.addComponentsBasedOnContentType(me.contentTypeItemConfig.items, me);
        }
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