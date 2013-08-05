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

        constructor(wizarPanel:api_app_wizard.WizardPanel, checkCanRemovePanel?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new api_app_wizard.CloseWizardPanelEvent(wizarPanel, checkCanRemovePanel).fire();
            });
        }
    }

}
