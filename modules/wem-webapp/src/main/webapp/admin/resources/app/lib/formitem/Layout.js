Ext.define('Admin.lib.formitem.Layout', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.Layout',

    mixins: {
        formGenerator: 'Admin.lib.formitem.FormGenerator'
    },

    layoutConfig: undefined,

    content: null, // Blocks

    listeners: {
        render: function () {
            this.initLayout();
        }
    },


    initComponent: function () {
        var me = this;

        me.title = me.layoutConfig.label;

        me.defaults = {
        };

        me.callParent(arguments);
    },


    initLayout: function () {
        var me = this;

        if (me.content) {
            me.mixins.formGenerator.addComponentsBasedOnContentData(me.content.value[0], me.layoutConfig.items, me);
        } else {
            me.mixins.formGenerator.addComponentsBasedOnContentType(me.layoutConfig.items, me);
        }
    }

});