module api.app.wizard {

    export class WizardStepsPanel extends api.ui.NavigatedPanelStrip {

        constructor(navigator: WizardStepNavigator) {
            super(navigator, "wizard-steps-panel");
        }

    }
}