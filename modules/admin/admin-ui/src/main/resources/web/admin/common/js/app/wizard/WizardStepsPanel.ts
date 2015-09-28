module api.app.wizard {

    export class WizardStepsPanel extends api.ui.panel.NavigatedPanelStrip {

        constructor(navigator: WizardStepNavigator, scrollable?: api.dom.Element) {
            super(navigator, scrollable, "wizard-steps-panel");
        }

    }
}