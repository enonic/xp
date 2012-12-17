Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    requires: [
        'Admin.view.contentManager.wizard.form.FieldSetLayout',
        'Admin.view.contentManager.wizard.form.FormItemSet',
        'Admin.view.contentManager.wizard.form.input.HtmlArea',
        'Admin.view.contentManager.wizard.form.input.Relation',
        'Admin.view.contentManager.wizard.form.input.TextArea',
        'Admin.view.contentManager.wizard.form.input.TextLine'
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

        if (me.content) {
            me.mixins.formGenerator.addComponentsBasedOnContentData(me.content.data, me.contentType.form, me);
        } else {
            me.mixins.formGenerator.addComponentsBasedOnContentType(me.contentType.form, me);
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
     * TODO: Refactor to a new class, ContentDataBuilder
     */
    buildContentData: function () {
        var me = this;
        var contentData = {};
        var formItems = me.items.items;

        Ext.Array.each(formItems, function (formItem) {
            if (formItem.getXType() === 'FormItemSet') {
                me.addFormItemSetContentData(formItem, contentData, '');
            } else if (formItem.getXType() === 'FieldSetLayout') {
                me.addLayoutData(formItem, contentData);
            } else {
                contentData[formItem.name] = formItem.getValue();
            }
        });

        return contentData;
    },


    addLayoutData: function (layoutComponent, contentData) {
        var items = layoutComponent.items.items;
        var layoutName = layoutComponent.name;

        Ext.Array.each(items, function (item, index) {
            contentData[layoutName.concat('[0].', item.name)] = item.getValue();
        });
    },


    addFormItemSetContentData: function (formItemSetItem, contentData, parentName) {
        var me = this;
        var blocks = me.getFormItemSetBlocks(formItemSetItem);

        Ext.Array.each(blocks, function (block, index) {

            var blockItems = block.items.items;

            Ext.Array.each(blockItems, function (item) {
                var formItemSetName = '';
                if (parentName !== '') {
                    formItemSetName = parentName + '.'; // Eg. contact_info[0]
                }
                formItemSetName = formItemSetName.concat(formItemSetItem.name, '[', index, ']');

                if (item.getXType() === 'FormItemSet') {
                    // Recursive
                    me.addFormItemSetContentData(item, contentData, formItemSetName);
                } else {
                    if (item.cls !== 'header') {
                        contentData[formItemSetName.concat('.', item.name)] = item.getValue();
                    }
                }
            });
        });
    },


    getFormItemSetBlocks: function (formItemSetComponent) {
        var blocks = [];
        Ext.Array.each(formItemSetComponent.items.items, function (item, index) {
            if (item.cls && item.cls.indexOf('admin-formitemset-block') > -1) {
                blocks.push(item);
            }
        });

        return blocks;
    }


});