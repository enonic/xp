module api_app_wizard {

    export class WizardStepDeckPanel extends api_ui.NavigatedDeckPanel {

        constructor(navigator:WizardStepNavigator) {
            super(navigator, "WizardStepDeckPanel");
            this.addClass("step-panel");

            navigator.addListener({
                onStepShown: (step:api_ui.PanelNavigationItem) => {
                    this.showPanel(step.getIndex());
                }
            });
        }

    }
}