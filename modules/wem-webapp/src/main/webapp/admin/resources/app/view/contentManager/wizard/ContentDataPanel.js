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

        Ext.Array.each(formItems, function (item) {
            if (item.getXType() === 'FormItemSet') {
                me.addFormItemSetContentData(item, contentData, '');
            } else if (item.getXType() === 'FieldSetLayout') {
                me.addLayoutData(item, contentData);
            } else {
                contentData[item.name] = item.getValue();
            }
        });

        return contentData;
    },


    addLayoutData: function (layoutComponent, contentData) {
        var me = this;
        var items = layoutComponent.items.items;
        var layoutName = layoutComponent.name.concat('[0]');

        Ext.Array.each(items, function (item, index) {
            if (item.getXType() === 'FormItemSet') {
                me.addFormItemSetContentData(item, contentData, layoutName);
            } else if (item.getXType() === 'FieldSetLayout') {
                me.addLayoutData(item, contentData);
            } else {
                if (item.cls !== 'header') {
                    contentData[layoutName.concat('.', item.name)] = item.getValue();
                }
            }
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
                    me.addFormItemSetContentData(item, contentData, formItemSetName);
                } else if (item.getXType() === 'FieldSetLayout') {
                    me.addLayoutData(item, contentData);
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