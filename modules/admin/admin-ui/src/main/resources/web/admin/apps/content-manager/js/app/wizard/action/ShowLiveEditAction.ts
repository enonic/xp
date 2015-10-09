module app.wizard.action {

    export class ShowLiveEditAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Live");

            this.setEnabled(false);
            this.setTitle("Show Page Editor");
            this.onExecuted(() => {
                wizard.showLiveEdit();
                new ShowLiveEditEvent().fire();
            });
        }
    }

}
