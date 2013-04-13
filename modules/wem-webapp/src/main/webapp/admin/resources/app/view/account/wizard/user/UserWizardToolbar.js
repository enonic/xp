Ext.define('Admin.view.account.wizard.user.UserWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.userWizardToolbar',

    border: false,

    cls: 'admin-toolbar',

    isNewUser: true,

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {

        var leftGrp = [
            {
                text: 'Save',
                action: 'saveUser',
                itemId: 'save',
                disabled: true
            }
        ];

        if (!this.isNewUser) {
            leftGrp.push(
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
            action: 'closeWizard'
        };

        this.items = leftGrp.concat([ '->', rightGrp ]);
        this.callParent(arguments);
    }

});
