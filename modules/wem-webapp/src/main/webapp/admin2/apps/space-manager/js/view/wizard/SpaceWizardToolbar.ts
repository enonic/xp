module app_ui_wizard {
    export class SpaceWizardToolbar {
        ext:Ext_toolbar_Toolbar;

        constructor(public isNew?:bool = true) {

            var tb = new Ext.toolbar.Toolbar({
                cls: 'admin-toolbar',
                itemId: 'spaceWizardToolbar',
                border: false
            });
            this.ext = <Ext_toolbar_Toolbar> tb;

            var defaults = {
                scale: 'medium'
            };
            var saveBtn = new Ext.button.Button(Ext.apply({
                text: 'Save',
                action: 'saveSpace',
                itemId: 'save',
                disabled: true
            }, defaults));
            var deleteBtn = new Ext.button.Button(Ext.apply({
                text: 'Delete',
                disabled: this.isNew,
                action: 'deleteSpace'
            }, defaults));
            var duplicateBtn = new Ext.button.Button(Ext.apply({
                text: 'Duplicate',
                disabled: this.isNew
            }, defaults));
            var closeBtn = new Ext.button.Button(Ext.apply({
                text: 'Close',
                action: 'closeWizard'
            }, defaults));

            tb.add(saveBtn, deleteBtn, duplicateBtn, '->', closeBtn);
        }

    }
}
