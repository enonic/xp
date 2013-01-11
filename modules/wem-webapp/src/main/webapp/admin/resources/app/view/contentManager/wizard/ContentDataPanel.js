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
     * TODO: Refactor! The following should be moved to another object soon
     */
    buildContentData: function () {
        var me = this,
            formItems = me.items.items,
            contentData = {};

        Ext.Array.each(formItems, function (item) {
            if (item.getXType() === 'FormItemSet') {
                me.addDataFromFormItemSet(item, contentData, '');
            } else if (item.getXType() === 'FieldSetLayout') {
                me.addDataFromLayout(item, contentData);
            } else {
                contentData[item.name] = item.getValue();
            }
        });

        return contentData;
    },


    addDataFromLayout: function (layoutComponent, contentData) {
        var me = this;
        var items = layoutComponent.items.items;
        var path = '';

        Ext.Array.each(items, function (item, index) {
            if (item.getXType() === 'FormItemSet') {
                me.addDataFromFormItemSet(item, contentData, path);
            } else if (item.getXType() === 'FieldSetLayout') {
                me.addDataFromLayout(item, contentData);
            } else {
                if (item.cls !== 'header') {
                    contentData[item.name] = item.getValue();
                }
            }

        });
    },


    addDataFromFormItemSet: function (itemSetComponent, contentData, parentPath) {
        var me = this;
        var blocks = me.getFormItemSetBlocks(itemSetComponent),
            blockItems,
            path;

        Ext.Array.each(blocks, function (block, index) {

            blockItems = block.items.items;

            Ext.Array.each(blockItems, function (item) {
                path = '';

                if (parentPath !== '') {
                    path = parentPath + '.'; // Eg. contact_info[0].
                }

                path = path.concat(itemSetComponent.name, '[', index, ']');

                if (item.getXType() === 'FormItemSet') {
                    me.addDataFromFormItemSet(item, contentData, path);
                } else if (item.getXType() === 'FieldSetLayout') {
                    me.addDataFromLayout(item, contentData);
                } else {
                    if (item.cls !== 'header') {
                        path = path.concat('.', item.name);
                        contentData[path] = item.getValue();
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