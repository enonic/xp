module app.wizard.action {

    export class UserItemWizardActions<USER_ITEM_TYPE extends api.Equitable> implements api.app.wizard.WizardActions<USER_ITEM_TYPE> {

        private save: api.ui.Action;

        private close: api.ui.Action;

        private delete: api.ui.Action;

        private actions: api.ui.Action[];

        constructor(wizardPanel: app.wizard.UserItemWizardPanel<USER_ITEM_TYPE>) {
            this.save = new api.app.wizard.SaveAction(wizardPanel);
            this.delete = new DeleteUserItemAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
            this.actions = [this.save, this.delete, this.close];
        }

        enableActionsForNew() {
            this.save.setEnabled(false);
            this.delete.setEnabled(false);
        }

        enableActionsForExisting() {
            this.save.setEnabled(true);
            this.delete.setEnabled(true);
        }

        getDeleteAction(): api.ui.Action {
            return this.delete;
        }

        getSaveAction(): api.ui.Action {
            return this.save;
        }

        getCloseAction(): api.ui.Action {
            return this.close;
        }

        getActions(): api.ui.Action[] {
            return this.actions;
        }
    }
}
