Ext.define('Admin.view.datadesigner.wizard.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.dataDesignerWizardToolbar',

    border: false,

    isNewGroup: true,

    initComponent: function () {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var leftGrp = {
            xtype: 'buttongroup',
            columns: 3,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Save',
                    action: 'saveUserstore',
                    itemId: 'save',
                    disabled: true,
                    iconCls: 'icon-save-24'
                }
            ]
        };

        var rightGrp = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Close',
                    action: 'closeWizard',
                    iconCls: 'icon-cancel-24'
                }
            ]
        };

        if (!this.isNew) {
            leftGrp.items.push({
                text: 'Delete',
                action: 'deleteUserstore',
                iconCls: 'icon-delete-user-24'
            });
            leftGrp.items.push({
                text: 'Synchronize',
                iconCls: 'icon-refresh',
                action: 'syncUserstore'
            });

        }

        this.items = [ leftGrp, '->', rightGrp ];
        this.callParent(arguments);
    }

});
