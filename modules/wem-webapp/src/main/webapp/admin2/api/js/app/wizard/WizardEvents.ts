module api_app_wizard {

    export class WizardPanelEvent extends api_event.Event {

        private wizardPanel:WizardPanel;

        constructor(name:string, wizardPanel:WizardPanel) {
            super(name);
            this.wizardPanel = wizardPanel;
        }

        getWizardPanel():WizardPanel {
            return this.wizardPanel;
        }
    }
}