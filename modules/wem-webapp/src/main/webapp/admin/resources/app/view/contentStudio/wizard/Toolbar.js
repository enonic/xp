Ext.define('Admin.view.contentStudio.wizard.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentStudioWizardToolbar',

    cls: 'admin-toolbar',

    border: false,

    isNewGroup: true,

    initComponent: function () {

        var leftGrp = {
            text: 'Save',
            action: 'saveContentType',
            itemId: 'save',
            disabled: false //true, TODO disable by default, enable when modified
        };

        var rightGrp = {
            text: 'Close',
            action: 'closeWizard'
        };

        if (!this.isNew) {
            leftGrp.items.push({
                text: 'Delete',
                action: 'deleteContentType'
            });
        }

        this.items = [ leftGrp, '->', rightGrp ];
        this.callParent(arguments);
    }

});
