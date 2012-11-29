Ext.define('Admin.view.contentStudio.wizard.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentStudioWizardToolbar',

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
                    action: 'saveContentType',
                    itemId: 'save',
                    disabled: false, //true, TODO disable by default, enable when modified
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
                action: 'deleteContentType',
                iconCls: 'icon-delete-user-24'
            });
        }

        this.items = [ leftGrp, '->', rightGrp ];
        this.callParent(arguments);
    }

});
