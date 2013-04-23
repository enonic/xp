Ext.define('Admin.view.schemaManager.DeleteSchemaWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteSchemaWindow',

    dialogTitle: undefined,

    /*    items: [
     {
     margin: '10px 0 10px 0px',
     xtype: 'container',
     defaults: {
     xtype: 'button',
     scale: 'medium',
     margin: '0 10 0 0'
     },
     items: [
     {
     text: 'Delete',
     iconCls: 'icon-delete-user-24',
     itemId: 'deleteContentTypeButton',
     action: 'deleteSchema'
     }
     ]
     }
     ],*/

    initComponent: function () {
        var me = this;
        this.items = [
            me.header('Delete schema(s)'),
            {
                region: 'center',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                border: false,
                items: {
                    itemId: 'modalDialog',
                    cls: 'dialog-info',
                    xtype: 'component',
                    border: false,
                    height: 150,
                    styleHtmlContent: true,
                    tpl: me.deleteTemplate
                }
            },
            me.buttonRow({
                text: 'Delete',
                action: 'deleteSchema'
            }, {
                xtype: 'button',
                text: 'Cancel',
                handler: function (btn, evt) {
                    me.close();
                }
            })
        ];

        this.callParent(arguments);
    },

    deleteTemplate: '<div class="delete-container">' +
                    '<tpl for=".">' +
                    '<div class="delete-item">' +
                    '<img class="icon" src="{data.iconUrl}"/>' +
                    '<h4>{data.displayName}</h4>' +
                    '<p>{data.type}</p>' +
                    '</div>' +
                    '</tpl>' +
                    '</div>',

    setModalDialogData: function (model) {
        this.modelData = model[0].data;
        this.data = model[0];
        if (model) {
            var info = this.down('#modalDialog');
            if (info) {
                info.update(model);
            }

        }
    },

    doShow: function (selection) {
        this.setModalDialogData(selection);
        this.show();
    }
});