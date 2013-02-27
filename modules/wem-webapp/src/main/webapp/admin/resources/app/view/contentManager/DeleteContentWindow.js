Ext.define('Admin.view.contentManager.DeleteContentWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteContentWindow',

    dialogTitle: undefined,

    initComponent: function () {
        var me = this;
        this.items = [
            me.header('Delete content(s)'),
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
                action: 'deleteContent'
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
        console.log("Model");
        console.log(model);
        this.modelData = model[0].data;
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