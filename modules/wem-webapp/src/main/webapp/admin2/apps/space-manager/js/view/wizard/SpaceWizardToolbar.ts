module admin.ui {
    export class SpaceWizardToolbar {

        ext:Ext_toolbar_Toolbar;

        constructor(public isNew?:bool = true) {

            var tb = this.ext = new Ext.toolbar.Toolbar({
                cls: 'admin-toolbar',
                itemId: 'spaceWizardToolbar',
                border: false,
                defaults: {
                    scale: 'medium'
                },
            });

            var saveBtn = new Ext.button.Button({
                text: 'Save',
                action: 'saveSpace',
                itemId: 'save',
                disabled: true
            });
            var deleteBtn = new Ext.button.Button({
                text: 'Delete',
                disabled: this.isNew,
                action: 'deleteSpace'
            });
            var duplicateBtn = new Ext.button.Button({
                text: 'Duplicate',
                disabled: this.isNew
            });
            var closeBtn = new Ext.button.Button({
                text: 'Close',
                action: 'closeWizard'
            });

            tb.add(saveBtn, deleteBtn, duplicateBtn, '->', closeBtn);

        }

    }
}
