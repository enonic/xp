Ext.define('Admin.view.account.preview.group.GroupPreviewToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.groupPreviewToolbar',

    initComponent: function () {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var leftGrp = {
            xtype: 'buttongroup',
            columns: 2,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Edit',
                    action: 'editGroup',
                    iconCls: 'icon-edit-generic'
                }
            ]
        };

        if (!this.isRole) {
            leftGrp.items.push({
                text: 'Delete',
                action: 'deleteGroup',
                iconCls: 'icon-delete-user-24'
            });
        }

        var rightGrp = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Close',
                    action: 'closePreview',
                    iconCls: 'icon-cancel-24'
                }
            ]
        };

        this.items = [ leftGrp, '->', rightGrp ];
        this.callParent(arguments);
    }

});
