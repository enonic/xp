module api.app.wizard {

    export class WizardStepDeckPanel extends api.ui.NavigatedDeckPanel {

        constructor(navigator:WizardStepNavigator) {
            super(navigator, "step-panel");
        }

    }
}