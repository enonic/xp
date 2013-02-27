Ext.define('Admin.view.spaceAdmin.DeleteSpaceWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteSpaceWindow',

    //dialogTitle: 'Delete Space(s)',

    multipleTemplate: '<div class="admin-delete-user-confirmation-message">' +
                      '<div class="icon-question-mark-32 admin-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
                      '<div class="admin-left" style="margin-top:5px">Are you sure you want to delete {selectionLength} item(s)?</div>' +
                      '</div>',

    singleTemplate: '<div>' +
                    '<div class="admin-content-info clearfix">' +
                    '<div class="admin-content-photo west admin-left">' +
                    '<div class="photo-placeholder"><img src="{iconUrl}" alt="{name}"/></div>' +
                    '</div>' +
                    '<div class="admin-left">' +
                    '<h2>{displayName}</h2>' +
                    '<p>{description}</p>' +
                    '</div>' +
                    '</div>' +
                    '</div>',

    dialogTitle: undefined,
    dialogSubTitle: undefined,

    initComponent: function () {
        var me = this;
        this.items = [
            me.header('Delete Space(s)'),
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
                action: 'deleteSpace'
            }, {
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