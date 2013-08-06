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

    export class CloseWizardPanelEvent extends WizardPanelEvent {

        private checkCanRemovePanel:bool;

        constructor(wizardPanel:WizardPanel, checkCanRemovePanel?:bool = true) {
            super('closeWizardPanel', wizardPanel);
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:CloseWizardPanelEvent) => void) {
            api_event.onEvent('closeWizardPanel', handler);
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