Ext.define('Admin.view.spaceAdmin.DeleteSpaceWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteSpaceWindow',

    dialogTitle: 'Delete Space(s)',

    multipleTemplate: '<div class="admin-delete-user-confirmation-message">' +
                      '<div class="icon-question-mark-32 admin-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
                      '<div class="admin-left" style="margin-top:5px">Are you sure you want to delete {selectionLength} item(s)?</div>' +
                      '</div>',

    singleTemplate: '<div>' +
                    '<div class="admin-content-info clearfix">' +
                    '<div class="admin-content-photo west admin-left">' +
                    '<div class="photo-placeholder"><img src="{image_url}" alt="{name}"/></div>' +
                    '</div>' +
                    '<div class="admin-left">' +
                    '<h2>{displayName}</h2>' +
                    '<p>{description}</p>' +
                    '</div>' +
                    '</div>' +
                    '</div>',

    items: [
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
                    action: 'deleteSpace'
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    },

    doShow: function (selection) {
        if (Ext.isObject(selection) || (Ext.isArray(selection) && selection.length === 1)) {
            this.setDialogInfoTpl(this.singleTemplate);
            this.callParent(Ext.isArray(selection) ? selection : [selection]);
        } else {
            this.setDialogInfoTpl(this.multipleTemplate);
            this.callParent(
                [
                    {
                        data: {
                            selection: selection,
                            selectionLength: selection.length
                        }
                    }
                ]
            );
        }
    }
});