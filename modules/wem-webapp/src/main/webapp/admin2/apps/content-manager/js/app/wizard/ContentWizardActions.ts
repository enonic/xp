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

        constructor(wizarPanel:api_app_wizard.WizardPanel, checkCanRemovePanel?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new api_app_wizard.CloseWizardPanelEvent(wizarPanel, checkCanRemovePanel).fire();
            });
        }
    }

}
