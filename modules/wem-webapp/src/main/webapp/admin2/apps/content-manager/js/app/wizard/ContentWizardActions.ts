module app_wizard {

    export class SaveContentAction extends api_ui.Action {

        constructor() {
            super("Save");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DuplicateContentAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteContentAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class CloseContentAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel, checkCanClose?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }

}
