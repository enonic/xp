Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    requires: [
        'Admin.view.contentManager.wizard.form.FieldSetLayout',
        'Admin.view.contentManager.wizard.form.FormItemSet',
        'Admin.view.contentManager.wizard.form.input.HtmlArea',
        'Admin.view.contentManager.wizard.form.input.Relation',
        'Admin.view.contentManager.wizard.form.input.TextArea',
        'Admin.view.contentManager.wizard.form.input.TextLine',
        'Admin.view.contentManager.wizard.form.FieldContainer'
    ],
    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },

    contentType: undefined,

    content: null, // content to be edited

    jsonSubmit: true,

    autoDestroy: true,

    initComponent: function () {
        var me = this;
        me.items = [];
        var existingContent = me.content && me.content.data;
        if (existingContent) {
            me.addComponentsBasedOnContentType(me.contentType.form, me, me.content.data);
        } else {
            me.addComponentsBasedOnContentType(me.contentType.form, me);
        }

        me.callParent(arguments);
    },


    getData: function () {
        return this.getContentData();
    },


    getContentData: function () {
        return this.buildContentData();
    },


    /**
     * TODO: Refactor! The following should be moved to another object soon
     */
    buildContentData: function () {
        var me = this,
            formItems = me.items.items,
            contentData = {};
        Ext.Array.each(formItems, function (item) {
            var currentItemValue = item.getValue();
            if (currentItemValue instanceof Array) {
                Ext.each(currentItemValue, function (itemValue) {
                    contentData[itemValue.path] = itemValue.value;
                });
            } else {
                contentData[currentItemValue.path] = currentItemValue.value;
            }
        });

        return contentData;
    }

});