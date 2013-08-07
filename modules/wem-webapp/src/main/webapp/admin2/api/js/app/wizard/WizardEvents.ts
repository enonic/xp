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

    export class WizardStepEvent extends api_event.Event {

        constructor() {
            super("wizardStep");
        }

        static on(handler:(event:WizardStepEvent) => void) {
            api_event.onEvent('wizardStep', handler);
        }
    }
}