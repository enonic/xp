/**
 * TODO: The create and add methods should be moved to a helper class or to the formitem
 */

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

    initComponent: function () {
        var me = this;
        me.items = [];

        me.mixins.formHelper.addFormItems(me.contentType.form, me);
        me.callParent(arguments);
    },


    /**  Temporary form submit button (for development only)**/

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
                    alert('Create form data and submit');
                }
            }
        }
    ]

});