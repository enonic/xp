module app_wizard {

    export class ContentWizardSaveDialog extends api_app_wizard.SaveBeforeCloseDialog {

        constructor() {
            super();

            this.getYesAction().addExecutionListener(() => {
                this.getWizardPanel().saveChanges();
                new app_browse.CloseContentEvent(this.getWizardPanel(), true).fire();
                this.close();
            });

            this.getNoAction().addExecutionListener(() => {
                new app_browse.CloseContentEvent(this.getWizardPanel(), false).fire();
                this.close();
            });
        }
    }
}