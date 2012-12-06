Ext.define('ContentDataPanel', {
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

        console.time('RenderForm');
        me.mixins.formHelper.addFormItems(me.contentType.form, me);
        console.timeEnd('RenderForm');

        me.callParent(arguments);
    },


    /**  Temporary form submit button (for development only) **/

    buttonAlign: 'left',
    buttons: [
        {
            text: 'Submit',
            formBind: true, //only enabled once the form is valid
            disabled: true,
            handler: function () {
                var formPanel = this.up('form');
                var form = formPanel.getForm();
                if (form.isValid()) {
                    formPanel.createFormData();
                }
            }
        }
    ],

    getData: function () {
        this.createFormData();
    },


    createFormData: function () {
        var me = this;
        var formItems = me.items.items;

        Ext.Array.each(formItems, function (formItem, index) {
            if (formItem.getXType() === 'FormItemSet') {
                me.createFormItemSetData(formItem);
            } else {
                console.log(formItem.name + ': ' + formItem.getValue());
            }
        });
    },


    createFormItemSetData: function (formItemSet) {
        //TODO: Recurse
        var blocks = Ext.ComponentQuery.query('container[formItemSetBlock=true]', formItemSet),
            name = formItemSet.name,
            blockIndex;

        Ext.Array.each(blocks, function (block, index) {
            blockIndex = index;
            Ext.Array.each(block.items.items, function (item) {
                if (item.cls !== 'header') {
                    console.log(name + '[' + blockIndex + '].' + item.name + ': ' + item.getValue());
                }
            });
        });
    }

});