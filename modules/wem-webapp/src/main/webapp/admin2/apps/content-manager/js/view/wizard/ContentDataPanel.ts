Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    requires: [
        'Admin.view.contentManager.wizard.form.FieldSetLayout',
        'Admin.view.contentManager.wizard.form.FormItemSet',
        'Admin.view.contentManager.wizard.form.input.HtmlArea',
        'Admin.view.contentManager.wizard.form.input.Relationship',
        'Admin.view.contentManager.wizard.form.input.TextArea',
        'Admin.view.contentManager.wizard.form.input.TextLine',
        'Admin.view.contentManager.wizard.form.input.Image',
        'Admin.view.contentManager.wizard.form.input.ImageSelector',
        'Admin.view.contentManager.wizard.form.InputContainer',
        'Admin.view.contentManager.wizard.form.FormItemSetContainer'
    ],
    mixins: {
        formGenerator: 'Admin.view.contentManager.wizard.form.FormGenerator'
    },
    maxWidth: 680,
    contentType: undefined,

    bodyPadding: '0 0 100 0',

    content: null, // content to be edited

    jsonSubmit: true,

    autoDestroy: true,

    listeners: {
        afterlayout: function () {
            //Set height to auto manually, so we have bottom padding. For some reason you can't set it through config
            this.setBodyStyle('height', 'auto');
        }
    },

    initComponent: function () {
        this.items = [];
        var contentData = !Ext.isEmpty(this.content) ? this.content.data : undefined;
        this.addComponentsBasedOnContentType(this.contentType.form, this, contentData);

        this.callParent(arguments);
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
            var currentItemValue = (<any> item).getValue();
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