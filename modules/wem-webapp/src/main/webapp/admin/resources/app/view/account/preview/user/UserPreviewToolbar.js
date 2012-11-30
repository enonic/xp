Ext.define('Admin.view.account.preview.user.UserPreviewToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.userPreviewToolbar',

    initComponent: function () {

        var leftGrp = [];

        if (this.isEditable) {
            leftGrp.push(
                {
                    text: 'Edit',
                    action: 'editUser'
                },
                {
                    text: 'Delete',
                    action: 'deleteUser'
                },

                {
                    text: 'Change Password',
                    action: 'changePassword'
                }
            );
        }

        var rightGrp = {
            text: 'Close',
            action: 'closePreview'
        };

        this.items = leftGrp.concat('->', rightGrp);
        this.callParent(arguments);
    }

});
