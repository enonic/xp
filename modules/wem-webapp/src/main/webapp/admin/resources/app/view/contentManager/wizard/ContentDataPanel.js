Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    requires: [
        'Admin.lib.formitem.FormItemSet',
        'Admin.lib.formitem.HtmlArea',
        'Admin.lib.formitem.Relation',
        'Admin.lib.formitem.TextArea',
        'Admin.lib.formitem.TextLine'
    ],

    mixins: {
        formHelper: 'Admin.lib.formitem.FormHelper'
    },

    layout: 'vbox',

    contentType: undefined,

    content: null, // content to be edited

    autoDestroy: true,

    jsonSubmit: true,

    initComponent: function () {
        var me = this;
        me.items = [];

        if (me.content) {
            me.mixins.formHelper.addFormItemsForEditForm(me.content, me.contentType, me);
        } else {
            me.mixins.formHelper.addFormItemsForNewForm(me.contentType.form, me);
        }

        me.callParent(arguments);
    },


    getData: function () {
        return this.createFormData();
    },


    createFormData: function () {
        var me = this,
            formData = {},
            formItems = me.items.items;

        Ext.Array.each(formItems, function (formItem) {
            if (formItem.getXType() === 'FormItemSet') {
                me.addFormItemSetData(formItem, formData);
            } else {
                //console.log(formItem.name + ': ' + formItem.getValue());
                formData[formItem.name] = formItem.getValue();
            }
        });

        return formData;
    },


    addFormItemSetData: function (formItemSet, formData) {
        //TODO: Recursive
        var blocks = Ext.ComponentQuery.query('container[formItemSetBlock=true]', formItemSet),
            formItemSetName = formItemSet.name,
            blockIndex;

        Ext.Array.each(blocks, function (block, index) {
            blockIndex = index;
            Ext.Array.each(block.items.items, function (item) {
                if (item.cls !== 'header') {
                    formData[formItemSetName + '[' + blockIndex + '].' + item.name] = item.getValue();
                    //console.log(name + '[' + blockIndex + '].' + item.name + ': ' + item.getValue());
                }
            });
        });
    }

});