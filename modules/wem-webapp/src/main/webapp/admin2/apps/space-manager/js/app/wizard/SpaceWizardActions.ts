module app_wizard {

    export class SaveSpaceAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

    export class DuplicateSpaceAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
        }
    }

    export class DeleteSpaceAction extends api_ui.Action {

        constructor() {
            super("Delete");
            this.setEnabled(false);
        }
    }

    export class CloseSpaceAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel, checkCanClose?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }

}
