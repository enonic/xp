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

    contentType: undefined,

    content: null, // content to be edited

    jsonSubmit: true,

    layout: 'vbox',

    autoDestroy: true,

    initComponent: function () {
        var me = this;
        me.items = [];

        if (me.content) {
            me.mixins.formHelper.addComponentsForEditForm(me.content, me.contentType, me);
        } else {
            me.mixins.formHelper.addComponentsForNewForm(me.contentType.form, me);
        }

        me.callParent(arguments);
    },


    getData: function () {
        return this._buildContentData();
    },


    _buildContentData: function () {
        var me = this,
            contentData = {},
            components = me.items.items;

        Ext.Array.each(components, function (component) {
            if (component.getXType() === 'FormItemSet') {
                me._addFormItemSetContentData(component, contentData);
            } else {
                contentData[component.name] = component.getValue();
            }
        });

        return contentData;
    },


    _addFormItemSetContentData: function (formItemSetComponent, contentData) {
        //TODO: Recursive
        var blocks = Ext.ComponentQuery.query('container[formItemSetBlock=true]', formItemSetComponent),
            formItemSetName = formItemSetComponent.name;

        Ext.Array.each(blocks, function (block, index) {
            Ext.Array.each(block.items.items, function (item) {
                if (item.cls !== 'header') {
                    contentData[formItemSetName + '[' + index + '].' + item.name] = item.getValue();
                }
            });
        });
    }

});