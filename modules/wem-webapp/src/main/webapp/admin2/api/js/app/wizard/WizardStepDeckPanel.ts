module api_app_wizard {

    export class WizardStepDeckPanel extends api_ui.NavigatedDeckPanel {

        constructor(navigator:WizardStepNavigator) {
            super(navigator, "WizardStepDeckPanel");
            this.addClass("step-panel");
        }

    }
}