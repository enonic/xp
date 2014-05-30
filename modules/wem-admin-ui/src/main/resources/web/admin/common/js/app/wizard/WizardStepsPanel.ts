module api.app.wizard {

    export class WizardStepsPanel extends api.ui.NavigatedPanelStrip {

        constructor(navigator: WizardStepNavigator, scrollable?: api.dom.Element) {
            super(navigator, scrollable, "wizard-steps-panel");
        }

    }
}