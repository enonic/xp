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
            me.mixins.formGenerator.addComponentsBasedOnContentData(me.content.value[0], me.contentTypeItemConfig.items, me);
        } else {
            me.mixins.formGenerator.addComponentsBasedOnContentType(me.contentTypeItemConfig.items, me);
        }
    }

});