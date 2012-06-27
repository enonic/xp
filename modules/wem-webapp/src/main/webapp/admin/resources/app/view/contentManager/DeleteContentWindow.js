Ext.define('Admin.view.contentManager.DeleteContentWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteContentWindow',

    dialogTitle: 'Delete Content(s)',

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
                    itemId: 'deleteContentButton',
                    action: 'deleteContent'
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    },

    doShow: function (selection) {
        if (selection.length === 1) {
            this.setDialogInfoTpl(Templates.contentManager.deleteSingle);
            this.callParent([selection[0]]);
        } else {
            this.setDialogInfoTpl(Templates.contentManager.deleteMultiple);
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