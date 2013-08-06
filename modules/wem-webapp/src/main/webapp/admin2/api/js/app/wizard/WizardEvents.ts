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

    export class DisplayNameChangedEvent extends WizardPanelEvent {

        constructor(wizardPanel:WizardPanel) {
            super('displayNameChanged', wizardPanel);
        }

        static on(handler:(event:DisplayNameChangedEvent) => void) {
            api_event.onEvent('displayNameChanged', handler);
        }
    }

    export class NameChangedEvent extends WizardPanelEvent {

        constructor(wizardPanel:WizardPanel) {
            super('nameChanged', wizardPanel);
        }

        static on(handler:(event:NameChangedEvent) => void) {
            api_event.onEvent('nameChanged', handler);
        }
    }
}