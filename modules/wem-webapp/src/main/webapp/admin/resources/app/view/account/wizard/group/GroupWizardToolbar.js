Ext.define('Admin.view.account.wizard.group.GroupWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.groupWizardToolbar',

    border: false,

    cls: 'admin-toolbar',

    isNewGroup: true,

    initComponent: function () {

        var leftGrp = [

            {
                text: 'Save',
                action: 'saveGroup',
                itemId: 'save',
                disabled: true
            }
        ];

        if (!this.isNew && !this.isRole) {
            leftGrp.push(
                {
                    text: 'Delete',
                    action: 'deleteGroup'
                }
            );
        }

        var rightGrp = {
            text: 'Close',
            action: 'closeWizard'
        };

        this.items = leftGrp.concat('->', rightGrp);
        this.callParent(arguments);
    }

});
