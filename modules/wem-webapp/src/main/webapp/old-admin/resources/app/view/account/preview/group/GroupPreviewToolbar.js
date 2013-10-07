Ext.define('Admin.view.account.preview.group.GroupPreviewToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.groupPreviewToolbar',

    initComponent: function () {

        var leftGrp = {
            text: 'Edit',
            action: 'editGroup',
            items: []
        };

        if (!this.isRole) {
            leftGrp.items.push({
                text: 'Delete',
                action: 'deleteGroup'
            });
        }

        var rightGrp = {
            text: 'Close',
            action: 'closePreview',
            items: []
        };

        this.items = [ leftGrp, '->', rightGrp ];
        this.callParent(arguments);
    }

});
