module app.wizard.action {

    export class ShowSplitEditAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Split");

            this.setEnabled(false);
            this.onExecuted(() => {
                new ShowSplitEditEvent().fire();
                wizard.showSplitEdit();

            });
        }
    }

}
