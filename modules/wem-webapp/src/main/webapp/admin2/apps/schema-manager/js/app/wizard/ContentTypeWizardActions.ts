module app_wizard {

    export class SaveContentTypeAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

    export class CloseContentTypeAction extends api_ui.Action {

        constructor(wizarPanel:api_app_wizard.WizardPanel, checkCanRemovePanel?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                new api_app_wizard.CloseWizardPanelEvent(wizarPanel, checkCanRemovePanel).fire();
            });
        }
    }
}