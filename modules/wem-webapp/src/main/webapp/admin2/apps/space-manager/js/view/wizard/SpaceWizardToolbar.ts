module admin.ui {
    export class SpaceWizardToolbar {
        ext:Ext_toolbar_Toolbar;

        constructor(public isNew?:bool = true) {

            var tb = new Ext.toolbar.Toolbar({
                cls: 'admin-toolbar',
                itemId: 'spaceWizardToolbar',
                border: false
            });
            this.ext = <Ext_toolbar_Toolbar> tb;

            var saveBtn = new Ext.button.Button({
                text: 'Save',
                action: 'saveSpace',
                itemId: 'save',
                disabled: true,
                scale: 'medium'
            });
            var deleteBtn = new Ext.button.Button({
                text: 'Delete',
                disabled: this.isNew,
                action: 'deleteSpace',
                scale: 'medium'
            });
            var duplicateBtn = new Ext.button.Button({
                text: 'Duplicate',
                disabled: this.isNew,
                scale: 'medium'
            });
            var closeBtn = new Ext.button.Button({
                text: 'Close',
                action: 'closeWizard',
                scale: 'medium'
            });

            tb.add(saveBtn, deleteBtn, duplicateBtn, '->', closeBtn);
        }

    }
}
